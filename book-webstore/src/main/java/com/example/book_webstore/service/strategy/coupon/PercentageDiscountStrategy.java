package com.example.book_webstore.service.strategy.coupon;

import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springframework.stereotype.Component;

import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.Coupon.CouponType;

@Component
public class PercentageDiscountStrategy implements CouponCalculationStrategy {

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal applicableSubtotal) {
        return applicableSubtotal
                .multiply(coupon.getValue())
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
    }

    @Override
    public CouponType getType() {
        return CouponType.PERCENTAGE;
    }
}
