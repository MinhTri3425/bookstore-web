package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

}
