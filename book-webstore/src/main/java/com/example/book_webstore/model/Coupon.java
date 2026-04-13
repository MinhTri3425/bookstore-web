package com.example.book_webstore.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "coupons")
public class Coupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code;

    public enum CouponType {
        PERCENTAGE,
        FIXED
    }

    @Enumerated(EnumType.STRING)
    private CouponType type;
    private BigDecimal value;
    private boolean active = true;
    private int maxUsePerUser;
    private Integer totalUsageLimit;
    private BigDecimal minOrderValue;
    private BigDecimal maxDiscountValue;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @ManyToMany
    @JoinTable(name = "coupon_books", joinColumns = @JoinColumn(name = "coupon_id"), inverseJoinColumns = @JoinColumn(name = "book_id"))
    private List<Book> applicableBooks = new ArrayList<>();
}
