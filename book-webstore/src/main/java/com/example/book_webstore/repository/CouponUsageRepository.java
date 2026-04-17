package com.example.book_webstore.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.example.book_webstore.model.CouponUsage;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {
    Optional<CouponUsage> findByCouponIdAndCustomerId(Long couponId, Long customerId);

    List<CouponUsage> findByCouponIdOrderByIdDesc(Long couponId);

    @Query("select coalesce(sum(cu.usageCount), 0) from CouponUsage cu where cu.coupon.id = :couponId")
    long sumUsageCountByCouponId(Long couponId);
}
