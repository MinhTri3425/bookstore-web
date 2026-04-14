package com.example.book_webstore.dto;

import com.example.book_webstore.model.Coupon;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutCouponOptionDTO {
    private Long id;
    private String code;
    private Coupon.CouponType type;
    private Coupon.CouponTarget target;
    private String valueDisplay;
    private boolean eligible;
    private String reason;
}
