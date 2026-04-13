package com.example.book_webstore.strategy.coupon;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.book_webstore.model.Coupon.CouponType;

@Component
public class CouponStrategyFactory {

    private final Map<CouponType, CouponCalculationStrategy> strategies = new EnumMap<>(CouponType.class);

    @Autowired
    public CouponStrategyFactory(List<CouponCalculationStrategy> strategyList) {
        for (CouponCalculationStrategy strategy : strategyList) {
            strategies.put(strategy.getType(), strategy);
        }
    }

    public CouponCalculationStrategy getStrategy(CouponType type) {
        CouponCalculationStrategy strategy = strategies.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("Unsupported coupon type: " + type);
        }
        return strategy;
    }
}
