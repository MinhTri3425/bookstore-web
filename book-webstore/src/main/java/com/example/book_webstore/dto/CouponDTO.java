package com.example.book_webstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    private BigDecimal value;
    private int maxUsePerUser;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<BookDTO> applicableBooks;
}
