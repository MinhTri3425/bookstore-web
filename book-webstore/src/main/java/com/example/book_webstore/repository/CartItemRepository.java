package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
	Optional<CartItem> findByCartIdAndBookId(Long cartId, Long bookId);

	List<CartItem> findByCartId(Long cartId);

}
