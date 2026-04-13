package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Shipper;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.repository.ShipperRepository;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.service.ShippingService;
import com.example.book_webstore.service.strategy.ShippingStrategy.ShippingCostProvider;
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
    private final ShippingCostProvider shippingCostProvider; // Inject Strategy Provider

    public ShippingServiceImpl(ShippingRepository shippingRepository,
            ShipperRepository shipperRepository,
            CustomerOrderRepository orderRepository,
            ShippingCostProvider shippingCostProvider) {
        this.shippingRepository = shippingRepository;
        this.shipperRepository = shipperRepository;
        this.orderRepository = orderRepository;
        this.shippingCostProvider = shippingCostProvider;
    }

    @Override
    @Transactional
    public void acceptOrder(Long orderId, Long shipperId) {
        // 1. Kiểm tra đơn hàng
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));

        // 2. Kiểm tra xem đơn đã có Shipping chưa (tránh 2 shipper cùng nhận 1 đơn)
        if (shippingRepository.existsByOrderId(orderId)) {
            throw new RuntimeException("Đơn hàng đã có người nhận giao!");
        }

        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper không tồn tại"));

        // 3. TẠO MỚI bản ghi Shipping khi có người nhận
        Shipping shipping = new Shipping();
        shipping.setOrder(order);
        shipping.setShipper(shipper);
        shipping.setCreatedAt(LocalDateTime.now());
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);

        // Phương thức mặc định (có thể lấy từ yêu cầu của khách trong Order)
        Shipping.ShippingMethod method = Shipping.ShippingMethod.STANDARD;
        shipping.setMethod(method);

        // Dùng Strategy tính phí ship tại thời điểm nhận đơn
        BigDecimal cost = shippingCostProvider.getStrategy(method).calculateShippingCost(orderId);
        shipping.setCost(cost);

        // Lưu thông tin khách hàng từ Order sang Shipping (để Shipper tra cứu nhanh)
        shipping.setCustomerName(order.getReceiverName());
        shipping.setCustomerAddress(order.getAddress());
        shipping.setCustomerPhone(order.getPhoneNumber());
        shipping.setNote(order.getNote());

        // 4. Cập nhật trạng thái bên Order (Module Ship tác động sang Order)
        // Lưu ý: Việc này bên Order sẽ hiểu là hàng bắt đầu rời kho
        // order.setStatus(CustomerOrder.OrderStatus.SHIPPING); // Nếu Enum Order có
        // SHIPPING

        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void assignShipperManual(Long orderId, Long shipperId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Shipper not found"));

        Shipping shipping = order.getShipping();
        if (shipping == null) {
            shipping = new Shipping();
            shipping.setOrder(order);
            shipping.setCreatedAt(LocalDateTime.now());
            shipping.setMethod(Shipping.ShippingMethod.STANDARD);

            // Tính phí ship cho bản ghi mới
            BigDecimal calculatedCost = shippingCostProvider.getStrategy(shipping.getMethod())
                    .calculateShippingCost(orderId);
            shipping.setCost(calculatedCost);
        }

        shipping.setShipper(shipper);
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, Shipping.ShippingStatus status) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Shipping shipping = order.getShipping();
        if (shipping == null)
            return;

        shipping.setStatus(status);

        if (status == Shipping.ShippingStatus.DELIVERED) {
            order.setStatus(CustomerOrder.OrderStatus.COMPLETED);
        }

        shippingRepository.save(shipping);
    }

    @Override
    public List<ShippingDTO> getActiveShippingsForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdAndStatus(shipperId, Shipping.ShippingStatus.SHIPPING)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ShippingDTO> getAdminShippingPage(String orderId, Shipping.ShippingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (orderId != null && !orderId.trim().isEmpty()) {
            return shippingRepository.findByOrderId(Long.valueOf(orderId), pageable).map(this::toDto);
        } else if (status != null) {
            return shippingRepository.findByStatus(status, pageable).map(this::toDto);
        }

        return shippingRepository.findAll(pageable).map(this::toDto);
    }

    // --- MAPPER CẬP NHẬT: Đổ dữ liệu cost từ Entity ra DTO ---
    private ShippingDTO toDto(Shipping shipping) {
        if (shipping == null)
            return null;

        CustomerOrder order = shipping.getOrder();

        // Khởi tạo DTO với đầy đủ tham số từ Constructor @AllArgsConstructor
        return new ShippingDTO(
                shipping.getId(),
                shipping.getCost(),
                shipping.getStatus(),
                shipping.getMethod(),
                (shipping.getShipper() != null) ? shipping.getShipper().getId() : null,
                (order != null) ? order.getId() : null,
                shipping.getCreatedAt(),
                // Lấy thông tin khách hàng từ đối tượng Order
                (order != null) ? order.getReceiverName() : "N/A",
                (order != null) ? order.getAddress() : "N/A",
                (order != null) ? order.getPhoneNumber() : "N/A",
                (order != null) ? order.getNote() : "");
    }

    @Override
    public List<ShippingDTO> getShippingsByShipperId(Long shipperId) {
        return shippingRepository.findAll().stream()
                .filter(s -> s.getShipper() != null && s.getShipper().getId().equals(shipperId))
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void handleOrderCancelled(Long orderId) {
        // Tìm bản ghi vận chuyển liên quan đến đơn hàng
        // Bạn nên viết thêm hàm findByOrderId trong ShippingRepository
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElse(null);

        if (shipping != null) {
            // 1. Cập nhật trạng thái vận chuyển thành FAILED hoặc CANCELLED
            shipping.setStatus(Shipping.ShippingStatus.FAILED);

            // 2. GIẢI PHÓNG SHIPPER: Cực kỳ quan trọng
            // Việc set null giúp shipper trở về trạng thái rảnh (ActiveJobs = 0)
            shipping.setShipper(null);

            shippingRepository.save(shipping);
        }
    }

    @Override
    public List<ShippingDTO> getShippingHistoryForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdOrderByCreatedAtDesc(shipperId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

}