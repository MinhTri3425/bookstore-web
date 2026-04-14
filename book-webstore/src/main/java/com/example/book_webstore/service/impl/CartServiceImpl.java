package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.CartItemDTO;
import com.example.book_webstore.dto.AddressDTO;
import com.example.book_webstore.model.Address;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.BookImage;
import com.example.book_webstore.model.Cart;
import com.example.book_webstore.model.CartItem;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.OrderItem;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.AddressRepository;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.CartItemRepository;
import com.example.book_webstore.repository.CartRepository;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.repository.PaymentRepository;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.repository.CouponRepository;
import com.example.book_webstore.service.CartService;
import com.example.book_webstore.service.payment.strategy.PaymentStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartServiceImpl implements CartService {
    private static final String VN_PHONE_REGEX = "^(0|\\+84)(3|5|7|8|9)\\d{8}$";

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final PaymentStrategyResolver paymentStrategyResolver;
    private final CouponRepository couponRepository;

    public CartServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            CustomerOrderRepository customerOrderRepository,
            PaymentRepository paymentRepository,
            ShippingRepository shippingRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            PaymentStrategyResolver paymentStrategyResolver,
            CouponRepository couponRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.bookRepository = bookRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
        this.paymentStrategyResolver = paymentStrategyResolver;
        this.couponRepository = couponRepository;
    }

    @Override
    public CartDTO getOrCreateCart(Long cartId) {
        Cart cart;
        if (cartId == null) {
            cart = cartRepository.save(new Cart());
        } else {
            cart = cartRepository.findById(cartId).orElseGet(() -> cartRepository.save(new Cart()));
        }
        return toCartDTO(cart);
    }

    @Override
    public CartDTO addToCart(Long cartId, Long bookId, int quantity) {
        int safeQuantity = Math.max(quantity, 1);
        Cart cart = getOrCreateEntity(cartId);
        Book book = bookRepository.findByIdWithImages(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setCart(cart);
                    newItem.setBook(book);
                    newItem.setQuantity(0);
                    return newItem;
                });

        item.setQuantity(item.getQuantity() + safeQuantity);
        cartItemRepository.save(item);
        return toCartDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    public CartDTO removeFromCart(Long cartId, Long bookId) {
        Cart cart = getOrCreateEntity(cartId);
        cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId).ifPresent(cartItemRepository::delete);
        return toCartDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    public CartDTO updateItemQuantity(Long cartId, Long bookId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0");
        }

        Cart cart = getOrCreateEntity(cartId);
        CartItem item = cartItemRepository.findByCartIdAndBookId(cart.getId(), bookId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng"));

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return toCartDTO(cartRepository.findById(cart.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public Long checkoutSelectedItems(Long cartId,
            List<Long> selectedBookIds,
            String customerEmail,
            Long selectedAddressId,
            String receiverName,
            String phoneNumber,
            String note,
            Payment.PaymentMethod paymentMethod,
            String shippingMethod,
            String couponCode) {

        // --- 1. Validate và lấy dữ liệu cơ bản (Giữ nguyên) ---
        if (selectedBookIds == null || selectedBookIds.isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm");
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null)
            throw new IllegalArgumentException("Người dùng không tồn tại");

        // --- 2. Khởi tạo Order (Gán dữ liệu thô) ---
        CustomerOrder order = new CustomerOrder();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(CustomerOrder.OrderStatus.PENDING);
        order.setCustomer(customer);
        order.setReceiverName(receiverName.trim());
        order.setAddress(resolveDeliveryAddress(customer, selectedAddressId));
        order.setPhoneNumber(normalizePhoneNumber(phoneNumber));
        order.setNote(note);

        // QUAN TRỌNG: Gán Coupon từ mã khách nhập vào để refreshOrderTotal có cái mà
        // tính
        if (couponCode != null && !couponCode.isBlank()) {
            couponRepository.findByCodeIgnoreCase(couponCode.trim())
                    .ifPresent(order::setCoupon);
        }
        customerOrderRepository.save(order);

        // --- 3. Tạo OrderItems và tính Subtotal tạm thời ---
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal subtotalAmount = BigDecimal.ZERO;
        for (CartItem cartItem : filterSelectedItems(cartId, selectedBookIds)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setBookTitle(cartItem.getBook().getTitle());
            orderItems.add(orderItem);
            subtotalAmount = subtotalAmount
                    .add(cartItem.getBook().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }
        order.setItems(orderItems);

        // --- 4. Khởi tạo Shipping (Gán phương thức khách chọn) ---
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        // Chuyển chuỗi "FAST"/"ECONOMY" từ JSP thành Enum
        shipping.setMethod(Shipping.ShippingMethod.valueOf(shippingMethod.toUpperCase()));
        shipping.setStatus(Shipping.ShippingStatus.PENDING);
        shipping.setCustomerAddress(order.getAddress());
        shipping.setCustomerName(order.getReceiverName());
        shipping.setCustomerPhone(order.getPhoneNumber());
        shipping.setCost(BigDecimal.ZERO); // Tạm để 0, tí nữa Strategy sẽ tính lại
        shippingRepository.save(shipping);
        order.setShipping(shipping);

        // --- 5. Khởi tạo Payment với giá tạm tính ---
        Payment payment = paymentStrategyResolver.resolve(paymentMethod)
                .createPayment(order, customer, subtotalAmount);
        paymentRepository.save(payment);
        order.setPayment(payment);

        // Lưu lại toàn bộ thông tin Order để chờ Refresh
        customerOrderRepository.save(order);
        cartItemRepository.deleteAll(filterSelectedItems(cartId, selectedBookIds));

        return order.getId();
    }

    private List<CartItem> filterSelectedItems(Long cartId, List<Long> selectedBookIds) {
        if (selectedBookIds == null || selectedBookIds.isEmpty()) {
            return new ArrayList<>();
        }
        Set<Long> selectedBookIdSet = new HashSet<>(selectedBookIds);
        return cartItemRepository.findByCartId(cartId).stream()
                .filter(item -> item.getBook() != null && selectedBookIdSet.contains(item.getBook().getId()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getOrderPaymentAmount(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("Ma don hang khong hop le");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thanh toan cua don hang"));
        return payment.getAmount() == null ? BigDecimal.ZERO : payment.getAmount();
    }

    @Override
    public void updatePaymentStatus(Long orderId, boolean paid) {
        if (orderId == null) {
            throw new IllegalArgumentException("Ma don hang khong hop le");
        }

        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Khong tim thay thanh toan cua don hang"));

        if (paid) {
            if (payment.getStatus() != Payment.PaymentStatus.PAID) {
                payment.setStatus(Payment.PaymentStatus.PAID);
                payment.setPaidAt(LocalDateTime.now());
                paymentRepository.save(payment);
            }

            return;
        }

        if (payment.getStatus() == Payment.PaymentStatus.PENDING) {
            payment.setStatus(Payment.PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressDTO> getUserAddresses(String customerEmail) {
        if (customerEmail == null || customerEmail.isBlank()) {
            return List.of();
        }

        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null || customer.getAddresses() == null) {
            return List.of();
        }

        return customer.getAddresses().stream()
                .filter(Objects::nonNull)
                .map(this::toAddressDTO)
                .toList();
    }

    @Override
    public AddressDTO addUserAddress(String customerEmail,
            String street,
            String ward,
            String district,
            String city) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("Không tìm thấy thông tin tài khoản");
        }
        if (isBlank(street) || isBlank(ward) || isBlank(district) || isBlank(city)) {
            throw new IllegalArgumentException("Vui lòng nhập đầy đủ thông tin địa chỉ mới");
        }

        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new IllegalArgumentException("Không tìm thấy người dùng đăng nhập");
        }

        Address newAddress = new Address();
        newAddress.setStreet(street.trim());
        newAddress.setWard(ward.trim());
        newAddress.setDistrict(district.trim());
        newAddress.setCity(city.trim());
        Address savedAddress = addressRepository.save(newAddress);

        customer.getAddresses().add(savedAddress);
        userRepository.save(customer);

        return toAddressDTO(savedAddress);
    }

    @Override
    @Transactional(readOnly = true)
    public long getItemCount(Long cartId) {
        if (cartId == null) {
            return 0;
        }
        return cartItemRepository.findByCartId(cartId).stream().mapToLong(CartItem::getQuantity).sum();
    }

    private Cart getOrCreateEntity(Long cartId) {
        if (cartId == null) {
            return cartRepository.save(new Cart());
        }
        return cartRepository.findById(cartId).orElseGet(() -> cartRepository.save(new Cart()));
    }

    @Transactional(readOnly = true)
    private CartDTO toCartDTO(Cart cart) {
        CartDTO dto = new CartDTO();
        dto.setId(cart.getId());

        List<CartItemDTO> itemDTOs = cartItemRepository.findByCartId(cart.getId()).stream().map(item -> {
            CartItemDTO itemDTO = new CartItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setBook(toBookDTO(item.getBook()));
            return itemDTO;
        }).collect(Collectors.toList());

        dto.setItems(itemDTOs);
        return dto;
    }

    private BookDTO toBookDTO(Book book) {
        Book hydrated = bookRepository.findByIdWithImages(book.getId()).orElse(book);
        BookDTO dto = new BookDTO();
        dto.setId(hydrated.getId());
        dto.setTitle(hydrated.getTitle());
        dto.setPrice(hydrated.getPrice());
        dto.setDescription(hydrated.getDescription());
        dto.setImages(hydrated.getImages().stream()
                .sorted(Comparator.comparingInt(BookImage::getSortOrder))
                .map(img -> {
                    BookImageDTO imageDTO = new BookImageDTO();
                    imageDTO.setId(img.getId());
                    imageDTO.setUrl(img.getUrl());
                    imageDTO.setAltText(img.getAltText());
                    imageDTO.setSortOrder(img.getSortOrder());
                    return imageDTO;
                })
                .collect(Collectors.toList()));
        return dto;
    }

    private AddressDTO toAddressDTO(Address address) {
        return new AddressDTO(
                address.getId(),
                address.getStreet(),
                address.getCity(),
                address.getDistrict(),
                address.getWard());
    }

    private String resolveDeliveryAddress(User customer, Long selectedAddressId) {
        if (selectedAddressId == null) {
            throw new IllegalArgumentException("Vui lòng chọn địa chỉ đã lưu trước khi đặt hàng");
        }

        Address selectedAddress = customer.getAddresses().stream()
                .filter(address -> address != null && selectedAddressId.equals(address.getId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Địa chỉ đã chọn không hợp lệ"));

        return formatAddress(selectedAddress.getStreet(), selectedAddress.getWard(), selectedAddress.getDistrict(),
                selectedAddress.getCity());
    }

    private String formatAddress(String street, String ward, String district, String city) {
        return String.join(", ",
                safeValue(street),
                safeValue(ward),
                safeValue(district),
                safeValue(city));
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private BigDecimal calculateCheckoutTotalPlaceholder(BigDecimal subtotal,
            Shipping.ShippingMethod shippingMethod,
            String couponCode) {
        BigDecimal safeSubtotal = subtotal == null ? BigDecimal.ZERO : subtotal;
        BigDecimal shippingFee = calculateShippingFeePlaceholder(shippingMethod);
        BigDecimal couponDiscount = calculateCouponDiscountPlaceholder(couponCode, safeSubtotal);

        BigDecimal payable = safeSubtotal.add(shippingFee).subtract(couponDiscount);
        return payable.max(BigDecimal.ZERO);
    }

    // tính phí vận chuyển dựa trên phương thức vận chuyển, placeholder này sẽ được
    // thay thế bằng logic tính phí thực tế từ backend sau này
    private BigDecimal calculateShippingFeePlaceholder(Shipping.ShippingMethod shippingMethod) {
        // TODO: integrate real shipping fee calculation from backend
        // configuration/service.
        if (shippingMethod == null) {
            return BigDecimal.ZERO;
        }
        return switch (shippingMethod) {
            case STANDARD -> BigDecimal.valueOf(15000);
            case FAST -> BigDecimal.valueOf(30000);
            case ECONOMY -> BigDecimal.valueOf(10000);
        };
    }

    // tính phí giảm giá
    private BigDecimal calculateCouponDiscountPlaceholder(String couponCode, BigDecimal subtotal) {
        // TODO: validate coupon and calculate discount from coupon backend.
        if (couponCode == null || couponCode.isBlank() || subtotal == null) {
            return BigDecimal.ZERO;
        }
        return BigDecimal.ZERO;
    }

    private String normalizePhoneNumber(String phoneNumber) {
        return phoneNumber == null ? "" : phoneNumber.replaceAll("\\s+", "").trim();
    }
}
