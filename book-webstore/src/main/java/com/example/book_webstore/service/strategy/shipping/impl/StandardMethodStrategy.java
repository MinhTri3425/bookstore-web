package com.example.book_webstore.service.strategy.shipping.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.strategy.shipping.ShippingCostStrategy;

@Service
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
