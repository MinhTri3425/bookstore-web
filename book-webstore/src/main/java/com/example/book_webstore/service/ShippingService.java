package com.example.book_webstore.service;

import java.util.List;
import org.springframework.data.domain.Page;
import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.Shipping;

public interface ShippingService {

    // 1. Dành cho Admin: Tạo bản ghi vận chuyển chờ (đưa ra chợ) khi xác nhận đơn
    // hàng
    // Thay thế cho autoAssignShipper vì không gán trực tiếp nữa
    void createShippingRecord(Long orderId);

    // 2. Dành cho Shipper: Hành động "Nhặt đơn" từ chợ
    // Gán shipperId vào bản ghi Shipping và đổi status sang SHIPPING
    void assignShipperToShipping(Long shippingId, Long shipperId);

    // 3. Dành cho Shipper: Lấy danh sách đơn hàng chưa có người nhận (Chợ đơn)
    // Query: shipper IS NULL AND status = PENDING
    List<ShippingDTO> getAllAvailableShippings();

    // 4. Dành cho Shipper: Lấy các đơn hàng MÌNH ĐANG GIAO (nhiều đơn cùng lúc)
    // Query: shipperId = :id AND status = SHIPPING
    List<ShippingDTO> getActiveShippingsForShipper(Long shipperId);

    // 5. Cập nhật trạng thái (Giao thành công/thất bại)
    void updateStatus(Long orderId, Shipping.ShippingStatus status);

    // 6. Lịch sử và Quản lý
    List<ShippingDTO> getShippingHistoryForShipper(Long shipperId);

    Page<ShippingDTO> getAdminShippingPage(String orderId, Shipping.ShippingStatus status, int page, int size);

    void handleOrderCancelled(Long orderId);

    // Giữ lại nếu bạn vẫn muốn hỗ trợ Admin gán thủ công một ai đó
    void manualAssignShipper(Long orderId, Long shipperId);

    // Trong ShippingService.java
    List<ShippingDTO> getShippingsByShipperId(Long shipperId);

    void undoLastShippingAction();
}