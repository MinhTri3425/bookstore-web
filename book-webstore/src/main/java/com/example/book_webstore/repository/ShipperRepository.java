package com.example.book_webstore.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import com.example.book_webstore.model.Shipper;

public interface ShipperRepository extends JpaRepository<Shipper, Long> {
    Optional<Shipper> findByEmail(String email);

    Optional<Shipper> findByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT s FROM Shipper s WHERE s.id NOT IN " +
            "(SELECT ship.shipper.id FROM Shipping ship " +
            "WHERE ship.status = 'SHIPPING' OR ship.status = 'PENDING')")
    List<Shipper> findAvailableShippers();

}
