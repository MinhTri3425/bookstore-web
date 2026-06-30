package com.example.book_webstore.service;

import java.util.List;

import com.example.book_webstore.dto.CouponDTO;
import com.example.book_webstore.dto.CouponUsageDTO;
import com.example.book_webstore.dto.CouponValidationDTO;

public interface CouponService {
    List<CouponDTO> getAllCoupons();

    org.springframework.data.domain.Page<CouponDTO> getAllCoupons(org.springframework.data.domain.Pageable pageable);

    CouponDTO getCouponById(Long id);

    CouponDTO createCoupon(CouponDTO couponDTO);

    CouponDTO updateCoupon(Long id, CouponDTO couponDTO);

    void deleteCoupon(Long id);

    List<CouponUsageDTO> getCouponUsages(Long couponId);

    CouponValidationDTO validateCouponForCart(String code, Long cartId, String customerEmail);

    CouponValidationDTO applyCouponToOrder(Long orderId, String code, String customerEmail);

    void removeCouponFromOrder(Long orderId, String customerEmail);
}
