package com.example.book_webstore.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book_webstore.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findByOrderIdIn(Collection<Long> orderIds);

    Optional<Payment> findByOrderId(Long orderId);
}
