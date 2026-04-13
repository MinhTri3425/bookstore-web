// CHỈ SHOW NHỮNG ĐOẠN BỊ CONFLICT ĐÃ FIX

// ===== FIELD =====
private final ShippingService shippingService;
private final CouponUsageRepository couponUsageRepository;


// ===== CONSTRUCTOR =====
public OrderServiceImpl(
        CustomerOrderRepository customerOrderRepository,
        PaymentRepository paymentRepository,
        UserRepository userRepository,
        ShippingService shippingService,
        CouponUsageRepository couponUsageRepository) {

    this.customerOrderRepository = customerOrderRepository;
    this.paymentRepository = paymentRepository;
    this.userRepository = userRepository;
    this.shippingService = shippingService;
    this.couponUsageRepository = couponUsageRepository;
}


// ===== CANCEL ORDER =====
order.setStatus(CustomerOrder.OrderStatus.CANCELLED);

// 🔥 GIỮ CẢ 2
shippingService.handleOrderCancelled(id);
restoreCouponUsage(order);


// ===== UPDATE ORDER STATUS =====
if (!isAllowedTransition(order.getStatus(), status)) {
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
            "Invalid order status transition: " + order.getStatus() + " -> " + status);
}

// 🔥 GIỮ validate COMPLETED
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

// 🔥 CANCEL từ admin
if (status == CustomerOrder.OrderStatus.CANCELLED) {
    shippingService.handleOrderCancelled(id);
    restoreCouponUsage(order);
}

customerOrderRepository.save(order);


// ❌ XÓA TOÀN BỘ METHOD NÀY (rất quan trọng)
/// public void updateShipping(...) { ... }


// ===== SHIPPER OPTIONS (XÓA) =====
// ❌ XÓA getShipperOptions()
// vì bạn đã dùng ShipperService ở controller


// ===== findAdminByExactOrderId =====
private Page<CustomerOrder> findAdminByExactOrderId(
        String orderId,
        CustomerOrder.OrderStatus status,
        Pageable pageable) {

    try {
        Long parsedId = Long.valueOf(orderId);
        Optional<CustomerOrder> orderOptional = customerOrderRepository.findListItemById(parsedId);

        if (orderOptional.isPresent()) {
            CustomerOrder order = orderOptional.get();

            if (status == null || order.getStatus() == status) {
                return new PageImpl<>(List.of(order), pageable, 1);
            }
        }
    } catch (NumberFormatException ignored) {}

    return new PageImpl<>(Collections.emptyList(), pageable, 0);
}


// ===== toItemDto =====
String title = item.getBook() != null && item.getBook().getTitle() != null
        ? item.getBook().getTitle()
        : "Untitled book";

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
                .build()
);


// ===== SHIPPER NAME =====
dto.setShipperName(
        shipping != null && shipping.getShipper() != null && shipping.getShipper().getName() != null
                ? shipping.getShipper().getName()
                : NOT_AVAILABLE);


// ===== toUserDto =====
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


// ===== isAllowedTransition =====
private boolean isAllowedTransition(CustomerOrder.OrderStatus current,
                                   CustomerOrder.OrderStatus target) {

    if (current == null || target == null || current == target) return false;

    return switch (current) {
        case PENDING -> target == CustomerOrder.OrderStatus.CONFIRMED
                || target == CustomerOrder.OrderStatus.CANCELLED;
        case CONFIRMED -> target == CustomerOrder.OrderStatus.COMPLETED
                || target == CustomerOrder.OrderStatus.CANCELLED;
        case COMPLETED, CANCELLED -> false;
    };
}


// ===== formatAmount =====
NumberFormat fmt = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
fmt.setMinimumFractionDigits(0);
fmt.setMaximumFractionDigits(2);
return fmt.format(amount) + " VND";