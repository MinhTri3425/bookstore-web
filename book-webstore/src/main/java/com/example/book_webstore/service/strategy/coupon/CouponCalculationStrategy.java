package com.example.book_webstore.service.strategy.coupon;

import java.math.BigDecimal;
import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.Coupon.CouponType;

public interface CouponCalculationStrategy {
    BigDecimal calculateDiscount(Coupon coupon, BigDecimal applicableSubtotal);

    CouponType getType();
}
