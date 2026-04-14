package com.example.book_webstore.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.example.book_webstore.model.Shipping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShippingDTO {
    private Long id;
    private BigDecimal cost;
    private Shipping.ShippingStatus status;
    private Shipping.ShippingMethod method;
    private Long shipperId;
    private Long orderId;
    private LocalDateTime createdAt;
    private String customerName;
    private String customerAddress;
    private String customerPhone;
    private String note;

    public String getCreatedAtDisplay() {
        if (this.createdAt == null)
            return "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return this.createdAt.format(formatter);
    }
}
