package com.example.book_webstore.strategy.coupon;

import java.math.BigDecimal;
import org.springframework.stereotype.Component;

import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.Coupon.CouponType;

@Component
public class FixedDiscountStrategy implements CouponCalculationStrategy {

    @Override
    public BigDecimal calculateDiscount(Coupon coupon, BigDecimal applicableSubtotal) {
        return coupon.getValue();
    }

    @Override
    public CouponType getType() {
        return CouponType.FIXED;
    }
}
