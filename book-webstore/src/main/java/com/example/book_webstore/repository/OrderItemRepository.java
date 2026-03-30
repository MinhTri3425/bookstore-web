package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
