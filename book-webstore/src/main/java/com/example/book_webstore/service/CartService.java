package com.example.book_webstore.service;

import com.example.book_webstore.dto.CartDTO;

public interface CartService {
    CartDTO getOrCreateCart(Long cartId);

    CartDTO addToCart(Long cartId, Long bookId, int quantity);

    CartDTO removeFromCart(Long cartId, Long bookId);

    long getItemCount(Long cartId);
}
