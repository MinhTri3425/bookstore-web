package com.example.book_webstore.dto;

import java.time.LocalDateTime;
import com.example.book_webstore.model.Shipping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShippingDTO {
    private Long id;
    private Shipping.ShippingStatus status;
    private ShipperDTO shipper;
    private CustomerOrderDTO order;
    private LocalDateTime createdAt;
}
