package com.example.book_webstore.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.security.core.Authentication;

import com.example.book_webstore.service.CartService;
import com.example.book_webstore.dto.CartDTO;

import jakarta.servlet.http.HttpSession;

@ControllerAdvice
public class GlobalControllerAdvice {
    private final CartService cartService;
    private static final String CART_SESSION_KEY = "CART_ID";

    public GlobalControllerAdvice(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("globalCartCount")
    public Long getCartCount(HttpSession session, Authentication authentication) {
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);

        String customerEmail = null;
        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            customerEmail = authentication.getName();
        }

        if (cartId == null && customerEmail == null) {
            return 0L;
        }

        CartDTO cart = cartService.getOrCreateCart(cartId, customerEmail);
        session.setAttribute(CART_SESSION_KEY, cart.getId());
        return cartService.getItemCount(cart.getId(), customerEmail);
    }
}
