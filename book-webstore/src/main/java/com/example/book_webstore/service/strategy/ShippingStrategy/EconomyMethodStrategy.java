package com.example.book_webstore.service.strategy.ShippingStrategy;

import java.math.BigDecimal;

import com.example.book_webstore.model.Shipping;

public class EconomyMethodStrategy implements ShippingCostStrategy {
    @Override
    public BigDecimal calculateShippingCost(Long orderId) {
        // Logic to calculate economy shipping cost, e.g., a flat rate or based on
        // distance
        return new BigDecimal("5000"); // Example flat rate for economy shipping
    }

    @Override
    public Shipping.ShippingMethod getMethod() {
        return Shipping.ShippingMethod.ECONOMY;
    }

}
