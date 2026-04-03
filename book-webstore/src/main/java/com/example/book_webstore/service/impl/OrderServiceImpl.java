package com.example.book_webstore.service.impl;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
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
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.OrderItem;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.repository.PaymentRepository;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.service.OrderService;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final String NOT_AVAILABLE = "N/A";

    private final CustomerOrderRepository customerOrderRepository;
    private final PaymentRepository paymentRepository;
    private final ShippingRepository shippingRepository;

    public OrderServiceImpl(
            CustomerOrderRepository customerOrderRepository,
            PaymentRepository paymentRepository,
            ShippingRepository shippingRepository) {
        this.customerOrderRepository = customerOrderRepository;
        this.paymentRepository = paymentRepository;
        this.shippingRepository = shippingRepository;
    }

    @Override
    public Page<CustomerOrderDTO> getOrderPage(String orderId, CustomerOrder.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), Sort.by(Sort.Direction.DESC, "id"));
        String normalizedOrderId = normalize(orderId);
        Page<CustomerOrder> orderPage;

        if (normalizedOrderId != null) {
            orderPage = findByExactOrderId(normalizedOrderId, status, pageable);
        } else if (status != null) {
            orderPage = customerOrderRepository.findByStatus(status, pageable);
        } else {
            orderPage = customerOrderRepository.findAllBy(pageable);
        }

        Map<Long, Payment> paymentsByOrderId = paymentRepository.findByOrderIdIn(extractOrderIds(orderPage.getContent()))
                .stream()
                .filter(payment -> payment.getOrder() != null)
                .collect(Collectors.toMap(payment -> payment.getOrder().getId(), Function.identity(), (left, right) -> left));

        Map<Long, Shipping> shippingsByOrderId = shippingRepository.findByOrderIdIn(extractOrderIds(orderPage.getContent()))
                .stream()
                .filter(shipping -> shipping.getOrder() != null)
                .collect(Collectors.toMap(shipping -> shipping.getOrder().getId(), Function.identity(), (left, right) -> left));

        List<CustomerOrderDTO> rows = orderPage.getContent().stream()
                .map(order -> toCustomerOrderDto(order, paymentsByOrderId.get(order.getId()), shippingsByOrderId.get(order.getId()), false))
                .toList();

        return new PageImpl<>(rows, pageable, orderPage.getTotalElements());
    }

    @Override
    public CustomerOrderDTO getOrderDetail(Long id) {
        CustomerOrder order = customerOrderRepository.findDetailById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Payment payment = paymentRepository.findByOrderId(id).orElse(null);
        Shipping shipping = shippingRepository.findByOrderId(id).orElse(null);
        return toCustomerOrderDto(order, payment, shipping, true);
    }

    private Page<CustomerOrder> findByExactOrderId(String orderId, CustomerOrder.OrderStatus status, Pageable pageable) {
        Long parsedId;
        try {
            parsedId = Long.valueOf(orderId);
        } catch (NumberFormatException ex) {
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

    private List<Long> extractOrderIds(List<CustomerOrder> orders) {
        if (orders.isEmpty()) {
            return List.of();
        }
        return orders.stream().map(CustomerOrder::getId).toList();
    }

    private CustomerOrderDTO toCustomerOrderDto(CustomerOrder order, Payment payment, Shipping shipping, boolean includeItems) {
        CustomerOrderDTO dto = new CustomerOrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus());
        dto.setUserId(payment != null && payment.getUser() != null ? String.valueOf(payment.getUser().getId()) : null);
        dto.setStatusCssClass(toStatusCssClass(order.getStatus()));
        dto.setCreatedAtDisplay(formatDateTime(order.getCreatedAt()));
        dto.setItemCount(order.getItems().size());
        dto.setTotalAmountDisplay(formatAmount(payment != null ? payment.getAmount() : null));
        dto.setCustomerName(payment != null && payment.getUser() != null && payment.getUser().getName() != null
                ? payment.getUser().getName()
                : NOT_AVAILABLE);
        dto.setShippingMethod(shipping != null && shipping.getMethod() != null
                ? shipping.getMethod().name()
                : NOT_AVAILABLE);
        dto.setItems(includeItems
                ? order.getItems().stream().map(this::toItemDto).toList()
                : List.of());
        return dto;
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
            dto.setBook(new BookDTO(
                    item.getBook().getId(),
                    item.getBook().getTitle(),
                    item.getBook().getIsbn(),
                    item.getBook().getDescription(),
                    item.getBook().getPrice(),
                    item.getBook().getAuthor() != null ? String.valueOf(item.getBook().getAuthor().getId()) : null,
                    item.getBook().getCategory() != null ? String.valueOf(item.getBook().getCategory().getId()) : null,
                    List.of()));
        }
        return dto;
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? NOT_AVAILABLE : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String formatAmount(BigDecimal amount) {
        if (amount == null) {
            return NOT_AVAILABLE;
        }
        NumberFormat numberFormat = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);
        return numberFormat.format(amount) + " VND";
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
