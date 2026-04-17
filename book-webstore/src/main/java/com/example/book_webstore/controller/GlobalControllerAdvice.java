package com.example.book_webstore.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.book_webstore.service.CartService;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final CartService cartService;
    private static final String CART_SESSION_KEY = "CART_ID";

    public GlobalControllerAdvice(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("globalCartCount")
    public Long getCartCount(HttpSession session) {
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        if (cartId == null)
            return 0L;
        return cartService.getItemCount(cartId);
    }
}
