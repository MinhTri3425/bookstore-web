package com.example.book_webstore.service.strategy.shipping.impl;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;

import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.strategy.shipping.ShippingCostStrategy;

@Service
public class FastMethodStrategy implements ShippingCostStrategy {
    @Override
    public BigDecimal calculateShippingCost(Long orderId) {
        // Logic to calculate fast shipping cost, e.g., a flat rate or based on distance
        return new BigDecimal("20000"); // Example flat rate for fast shipping
    }

    @Override
    public Shipping.ShippingMethod getMethod() {
        return Shipping.ShippingMethod.FAST;
    }
}
