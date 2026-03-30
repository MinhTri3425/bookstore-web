package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.CouponUsage;

public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

}
