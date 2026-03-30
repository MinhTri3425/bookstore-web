package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long> {

}
