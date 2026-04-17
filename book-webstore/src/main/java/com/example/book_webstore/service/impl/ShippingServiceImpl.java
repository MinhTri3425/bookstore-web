package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipper;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.repository.ShipperRepository;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.service.ShippingService;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShippingServiceImpl implements ShippingService {

    private final ShippingRepository shippingRepository;
    private final ShipperRepository shipperRepository;
    private final CustomerOrderRepository orderRepository;

    public ShippingServiceImpl(ShippingRepository shippingRepository,
            ShipperRepository shipperRepository,
            CustomerOrderRepository orderRepository) {
        this.shippingRepository = shippingRepository;
        this.shipperRepository = shipperRepository;
        this.orderRepository = orderRepository;
    }

    @Override
    @Transactional
    public void autoAssignShipper(Long orderId) {
        // Nếu đã có bản ghi Shipping thì cập nhật shipper chứ không tạo mới
        Shipping shipping = shippingRepository.findByOrderId(orderId).orElse(null);

        // Nếu chưa có (trường hợp hiếm), mới tạo mới hoàn toàn
        if (shipping == null) {
            shipping = new Shipping();
            CustomerOrder order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
            shipping.setOrder(order);
            shipping.setCreatedAt(LocalDateTime.now());
            shipping.setMethod(Shipping.ShippingMethod.STANDARD);
            shipping.setCost(java.math.BigDecimal.ZERO);

            // Snapshot thông tin
            shipping.setCustomerName(order.getReceiverName());
            shipping.setCustomerAddress(order.getAddress());
            shipping.setCustomerPhone(order.getPhoneNumber());
            shipping.setNote(order.getNote());
        }

        // 1. Tìm danh sách Shipper đang rảnh
        List<Shipper> availableShippers = shipperRepository.findAvailableShippers();

        if (availableShippers.isEmpty()) {
            // Nếu không có ai rảnh, vẫn lưu shipping nhưng shipperId = null để đơn vào
            // "chợ"
            shipping.setShipper(null);
        } else {
            // 2. Chọn Shipper đầu tiên rảnh
            shipping.setShipper(availableShippers.get(0));
        }

        shipping.setStatus(Shipping.ShippingStatus.PENDING);
        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void acceptOrder(Long orderId, Long shipperId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin vận chuyển"));

        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);

        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void rejectOrder(Long orderId, Long shipperId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy vận chuyển"));

        // Xóa bản ghi gán đơn cũ
        shippingRepository.delete(shipping);

        // Tự động tìm người rảnh khác gán lại
        autoAssignShipper(orderId);
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, Shipping.ShippingStatus status) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng #" + orderId));

        Shipping shipping = order.getShipping();
        if (shipping == null)
            return;

        // 1. Cập nhật trạng thái Shipping
        shipping.setStatus(status);

        // 2. Logic khi giao hàng thành công
        if (status == Shipping.ShippingStatus.DELIVERED) {
            // Đổi trạng thái đơn hàng tổng quát
            order.setStatus(CustomerOrder.OrderStatus.COMPLETED);

            // TỰ ĐỘNG CẬP NHẬT THANH TOÁN (Cho đơn COD)
            if (order.getPayment() != null &&
                    order.getPayment().getStatus() != Payment.PaymentStatus.PAID) {

                order.getPayment().setStatus(Payment.PaymentStatus.PAID);
                order.getPayment().setPaidAt(LocalDateTime.now());
                // paymentRepository.save(order.getPayment()); // Tự động lưu nhờ @Transactional
            }
        }

        // 3. Logic khi giao hàng thất bại (Tùy chọn)
        if (status == Shipping.ShippingStatus.FAILED) {
            // Bạn có thể giữ đơn ở CONFIRMED để gán lại người khác hoặc hủy luôn tùy quy
            // trình
            // order.setStatus(CustomerOrder.OrderStatus.CANCELLED);
        }

        shippingRepository.save(shipping);
        orderRepository.save(order);
    }

    @Override
    public List<ShippingDTO> getActiveShippingsForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdAndStatus(shipperId, Shipping.ShippingStatus.SHIPPING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    private ShippingDTO toDto(Shipping shipping) {
        if (shipping == null)
            return null;
        CustomerOrder order = shipping.getOrder();
        return new ShippingDTO(
                shipping.getId(),
                shipping.getCost(),
                shipping.getStatus(),
                shipping.getMethod(),
                (shipping.getShipper() != null) ? shipping.getShipper().getId() : null,
                (order != null) ? order.getId() : null,
                shipping.getCreatedAt(),
                shipping.getCustomerName(),
                shipping.getCustomerAddress(),
                shipping.getCustomerPhone(),
                shipping.getNote());
    }

    @Override
    public List<ShippingDTO> getShippingHistoryForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdOrderByCreatedAtDesc(shipperId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handleOrderCancelled(Long orderId) {
        shippingRepository.findByOrderId(orderId).ifPresent(s -> {
            s.setStatus(Shipping.ShippingStatus.FAILED);
            s.setShipper(null);
            shippingRepository.save(s);
        });
    }

    // Các hàm phụ trợ Admin/Shipper giữ nguyên cấu trúc
    @Override
    public Page<ShippingDTO> getAdminShippingPage(String orderId, Shipping.ShippingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return shippingRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public List<ShippingDTO> getShippingsByShipperId(Long shipperId) {
        return shippingRepository.findAll().stream()
                .filter(s -> s.getShipper() != null && s.getShipper().getId().equals(shipperId))
                .map(this::toDto).collect(Collectors.toList());
    }

}