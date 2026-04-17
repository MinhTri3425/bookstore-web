package com.example.book_webstore.service.strategy.shipping;

import java.math.BigDecimal;

import com.example.book_webstore.model.Shipping;

public interface ShippingCostStrategy {
    BigDecimal calculateShippingCost(Long orderId);

    Shipping.ShippingMethod getMethod();
}
