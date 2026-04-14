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
import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.CouponUsage;
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
import com.example.book_webstore.repository.CouponUsageRepository;
import com.example.book_webstore.service.CartService;
import com.example.book_webstore.service.payment.strategy.PaymentStrategyResolver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
    private final CouponUsageRepository couponUsageRepository;

    public CartServiceImpl(CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            BookRepository bookRepository,
            CustomerOrderRepository customerOrderRepository,
            PaymentRepository paymentRepository,
            ShippingRepository shippingRepository,
            UserRepository userRepository,
            AddressRepository addressRepository,
            PaymentStrategyResolver paymentStrategyResolver,
            CouponRepository couponRepository,
            CouponUsageRepository couponUsageRepository) {
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
        this.couponUsageRepository = couponUsageRepository;
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
            String productCouponCode,
            String shippingCouponCode) {

        // --- 1. Validate và lấy dữ liệu cơ bản (Giữ nguyên) ---
        if (selectedBookIds == null || selectedBookIds.isEmpty())
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm");
        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null)
            throw new IllegalArgumentException("Người dùng không tồn tại");

        List<CartItem> selectedItems = filterSelectedItems(cartId, selectedBookIds);
        if (selectedItems.isEmpty()) {
            throw new IllegalArgumentException("Không có sản phẩm hợp lệ để checkout");
        }

        // --- 2. Khởi tạo Order (Gán dữ liệu thô) ---
        CustomerOrder order = new CustomerOrder();
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(CustomerOrder.OrderStatus.PENDING);
        order.setCustomer(customer);
        order.setReceiverName(receiverName.trim());
        order.setAddress(resolveDeliveryAddress(customer, selectedAddressId));
        order.setPhoneNumber(normalizePhoneNumber(phoneNumber));
        order.setNote(note);

        Shipping.ShippingMethod selectedShippingMethod = resolveShippingMethod(shippingMethod);

        BigDecimal subtotalAmount = selectedItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal shippingAmount = calculateShippingFeePlaceholder(selectedShippingMethod)
                .setScale(2, RoundingMode.HALF_UP);

        Coupon productCoupon = resolveCouponByCodeAndTarget(productCouponCode, Coupon.CouponTarget.PRODUCT);
        BigDecimal productDiscount = calculateProductCouponDiscount(productCoupon, selectedItems, subtotalAmount,
                customer);

        Coupon shippingCoupon = resolveCouponByCodeAndTarget(shippingCouponCode, Coupon.CouponTarget.SHIPPING);
        BigDecimal shippingDiscount = calculateShippingCouponDiscount(shippingCoupon, shippingAmount, subtotalAmount,
                customer);

        order.setCoupon(productCoupon);
        order.setShippingCoupon(shippingCoupon);
        order.setDiscountAmount(productDiscount);
        order.setShippingDiscountAmount(shippingDiscount);

        customerOrderRepository.save(order);

        // --- 3. Tạo OrderItems và tính Subtotal tạm thời ---
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : selectedItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(cartItem.getBook());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setBookTitle(cartItem.getBook().getTitle());
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        // --- 4. Khởi tạo Shipping (Gán phương thức khách chọn) ---
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setMethod(selectedShippingMethod);
        shipping.setStatus(Shipping.ShippingStatus.PENDING);
        shipping.setCustomerAddress(order.getAddress());
        shipping.setCustomerName(order.getReceiverName());
        shipping.setCustomerPhone(order.getPhoneNumber());
        shipping.setCost(shippingAmount);
        shippingRepository.save(shipping);
        order.setShipping(shipping);

        // --- 5. Khởi tạo Payment với giá tạm tính ---
        BigDecimal paymentAmount = subtotalAmount
                .subtract(productDiscount)
                .add(shippingAmount)
                .subtract(shippingDiscount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        Payment payment = paymentStrategyResolver.resolve(paymentMethod)
                .createPayment(order, customer, paymentAmount);
        paymentRepository.save(payment);
        order.setPayment(payment);

        // Lưu lại toàn bộ thông tin Order để chờ Refresh
        customerOrderRepository.save(order);

        incrementUsageIfPresent(productCoupon, customer);
        if (shippingCoupon != null
                && (productCoupon == null || !shippingCoupon.getId().equals(productCoupon.getId()))) {
            incrementUsageIfPresent(shippingCoupon, customer);
        }

        cartItemRepository.deleteAll(selectedItems);

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

    private Shipping.ShippingMethod resolveShippingMethod(String shippingMethod) {
        if (isBlank(shippingMethod)) {
            return Shipping.ShippingMethod.STANDARD;
        }
        try {
            return Shipping.ShippingMethod.valueOf(shippingMethod.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Phương thức vận chuyển không hợp lệ");
        }
    }

    private Coupon resolveCouponByCodeAndTarget(String code, Coupon.CouponTarget target) {
        if (isBlank(code)) {
            return null;
        }
        Coupon coupon = couponRepository.findByCodeIgnoreCase(code.trim())
                .orElseThrow(() -> new IllegalArgumentException("Mã coupon không tồn tại: " + code));
        if (coupon.getTarget() != target) {
            throw new IllegalArgumentException("Coupon " + coupon.getCode() + " không đúng loại " + target.name());
        }
        return coupon;
    }

    private BigDecimal calculateProductCouponDiscount(Coupon coupon,
            List<CartItem> selectedItems,
            BigDecimal subtotal,
            User customer) {
        if (coupon == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        validateCouponUsage(coupon, customer, subtotal);

        List<CartItem> applicableItems = selectedItems;
        if (coupon.getApplicableBooks() != null && !coupon.getApplicableBooks().isEmpty()) {
            Set<Long> applicableBookIds = coupon.getApplicableBooks().stream().map(Book::getId)
                    .collect(Collectors.toSet());
            applicableItems = selectedItems.stream()
                    .filter(item -> item.getBook() != null && applicableBookIds.contains(item.getBook().getId()))
                    .toList();
            if (applicableItems.isEmpty()) {
                throw new IllegalArgumentException(
                        "Coupon " + coupon.getCode() + " không áp dụng cho sản phẩm đã chọn");
            }
        }

        BigDecimal applicableSubtotal = applicableItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = calculateCouponDiscountByType(coupon, applicableSubtotal);
        return discount.min(applicableSubtotal).min(subtotal).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateShippingCouponDiscount(Coupon coupon,
            BigDecimal shippingAmount,
            BigDecimal subtotal,
            User customer) {
        if (coupon == null) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        validateCouponUsage(coupon, customer, subtotal);
        BigDecimal discount = calculateCouponDiscountByType(coupon, shippingAmount);
        return discount.min(shippingAmount).max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateCouponDiscountByType(Coupon coupon, BigDecimal baseAmount) {
        BigDecimal discount;
        if (coupon.getType() == Coupon.CouponType.FIXED) {
            discount = coupon.getValue();
        } else {
            discount = baseAmount.multiply(coupon.getValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
            discount = coupon.getMaxDiscountValue();
        }

        return discount;
    }

    private void validateCouponUsage(Coupon coupon, User customer, BigDecimal subtotal) {
        if (!coupon.isActive()) {
            throw new IllegalArgumentException("Coupon " + coupon.getCode() + " đang bị vô hiệu hóa");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
            throw new IllegalArgumentException("Coupon " + coupon.getCode() + " chưa đến thời gian áp dụng");
        }
        if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
            throw new IllegalArgumentException("Coupon " + coupon.getCode() + " đã hết hạn");
        }

        if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new IllegalArgumentException("Đơn hàng chưa đủ điều kiện tối thiểu cho coupon " + coupon.getCode());
        }

        CouponUsage usage = couponUsageRepository.findByCouponIdAndCustomerId(coupon.getId(), customer.getId())
                .orElse(null);
        int usedCount = usage != null ? usage.getUsageCount() : 0;
        if (usedCount >= coupon.getMaxUsePerUser()) {
            throw new IllegalArgumentException("Bạn đã dùng hết lượt coupon " + coupon.getCode());
        }

        if (coupon.getTotalUsageLimit() != null) {
            long totalUsage = couponUsageRepository.sumUsageCountByCouponId(coupon.getId());
            if (totalUsage >= coupon.getTotalUsageLimit()) {
                throw new IllegalArgumentException("Coupon " + coupon.getCode() + " đã hết lượt sử dụng");
            }
        }
    }

    private void incrementUsageIfPresent(Coupon coupon, User customer) {
        if (coupon == null || customer == null) {
            return;
        }

        CouponUsage usage = couponUsageRepository.findByCouponIdAndCustomerId(coupon.getId(), customer.getId())
                .orElseGet(() -> {
                    CouponUsage created = new CouponUsage();
                    created.setCoupon(coupon);
                    created.setCustomer(customer);
                    created.setUsageCount(0);
                    return created;
                });

        usage.setUsageCount(usage.getUsageCount() + 1);
        couponUsageRepository.save(usage);
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
