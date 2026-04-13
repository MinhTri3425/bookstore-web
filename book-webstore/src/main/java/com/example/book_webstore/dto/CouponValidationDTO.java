package com.example.book_webstore.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponValidationDTO {
    private boolean valid;
    private String code;
    private String message;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal finalTotal;
    private Integer remainingUses;
}
