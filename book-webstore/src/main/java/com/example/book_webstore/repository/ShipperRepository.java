package com.example.book_webstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.book_webstore.model.Shipper;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    Optional<Shipper> findByEmail(String email);

    Optional<Shipper> findByPhone(String phone);

}
