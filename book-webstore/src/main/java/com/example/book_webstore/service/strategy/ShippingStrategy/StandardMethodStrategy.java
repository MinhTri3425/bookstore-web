package com.example.book_webstore.service.strategy.ShippingStrategy;

import java.math.BigDecimal;
import com.example.book_webstore.model.Shipping;

public class StandardMethodStrategy implements ShippingCostStrategy {
    @Override
    public BigDecimal calculateShippingCost(Long orderId) {
        // Logic to calculate standard shipping cost, e.g., a flat rate or based on
        // distance
        return new BigDecimal("10000"); // Example flat rate for standard shipping
    }

    @Override
    public Shipping.ShippingMethod getMethod() {
        return Shipping.ShippingMethod.STANDARD;
    }

}
