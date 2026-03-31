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
    private String couponId;
    private String userId;
    private int usageCount;

}
