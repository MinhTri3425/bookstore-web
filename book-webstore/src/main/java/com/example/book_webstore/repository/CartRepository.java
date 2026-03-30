package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long> {

}
