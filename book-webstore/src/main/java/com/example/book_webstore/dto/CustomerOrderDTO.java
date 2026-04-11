package com.example.book_webstore.dto;

import com.example.book_webstore.model.CustomerOrder;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private CustomerOrder.OrderStatus status;
    private String userId;
    private List<OrderItemDTO> items;
    private UserDTO customer;
    private PaymentDTO payment;
    private ShippingDTO shipping;
    private String statusCssClass;
    private String createdAtDisplay;
    private int itemCount;
    private String totalAmountDisplay;
    private String customerName;
    private String shippingMethod;
    private String paymentStatusDisplay;
    private String shippingStatusDisplay;
    private String shipperName;
    private String couponCode;
    private String subtotalAmountDisplay;
    private String discountAmountDisplay;
    private boolean canCancel;
    private boolean canConfirm;
    private boolean canComplete;
    private boolean canAdminCancel;

}
