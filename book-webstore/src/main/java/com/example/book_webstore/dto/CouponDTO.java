package com.example.book_webstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.example.book_webstore.model.Coupon;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CouponDTO {
    private Long id;
    private String code;
    private Coupon.CouponType type;
    private Coupon.CouponTarget target;
    private BigDecimal value;
    private boolean active;
    private int maxUsePerUser;
    private Integer totalUsageLimit;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountValue;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startAt;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endAt;
    private Long currentUsageCount;
    private List<Long> applicableBookIds;
    private List<BookDTO> applicableBooks;
}
