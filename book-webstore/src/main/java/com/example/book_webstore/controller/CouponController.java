package com.example.book_webstore.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.book_webstore.dto.ApplyCouponRequestDTO;
import com.example.book_webstore.dto.CouponValidationDTO;
import com.example.book_webstore.service.CouponService;

@RestController
@RequestMapping("/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    @GetMapping("/validate")
    public CouponValidationDTO validateCoupon(
            @RequestParam String code,
            @RequestParam Long cartId,
            Principal principal) {
        return couponService.validateCouponForCart(code, cartId, principal != null ? principal.getName() : null);
    }

    @PostMapping("/orders/{orderId}/apply")
    @ResponseStatus(HttpStatus.OK)
    public CouponValidationDTO applyCouponToOrder(
            @PathVariable Long orderId,
            @RequestBody ApplyCouponRequestDTO request,
            Principal principal) {
        return couponService.applyCouponToOrder(
                orderId,
                request != null ? request.getCode() : null,
                principal != null ? principal.getName() : null);
    }

    @DeleteMapping("/orders/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeCouponFromOrder(@PathVariable Long orderId, Principal principal) {
        couponService.removeCouponFromOrder(orderId, principal != null ? principal.getName() : null);
    }
}
