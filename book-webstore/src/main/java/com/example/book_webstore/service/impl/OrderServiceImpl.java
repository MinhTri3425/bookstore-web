package com.example.book_webstore.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.book_webstore.dto.*;
import com.example.book_webstore.model.*;
import com.example.book_webstore.repository.*;
import com.example.book_webstore.service.OrderService;
import com.example.book_webstore.service.ShippingService;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String NOT_AVAILABLE = "N/A";

    private final CustomerOrderRepository customerOrderRepository;
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    private final ShippingService shippingService;
    private final CouponUsageRepository couponUsageRepository;

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

    // ================= COUPON =================
    private void restoreCouponUsage(CustomerOrder order) {
        if (order.getCoupon() != null && order.getCustomer() != null) {
            couponUsageRepository
                    .findByCouponIdAndCustomerId(
                            order.getCoupon().getId(),
                            order.getCustomer().getId())
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

    // ================= ADMIN =================
    @Override
    public Page<CustomerOrderDTO> getAdminOrderPage(String orderId,
            CustomerOrder.OrderStatus status,
            int page, int size) {

        Pageable pageable = buildPageable(page, size);
        String normalized = normalize(orderId);

        if (normalized != null) {
            return findAdminByExactOrderId(normalized, status, pageable)
                    .map(o -> toCustomerOrderDto(o, false));
        }

        Page<CustomerOrder> pageData = (status != null)
                ? customerOrderRepository.findByStatus(status, pageable)
                : customerOrderRepository.findAllBy(pageable);

        return pageData.map(o -> toCustomerOrderDto(o, false));
    }

    @Override
    public CustomerOrderDTO getAdminOrderDetail(Long id) {
        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        return toCustomerOrderDto(order, true);
    }

    // ================= CUSTOMER =================
    @Override
    public Page<CustomerOrderDTO> getCustomerOrderPage(Long customerId,
            CustomerOrder.OrderStatus status,
            int page, int size) {

        if (customerId == null)
            return Page.empty();

        Pageable pageable = buildPageable(page, size);

        return customerOrderRepository
                .findCustomerOrders(customerId, status, pageable)
                .map(o -> toCustomerOrderDto(o, false));
    }

    @Override
    public CustomerOrderDTO getCustomerOrderDetail(Long customerId, Long id) {
        requireCustomerId(customerId);

        CustomerOrder order = customerOrderRepository
                .findCustomerDetailById(customerId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        return toCustomerOrderDto(order, true);
    }

    // ================= CANCEL =================
    @Override
    @Transactional
    public void cancelCustomerOrder(Long customerId, Long id) {

        requireCustomerId(customerId);

        CustomerOrder order = customerOrderRepository
                .findCustomerDetailById(customerId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (order.getStatus() != CustomerOrder.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Chỉ đơn hàng đang chờ duyệt mới có thể hủy");
        }

        attachCustomerFromPaymentIfMissing(order);

        order.setStatus(CustomerOrder.OrderStatus.CANCELLED);

        restoreCouponUsage(order);
        shippingService.handleOrderCancelled(id);

        customerOrderRepository.save(order);
    }

    // ================= UPDATE STATUS =================
    @Override
    @Transactional
    public void updateOrderStatus(Long id, CustomerOrder.OrderStatus status) {

        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is required");
        }

        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (!isAllowedTransition(order.getStatus(), status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid order status transition: " + order.getStatus() + " -> " + status);
        }

        // Validate COMPLETED
        if (status == CustomerOrder.OrderStatus.COMPLETED) {
            if (order.getPayment() == null ||
                    order.getPayment().getStatus() != Payment.PaymentStatus.PAID) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Payment must be PAID");
            }

            if (order.getShipping() == null ||
                    order.getShipping().getStatus() != Shipping.ShippingStatus.DELIVERED) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Shipping must be DELIVERED");
            }
        }

        if (status == CustomerOrder.OrderStatus.CANCELLED) {
            restoreCouponUsage(order);
            shippingService.handleOrderCancelled(id);
        }

        attachCustomerFromPaymentIfMissing(order);
        order.setStatus(status);

        customerOrderRepository.save(order);
    }

    // ================= PAYMENT =================
    @Override
    @Transactional
    public void updatePayment(Long id, Payment.PaymentStatus status) {

        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment status is required");
        }

        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Payment payment = ensurePayment(order);

        payment.setStatus(status);
        payment.setPaidAt(status == Payment.PaymentStatus.PAID ? LocalDateTime.now() : null);

        paymentRepository.save(payment);
    }

    // ================= OPTIONS =================
    @Override
    public List<UserDTO> getCustomerOptions() {
        return userRepository.findByRoleOrderByNameAsc(User.Role.USER)
                .stream().map(this::toUserDto).toList();
    }

    // ================= UTIL =================
    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1),
                Sort.by(Sort.Direction.DESC, "id"));
    }

    private Page<CustomerOrder> findAdminByExactOrderId(String orderId,
            CustomerOrder.OrderStatus status,
            Pageable pageable) {
        try {
            Long id = Long.valueOf(orderId);
            Optional<CustomerOrder> opt = customerOrderRepository.findListItemById(id);

            if (opt.isPresent()) {
                CustomerOrder o = opt.get();
                if (status == null || o.getStatus() == status) {
                    return new PageImpl<>(List.of(o), pageable, 1);
                }
            }
        } catch (Exception ignored) {
        }

        return new PageImpl<>(Collections.emptyList(), pageable, 0);
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

        dto.setItemCount(order.getItems().stream()
                .mapToInt(OrderItem::getQuantity).sum());

        dto.setTotalAmountDisplay(formatAmount(resolveAmount(order, payment)));

        dto.setCustomerName(customer != null ? customer.getName() : NOT_AVAILABLE);

        dto.setShippingMethod(shipping != null && shipping.getMethod() != null
                ? shipping.getMethod().name()
                : NOT_AVAILABLE);

        dto.setShippingStatusDisplay(shipping != null && shipping.getStatus() != null
                ? shipping.getStatus().name()
                : NOT_AVAILABLE);

        dto.setShipperName(shipping != null && shipping.getShipper() != null
                ? shipping.getShipper().getName()
                : NOT_AVAILABLE);

        dto.setPaymentStatusDisplay(payment != null && payment.getStatus() != null
                ? payment.getStatus().name()
                : NOT_AVAILABLE);

        dto.setCanCancel(order.getStatus() == CustomerOrder.OrderStatus.PENDING);
        dto.setCanConfirm(order.getStatus() == CustomerOrder.OrderStatus.PENDING);
        dto.setCanComplete(order.getStatus() == CustomerOrder.OrderStatus.CONFIRMED);
        dto.setCanAdminCancel(order.getStatus() == CustomerOrder.OrderStatus.PENDING
                || order.getStatus() == CustomerOrder.OrderStatus.CONFIRMED);

        dto.setItems(includeItems
                ? order.getItems().stream().map(this::toItemDto).toList()
                : List.of());

        return dto;
    }

    private BigDecimal resolveAmount(CustomerOrder order, Payment payment) {
        if (payment != null && payment.getAmount() != null)
            return payment.getAmount();

        return order.getItems().stream()
                .filter(i -> i.getBook() != null && i.getBook().getPrice() != null)
                .map(i -> i.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();

        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setBookTitle(item.getBook() != null
                ? item.getBook().getTitle()
                : "Untitled book");

        if (item.getBook() != null) {
            var b = item.getBook();

            dto.setBook(BookDTO.builder()
                    .id(b.getId())
                    .title(b.getTitle())
                    .price(b.getPrice())
                    .authorName(b.getAuthor() != null ? b.getAuthor().getName() : null)
                    .build());
        }

        return dto;
    }

    private Payment ensurePayment(CustomerOrder order) {
        if (order.getPayment() != null)
            return order.getPayment();

        Payment p = new Payment();
        p.setOrder(order);
        p.setAmount(resolveAmount(order, null));
        p.setPaymentMethod(Payment.PaymentMethod.CASH);
        p.setStatus(Payment.PaymentStatus.PENDING);
        p.setUser(resolveCustomer(order, null));

        order.setPayment(p);
        return paymentRepository.save(p);
    }

    private User resolveCustomer(CustomerOrder order, Payment payment) {
        if (order.getCustomer() != null)
            return order.getCustomer();
        return payment != null ? payment.getUser() : null;
    }

    private void attachCustomerFromPaymentIfMissing(CustomerOrder order) {
        if (order.getCustomer() == null &&
                order.getPayment() != null &&
                order.getPayment().getUser() != null) {

            order.setCustomer(order.getPayment().getUser());
        }
    }

    private UserDTO toUserDto(User user) {
        if (user == null)
            return null;

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                null,
                user.getName(),
                user.getPhoneNumber(),
                user.getRole(),
                List.of(),
                user.isShipper());
    }

    private PaymentDTO toPaymentDto(Payment payment) {
        if (payment == null)
            return null;

        return new PaymentDTO(
                payment.getId(),
                payment.getAmount(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getPaidAt(),
                payment.getUser() != null ? String.valueOf(payment.getUser().getId()) : null,
                null);
    }

    private ShippingDTO toShippingDto(Shipping shipping) {
        if (shipping == null)
            return null;

        return new ShippingDTO(
                shipping.getId(),
                shipping.getCost(),
                shipping.getStatus(),
                shipping.getMethod(),
                shipping.getShipper() != null ? shipping.getShipper().getId() : null,
                shipping.getOrder() != null ? shipping.getOrder().getId() : null,
                shipping.getCreatedAt(),
                shipping.getCustomerName(),
                shipping.getCustomerAddress(),
                shipping.getCustomerPhone(),
                shipping.getNote());
    }

    private String normalize(String value) {
        if (value == null)
            return null;
        value = value.trim();
        return value.isEmpty() ? null : value;
    }

    private void requireCustomerId(Long id) {
        if (id == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    private boolean isAllowedTransition(CustomerOrder.OrderStatus current,
            CustomerOrder.OrderStatus target) {

        if (current == null || target == null || current == target)
            return false;

        return switch (current) {
            case PENDING -> target == CustomerOrder.OrderStatus.CONFIRMED
                    || target == CustomerOrder.OrderStatus.CANCELLED;
            case CONFIRMED -> target == CustomerOrder.OrderStatus.COMPLETED
                    || target == CustomerOrder.OrderStatus.CANCELLED;
            default -> false;
        };
    }

    private String formatDateTime(LocalDateTime dt) {
        return dt == null ? NOT_AVAILABLE : DATE_TIME_FORMATTER.format(dt);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null)
            return NOT_AVAILABLE;

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        return nf.format(amount) + " VND";
    }

    private String toStatusCssClass(CustomerOrder.OrderStatus status) {
        if (status == null)
            return "status-neutral";

        return switch (status) {
            case PENDING -> "status-pending";
            case CONFIRMED -> "status-confirmed";
            case COMPLETED -> "status-completed";
            case CANCELLED -> "status-cancelled";
        };
    }

    @Override
    public List<CustomerOrderDTO> getOrdersReadyForPickup() {
        return customerOrderRepository
                .findByStatusAndShippingIsNull(CustomerOrder.OrderStatus.CONFIRMED)
                .stream()
                .map(o -> toCustomerOrderDto(o, false))
                .collect(Collectors.toList());
    }
}