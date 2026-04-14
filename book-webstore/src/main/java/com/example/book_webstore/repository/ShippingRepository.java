package com.example.book_webstore.repository;

import com.example.book_webstore.model.Shipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

import java.util.List;

public interface ShippingRepository extends JpaRepository<Shipping, Long> {

    // Đếm số đơn đang giao để phục vụ thuật toán tự động xếp đơn
    @Query("SELECT COUNT(s) FROM Shipping s WHERE s.shipper.id = :shipperId AND s.status = 'SHIPPING'")
    long countActiveJobsByShipperId(@Param("shipperId") Long shipperId);

    // Lấy danh sách Shipping theo Shipper và trạng thái
    List<Shipping> findByShipperIdAndStatus(Long shipperId, Shipping.ShippingStatus status);

    Page<Shipping> findByStatus(Shipping.ShippingStatus status, Pageable pageable);

    // Tìm theo mã đơn hàng có phân trang
    Page<Shipping> findByOrderId(Long orderId, Pageable pageable);

    boolean existsByOrderId(Long orderId);

    Optional<Shipping> findByOrderId(Long orderId);

    Optional<Shipping> findByShipperId(Long shipperd);

    List<Shipping> findByShipperIdOrderByCreatedAtDesc(Long shipperId);

    List<Shipping> findByShipperIsNullAndStatus(Shipping.ShippingStatus status);

}