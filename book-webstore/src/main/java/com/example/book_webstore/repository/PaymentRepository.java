package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
