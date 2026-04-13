package com.example.book_webstore.service;

import java.util.List;
import org.springframework.data.domain.Page;
import com.example.book_webstore.dto.ShippingDTO;

import com.example.book_webstore.model.Shipping;

public interface ShippingService {

    void acceptOrder(Long orderId, Long shipperId);

    // Gán thủ công bởi Admin (truyền orderId để xác định đơn hàng cần gán)
    void assignShipperManual(Long orderId, Long shipperId);

    // Cập nhật trạng thái giao hàng (Dùng orderId để đồng bộ với trang chi tiết đơn
    // hàng)
    void updateStatus(Long orderId, Shipping.ShippingStatus status);

    // Lấy danh sách đang giao cho Dashboard của Shipper
    List<ShippingDTO> getActiveShippingsForShipper(Long shipperId);

    // Lấy lịch sử cho Shipper
    List<ShippingDTO> getShippingsByShipperId(Long shipperId);

    // Trang quản lý dành cho Admin (Tìm kiếm theo mã đơn hoặc trạng thái)
    Page<ShippingDTO> getAdminShippingPage(String orderId, Shipping.ShippingStatus status, int page, int size);

    void handleOrderCancelled(Long orderId);

    List<ShippingDTO> getShippingHistoryForShipper(Long shipperId);
}