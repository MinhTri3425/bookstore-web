package com.example.book_webstore.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

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
import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.OrderItem;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.repository.PaymentRepository;
import com.example.book_webstore.repository.UserRepository;
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

    public OrderServiceImpl(
            CustomerOrderRepository customerOrderRepository,
            PaymentRepository paymentRepository,
            UserRepository userRepository,
            ShippingService shippingService) {
        this.customerOrderRepository = customerOrderRepository;
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
        this.shippingService = shippingService;
    }

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

    @Override
    @Transactional
    public void cancelCustomerOrder(Long customerId, Long id) {
        requireCustomerId(customerId);
        CustomerOrder order = customerOrderRepository.findCustomerDetailById(customerId, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (order.getStatus() != CustomerOrder.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Chỉ đơn hàng đang chờ duyệt mới có thể hủy");
        }

        attachCustomerFromPaymentIfMissing(order);
        order.setStatus(CustomerOrder.OrderStatus.CANCELLED);

        // THÔNG BÁO HỦY: Giải phóng Shipper nếu lỡ có người đã nhận đơn này
        shippingService.handleOrderCancelled(id);

        customerOrderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long id, CustomerOrder.OrderStatus status) {
        if (status == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is required");
        }

        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        if (!isAllowedTransition(order.getStatus(), status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid order status transition: " + order.getStatus() + " -> " + status);
        }

        // --- XỬ LÝ HỦY ĐƠN TỪ ADMIN ---
        if (status == CustomerOrder.OrderStatus.CANCELLED) {
            shippingService.handleOrderCancelled(id);
        }

        // ĐÃ XÓA createAutoShipping TẠI ĐÂY
        // Đơn hàng CONFIRMED sẽ đợi Shipper chủ động vào nhận qua acceptOrder()

        attachCustomerFromPaymentIfMissing(order);
        order.setStatus(status);
        customerOrderRepository.save(order);
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

    @Override
    public List<UserDTO> getCustomerOptions() {
        return userRepository.findByRoleOrderByNameAsc(User.Role.USER).stream()
                .map(this::toUserDto)
                .toList();
    }

    // --- Private Helper Methods ---

    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "id"));
    }

    private Page<CustomerOrder> findAdminByExactOrderId(String orderId, CustomerOrder.OrderStatus status,
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
        } catch (NumberFormatException ignored) {
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
        dto.setItemCount(order.getItems().stream().mapToInt(OrderItem::getQuantity).sum());
        dto.setTotalAmountDisplay(formatAmount(resolveAmount(order, payment)));
        dto.setCustomerName(customer != null && customer.getName() != null ? customer.getName() : NOT_AVAILABLE);

        dto.setShippingMethod(
                shipping != null && shipping.getMethod() != null ? shipping.getMethod().name() : NOT_AVAILABLE);
        dto.setShippingStatusDisplay(
                shipping != null && shipping.getStatus() != null ? shipping.getStatus().name() : NOT_AVAILABLE);
        dto.setShipperName(
                shipping != null && shipping.getShipper() != null ? shipping.getShipper().getName() : NOT_AVAILABLE);

        dto.setPaymentStatusDisplay(
                payment != null && payment.getStatus() != null ? payment.getStatus().name() : NOT_AVAILABLE);

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
        return order.getItems().stream()
                .filter(item -> item.getBook() != null && item.getBook().getPrice() != null)
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private OrderItemDTO toItemDto(OrderItem item) {
        OrderItemDTO dto = new OrderItemDTO();
        dto.setId(item.getId());
        dto.setQuantity(item.getQuantity());
        dto.setBookTitle(item.getBook() != null ? item.getBook().getTitle() : "Untitled book");

        if (item.getBook() != null) {
            var book = item.getBook();
            dto.setBook(BookDTO.builder()
                    .id(book.getId()).title(book.getTitle()).price(book.getPrice())
                    .authorName(book.getAuthor() != null ? book.getAuthor().getName() : null)
                    .build());
        }
        return dto;
    }

    private Payment ensurePayment(CustomerOrder order) {
        if (order.getPayment() != null)
            return order.getPayment();
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(resolveAmount(order, null));
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setUser(resolveCustomer(order, null));
        order.setPayment(payment);
        return paymentRepository.save(payment);
    }

    private User resolveCustomer(CustomerOrder order, Payment payment) {
        if (order.getCustomer() != null)
            return order.getCustomer();
        return payment != null ? payment.getUser() : null;
    }

    private void attachCustomerFromPaymentIfMissing(CustomerOrder order) {
        if (order.getCustomer() == null && order.getPayment() != null && order.getPayment().getUser() != null) {
            order.setCustomer(order.getPayment().getUser());
        }
    }

    private UserDTO toUserDto(User user) {
        if (user == null)
            return null;
        return new UserDTO(user.getId(), user.getEmail(), null, user.getName(), user.getPhoneNumber(), user.getRole(),
                List.of(), user.isShipper());
    }

    private PaymentDTO toPaymentDto(Payment payment) {
        if (payment == null)
            return null;
        return new PaymentDTO(payment.getId(), payment.getAmount(), payment.getPaymentMethod(), payment.getStatus(),
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
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void requireCustomerId(Long customerId) {
        if (customerId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer id is required");
    }

    private boolean isAllowedTransition(CustomerOrder.OrderStatus currentStatus,
            CustomerOrder.OrderStatus targetStatus) {
        if (currentStatus == null || targetStatus == null || currentStatus == targetStatus)
            return false;

        return switch (currentStatus) {
            case PENDING -> targetStatus == CustomerOrder.OrderStatus.CONFIRMED
                    || targetStatus == CustomerOrder.OrderStatus.CANCELLED;
            case CONFIRMED -> targetStatus == CustomerOrder.OrderStatus.COMPLETED
                    || targetStatus == CustomerOrder.OrderStatus.CANCELLED;
            case CANCELLED -> false;
            case COMPLETED -> false;
        };
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? NOT_AVAILABLE : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null)
            return NOT_AVAILABLE;
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        return numberFormat.format(amount) + " VND";
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
        // Chỉ lấy những đơn hàng đã CONFIRMED và chưa có bản ghi Shipping nào
        List<CustomerOrder> orders = customerOrderRepository
                .findByStatusAndShippingIsNull(CustomerOrder.OrderStatus.CONFIRMED);

        return orders.stream()
                .map(order -> toCustomerOrderDto(order, false))
                .collect(Collectors.toList());
    }
}