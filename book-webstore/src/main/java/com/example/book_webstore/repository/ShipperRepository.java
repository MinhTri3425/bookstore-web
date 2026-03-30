package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Shipper;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {

}
