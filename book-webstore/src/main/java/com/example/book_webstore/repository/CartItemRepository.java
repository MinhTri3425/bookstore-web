package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

}
