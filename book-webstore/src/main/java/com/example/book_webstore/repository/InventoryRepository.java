package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {

}
