package com.example.book_webstore.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book_webstore.model.Shipping;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    List<Shipping> findByOrderIdIn(Collection<Long> orderIds);

    Optional<Shipping> findByOrderId(Long orderId);
}
