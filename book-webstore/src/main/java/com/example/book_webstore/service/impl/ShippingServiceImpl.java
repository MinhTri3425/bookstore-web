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
import com.example.book_webstore.service.strategy.shipping.ShippingCostStrategyFactory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShippingServiceImpl implements ShippingService {

    private final ShippingRepository shippingRepository;
    private final ShipperRepository shipperRepository;
    private final CustomerOrderRepository orderRepository;
    private final ShippingCostStrategyFactory shippingCostStrategyFactory;

    public ShippingServiceImpl(ShippingRepository shippingRepository,
            ShipperRepository shipperRepository,
            CustomerOrderRepository orderRepository,
            ShippingCostStrategyFactory shippingCostStrategyFactory) {
        this.shippingRepository = shippingRepository;
        this.shipperRepository = shipperRepository;
        this.orderRepository = orderRepository;
        this.shippingCostStrategyFactory = shippingCostStrategyFactory;
    }

    // 1. Tạo bản ghi vận chuyển (Đưa đơn ra chợ)
    @Override
    @Transactional
    public void createShippingRecord(Long orderId) {
        // Tìm bản ghi Shipping đã tồn tại (khách tạo lúc checkout)
        Shipping shipping = shippingRepository.findByOrderId(orderId).orElse(null);

        if (shipping != null) {
            CustomerOrder order = shipping.getOrder();

            Shipping.ShippingMethod method = shipping.getMethod();

            BigDecimal calculatedCost = shippingCostStrategyFactory
                    .getStrategy(method)
                    .calculateShippingCost(order.getId());
            shipping.setCost(calculatedCost);

            shipping.setCustomerName(order.getReceiverName());
            shipping.setCustomerAddress(order.getAddress());
            shipping.setCustomerPhone(order.getPhoneNumber());
            shipping.setNote(order.getNote());

            shipping.setShipper(null);
            shipping.setStatus(Shipping.ShippingStatus.PENDING);

            shippingRepository.save(shipping);
        }
    }

    @Override
    public List<ShippingDTO> getShippingsByShipperId(Long shipperId) {
        // Hàm này lấy toàn bộ danh sách vận chuyển liên quan đến 1 shipper
        return shippingRepository.findByShipperId(shipperId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // 2. Nhặt đơn từ chợ
    @Override
    @Transactional
    public void assignShipperToShipping(Long shippingId, Long shipperId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        if (shipping.getShipper() != null) {
            throw new RuntimeException("Đơn hàng đã có người nhận!");
        }
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper không hợp lệ"));
        shipping.setShipper(shipper);
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
    }

    // 3. Lấy đơn ở chợ
    @Override
    public List<ShippingDTO> getAllAvailableShippings() {
        return shippingRepository.findByShipperIsNullAndStatus(Shipping.ShippingStatus.PENDING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // 4. Lấy đơn đang giao của tôi
    @Override
    public List<ShippingDTO> getActiveShippingsForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdAndStatus(shipperId, Shipping.ShippingStatus.SHIPPING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // 5. Cập nhật trạng thái (Giao thành công)
    @Override
    @Transactional
    public void updateStatus(Long orderId, Shipping.ShippingStatus status) {
        CustomerOrder order = orderRepository.findById(orderId).orElseThrow();
        Shipping shipping = order.getShipping();
        if (shipping == null)
            return;

        shipping.setStatus(status);
        if (status == Shipping.ShippingStatus.DELIVERED) {
            order.setStatus(CustomerOrder.OrderStatus.COMPLETED);
            if (order.getPayment() != null) {
                order.getPayment().setStatus(Payment.PaymentStatus.PAID);
                order.getPayment().setPaidAt(LocalDateTime.now());
            }
        }
        shippingRepository.save(shipping);
        orderRepository.save(order);
    }

    // 6. Lịch sử giao hàng
    @Override
    public List<ShippingDTO> getShippingHistoryForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdOrderByCreatedAtDesc(shipperId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // 7. Quản lý Admin (Hàm bị thiếu trong hình của bạn)
    @Override
    public Page<ShippingDTO> getAdminShippingPage(String orderId, Shipping.ShippingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        // Ở đây tạm thời findAll, bạn có thể thêm logic filter theo orderId/status sau
        return shippingRepository.findAll(pageable).map(this::toDto);
    }

    // 8. Xử lý khi đơn bị hủy (Hàm bị thiếu)
    @Override
    @Transactional
    public void handleOrderCancelled(Long orderId) {
        shippingRepository.findByOrderId(orderId).ifPresent(s -> {
            s.setStatus(Shipping.ShippingStatus.FAILED);
            shippingRepository.save(s);
        });
    }

    // 9. Admin gán thủ công (Hàm bị thiếu)
    @Override
    @Transactional
    public void manualAssignShipper(Long orderId, Long shipperId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId).orElseThrow();
        Shipper shipper = shipperRepository.findById(shipperId).orElseThrow();
        shipping.setShipper(shipper);
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
    }

    private ShippingDTO toDto(Shipping shipping) {
        if (shipping == null)
            return null;
        return new ShippingDTO(
                shipping.getId(), shipping.getCost(), shipping.getStatus(),
                shipping.getMethod(), (shipping.getShipper() != null ? shipping.getShipper().getId() : null),
                (shipping.getOrder() != null ? shipping.getOrder().getId() : null),
                shipping.getCreatedAt(), shipping.getCustomerName(),
                shipping.getCustomerAddress(), shipping.getCustomerPhone(), shipping.getNote());
    }
}