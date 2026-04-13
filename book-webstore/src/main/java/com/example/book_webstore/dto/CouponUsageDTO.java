package com.example.book_webstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponUsageDTO {
    private Long id;
    private Long couponId;
    private String couponCode;
    private Long userId;
    private String customerEmail;
    private String customerName;
    private int usageCount;
}
