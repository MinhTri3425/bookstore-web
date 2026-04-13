package com.example.book_webstore.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.dto.OrderItemDTO;
import com.example.book_webstore.dto.PaymentDTO;
import com.example.book_webstore.dto.ShipperDTO;
import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.OrderItem;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipper;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.CouponUsageRepository;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.repository.PaymentRepository;
import com.example.book_webstore.repository.ShipperRepository;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.service.OrderService;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String NOT_AVAILABLE = "N/A";

    private final CustomerOrderRepository customerOrderRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;
    private final UserRepository userRepository;
    private final ShipperRepository shipperRepository;
    private final CouponUsageRepository couponUsageRepository;

    public OrderServiceImpl(
            CustomerOrderRepository customerOrderRepository,
            PaymentRepository paymentRepository,
            ShippingRepository shippingRepository,
            UserRepository userRepository,
            ShipperRepository shipperRepository,
            CouponUsageRepository couponUsageRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
        this.userRepository = userRepository;
        this.shipperRepository = shipperRepository;
        this.couponUsageRepository = couponUsageRepository;
    }

    // ─── Private helpers ────────────────────────────────────────────────────────

    /**
     * Khôi phục lượt dùng coupon khi đơn hàng bị hủy.
     * Nếu usageCount về 0 thì xoá hẳn bản ghi CouponUsage.
     */
    private void restoreCouponUsage(CustomerOrder order) {
        if (order.getCoupon() != null && order.getCustomer() != null) {
            couponUsageRepository
                .findByCouponIdAndCustomerId(order.getCoupon().getId(), order.getCustomer().getId())
                .ifPresent(usage -> {
                    int next = usage.getUsageCount() - 1;
                    if (next <= 0) {
                        couponUsageRepository.delete(usage);
                    } else {
                        usage.setUsageCount(next);
                        couponUsageRepository.save(usage);
                    }
                });
        }
    }

    // ─── Admin queries ───────────────────────────────────────────────────────────

    @Override
    public Page<CustomerOrderDTO> getAdminOrderPage(String orderId, CustomerOrder.OrderStatus status, int page,
            int size) {
        Pageable pageable = buildPageable(page, size);
        String normalizedOrderId = normalize(orderId);
        Page<CustomerOrder> orderPage;

        if (normalizedOrderId != null) {
            orderPage = findAdminByExactOrderId(normalizedOrderId, status, pageable);
        } else if (status != null) {
            orderPage = customerOrderRepository.findByStatus(status, pageable);
        } else {
            orderPage = customerOrderRepository.findAllBy(pageable);
        }

        return orderPage.map(order -> toCustomerOrderDto(order, false));
    }

    @Override
    public CustomerOrderDTO getAdminOrderDetail(Long id) {
        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return toCustomerOrderDto(order, true);
    }

    // ─── Customer queries ────────────────────────────────────────────────────────

    @Override
    public Page<CustomerOrderDTO> getCustomerOrderPage(Long customerId, CustomerOrder.OrderStatus status, int page,
            int size) {
        if (customerId == null) {
            return Page.empty(buildPageable(page, size));
        }

        Pageable pageable = buildPageable(page, size);
        Page<CustomerOrder> orderPage = customerOrderRepository.findCustomerOrders(customerId, status, pageable);
        return orderPage.map(order -> toCustomerOrderDto(order, false));
    }

    @Override
    public CustomerOrderDTO getCustomerOrderDetail(Long customerId, Long id) {
        requireCustomerId(customerId);
        CustomerOrder order = customerOrderRepository.findCustomerDetailById(customerId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        return toCustomerOrderDto(order, true);
    }

    // ─── Customer actions ────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void cancelCustomerOrder(Long customerId, Long id) {
        requireCustomerId(customerId);
        CustomerOrder order = customerOrderRepository.findCustomerDetailById(customerId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() != CustomerOrder.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can be cancelled");
        }

        attachCustomerFromPaymentIfMissing(order);
        order.setStatus(CustomerOrder.OrderStatus.CANCELLED);
        restoreCouponUsage(order);
        customerOrderRepository.save(order);
    }

    // ─── Admin actions ───────────────────────────────────────────────────────────

    @Override
    @Transactional
    public void updateOrderStatus(Long id, CustomerOrder.OrderStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is required");
        }

        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        CustomerOrder.OrderStatus currentStatus = order.getStatus();
        if (!isAllowedTransition(currentStatus, status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid order status transition: " + currentStatus + " -> " + status);
        }

        if (status == CustomerOrder.OrderStatus.COMPLETED) {
            Payment payment = order.getPayment();
            Shipping shipping = order.getShipping();
            if (payment == null || payment.getStatus() != Payment.PaymentStatus.PAID) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot complete order: Payment must be PAID.");
            }
            if (shipping == null || shipping.getStatus() != Shipping.ShippingStatus.DELIVERED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Cannot complete order: Shipping must be DELIVERED.");
            }
        }

        attachCustomerFromPaymentIfMissing(order);
        order.setStatus(status);
        if (status == CustomerOrder.OrderStatus.CANCELLED) {
            restoreCouponUsage(order);
        }
        customerOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateShipping(Long id, Shipping.ShippingStatus status, Long shipperId) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shipping status is required");
        }

        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Shipping shipping = ensureShipping(order);
        if (order.getStatus() == CustomerOrder.OrderStatus.PENDING && status != Shipping.ShippingStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Pending orders cannot move to active shipping states");
        }

        shipping.setStatus(status);
        if (shipperId != null) {
            Shipper shipper = shipperRepository.findById(shipperId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Shipper not found"));
            shipping.setShipper(shipper);
        }

        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void updatePayment(Long id, Payment.PaymentStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment status is required");
        }

        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Payment payment = ensurePayment(order);
        payment.setStatus(status);
        payment.setPaidAt(status == Payment.PaymentStatus.PAID ? LocalDateTime.now() : null);
        paymentRepository.save(payment);
    }

    // ─── Options ─────────────────────────────────────────────────────────────────

    @Override
    public List<UserDTO> getCustomerOptions() {
        return userRepository.findByRoleOrderByNameAsc(User.Role.USER).stream()
                .map(this::toUserDto)
                .toList();
    }

    @Override
    public List<ShipperDTO> getShipperOptions() {
        return shipperRepository.findAll().stream()
                .sorted((a, b) -> a.getName().compareToIgnoreCase(b.getName()))
                .map(s -> new ShipperDTO(s.getId(), s.getName(), s.getPhone()))
                .toList();
    }

    // ─── Private utilities ────────────────────────────────────────────────────────

    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "id"));
    }

    private Page<CustomerOrder> findAdminByExactOrderId(String orderId, CustomerOrder.OrderStatus status,
            Pageable pageable) {
        Long parsedId = parseOrderId(orderId);
        if (parsedId == null) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        Optional<CustomerOrder> orderOptional = customerOrderRepository.findListItemById(parsedId);
        if (orderOptional.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        CustomerOrder order = orderOptional.get();
        if (status != null && order.getStatus() != status) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        if (pageable.getOffset() > 0) {
            return new PageImpl<>(Collections.emptyList(), pageable, 1);
        }

        return new PageImpl<>(List.of(order), pageable, 1);
    }

    private Long parseOrderId(String orderId) {
        try {
            return Long.valueOf(orderId);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private CustomerOrderDTO toCustomerOrderDto(CustomerOrder order, boolean includeItems) {
        attachCustomerFromPaymentIfMissing(order);

        Payment payment = order.getPayment();
        Shipping shipping = order.getShipping();
        User customer = resolveCustomer(order, payment);

        CustomerOrderDTO dto = new CustomerOrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setUserId(customer != null ? String.valueOf(customer.getId()) : null);
        dto.setCustomer(toUserDto(customer));
        dto.setPayment(toPaymentDto(payment));
        dto.setShipping(toShippingDto(shipping));
        dto.setStatusCssClass(toStatusCssClass(order.getStatus()));
        dto.setCreatedAtDisplay(formatDateTime(order.getCreatedAt()));
        dto.setItemCount(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());

        BigDecimal subtotal = resolveSubtotal(order);
        BigDecimal discountAmount = order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount();
        dto.setSubtotalAmountDisplay(formatAmount(subtotal));
        dto.setDiscountAmountDisplay(formatAmount(discountAmount));
        dto.setTotalAmountDisplay(formatAmount(resolveAmount(order, payment)));
        dto.setCouponCode(order.getCoupon() != null ? order.getCoupon().getCode() : NOT_AVAILABLE);

        dto.setCustomerName(customer != null && customer.getName() != null ? customer.getName() : NOT_AVAILABLE);
        dto.setShippingMethod(
                shipping != null && shipping.getMethod() != null ? shipping.getMethod().name() : NOT_AVAILABLE);
        dto.setPaymentStatusDisplay(
                payment != null && payment.getStatus() != null ? payment.getStatus().name() : NOT_AVAILABLE);
        dto.setShippingStatusDisplay(
                shipping != null && shipping.getStatus() != null ? shipping.getStatus().name() : NOT_AVAILABLE);
        dto.setShipperName(shipping != null && shipping.getShipper() != null && shipping.getShipper().getName() != null
                ? shipping.getShipper().getName()
                : NOT_AVAILABLE);

        dto.setCanCancel(order.getStatus() == CustomerOrder.OrderStatus.PENDING);
        dto.setCanConfirm(order.getStatus() == CustomerOrder.OrderStatus.PENDING);
        dto.setCanComplete(order.getStatus() == CustomerOrder.OrderStatus.CONFIRMED);
        dto.setCanAdminCancel(order.getStatus() == CustomerOrder.OrderStatus.PENDING
                || order.getStatus() == CustomerOrder.OrderStatus.CONFIRMED);
        dto.setItems(includeItems ? order.getItems().stream().map(this::toItemDto).toList() : List.of());
        return dto;
    }

    private BigDecimal resolveAmount(CustomerOrder order, Payment payment) {
        if (payment != null && payment.getAmount() != null) {
            return payment.getAmount();
        }
        BigDecimal discount = order.getDiscountAmount() == null ? BigDecimal.ZERO : order.getDiscountAmount();
        return resolveSubtotal(order).subtract(discount);
    }

    private BigDecimal resolveSubtotal(CustomerOrder order) {
        return order.getItems().stream()
                .filter(item -> item.getBook() != null && item.getBook().getPrice() != null)
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        String title = item.getBook() != null && item.getBook().getTitle() != null
                ? item.getBook().getTitle()
                : "Untitled book";

        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setBookTitle(title);

        if (item.getBook() != null) {
            var book = item.getBook();
            dto.setBook(
                    BookDTO.builder()
                            .id(book.getId())
                            .title(book.getTitle())
                            .isbn(book.getIsbn())
                            .description(book.getDescription())
                            .price(book.getPrice())
                            .authorId(book.getAuthor() != null ? book.getAuthor().getId() : null)
                            .authorName(book.getAuthor() != null ? book.getAuthor().getName() : null)
                            .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                            .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                            .images(List.of())
                            .stock(null)
                            .build());
        }

        return dto;
    }

    private User resolveCustomer(CustomerOrder order, Payment payment) {
        if (order.getCustomer() != null) {
            return order.getCustomer();
        }
        return payment != null ? payment.getUser() : null;
    }

    private void attachCustomerFromPaymentIfMissing(CustomerOrder order) {
        if (order.getCustomer() == null && order.getPayment() != null && order.getPayment().getUser() != null) {
            order.setCustomer(order.getPayment().getUser());
        }
    }

    private UserDTO toUserDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getEmail(),
                null,
                user.getName(),
                user.getPhoneNumber(),
                user.getRole(),
                List.of(),
                user.isShipper()
        );
    }

    private PaymentDTO toPaymentDto(Payment payment) {
        if (payment == null) {
            return null;
        }
        return new PaymentDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getUser() != null ? String.valueOf(payment.getUser().getId()) : null,
                payment.getOrder() != null ? String.valueOf(payment.getOrder().getId()) : null);
    }

    private ShippingDTO toShippingDto(Shipping shipping) {
        if (shipping == null) {
            return null;
        }
        return new ShippingDTO(
                shipping.getId(),
                shipping.getStatus(),
                shipping.getMethod(),
                shipping.getShipper() != null ? String.valueOf(shipping.getShipper().getId()) : null,
                shipping.getOrder() != null ? String.valueOf(shipping.getOrder().getId()) : null,
                shipping.getCreatedAt());
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void requireCustomerId(Long customerId) {
        if (customerId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer id is required");
        }
    }

    private boolean isAllowedTransition(CustomerOrder.OrderStatus current, CustomerOrder.OrderStatus target) {
        if (current == null || target == null || current == target) {
            return false;
        }
        return switch (current) {
            case PENDING -> target == CustomerOrder.OrderStatus.CONFIRMED
                    || target == CustomerOrder.OrderStatus.CANCELLED;
            case CONFIRMED -> target == CustomerOrder.OrderStatus.COMPLETED
                    || target == CustomerOrder.OrderStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
    }

    private Shipping ensureShipping(CustomerOrder order) {
        if (order.getShipping() != null) {
            return order.getShipping();
        }

        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setMethod(Shipping.ShippingMethod.STANDARD);
        shipping.setStatus(Shipping.ShippingStatus.PENDING);
        shipping.setCreatedAt(LocalDateTime.now());
        order.setShipping(shipping);
        return shippingRepository.save(shipping);
    }

    private Payment ensurePayment(CustomerOrder order) {
        if (order.getPayment() != null) {
            return order.getPayment();
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(resolveAmount(order, null));
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setUser(resolveCustomer(order, null));
        order.setPayment(payment);
        return paymentRepository.save(payment);
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? NOT_AVAILABLE : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return NOT_AVAILABLE;
        }
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        fmt.setMinimumFractionDigits(0);
        fmt.setMaximumFractionDigits(2);
        return fmt.format(amount) + " VND";
    }

    private String toStatusCssClass(CustomerOrder.OrderStatus status) {
        if (status == null) {
            return "status-neutral";
        }
        return switch (status) {
            case PENDING -> "status-pending";
            case CONFIRMED -> "status-confirmed";
            case COMPLETED -> "status-completed";
            case CANCELLED -> "status-cancelled";
        };
    }
}
