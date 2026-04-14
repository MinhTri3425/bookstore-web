package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.*;
import com.example.book_webstore.repository.*;
import com.example.book_webstore.service.ShippingService;
import com.example.book_webstore.service.command.*;
import com.example.book_webstore.service.strategy.shipping.ShippingCostStrategyFactory;
import com.example.book_webstore.service.command.concrete.*;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ShippingServiceImpl implements ShippingService {

    private final ShippingRepository shippingRepository;
    private final ShipperRepository shipperRepository;
    private final CustomerOrderRepository orderRepository;
    private final ShippingCostStrategyFactory shippingCostStrategyFactory;
    private final ShippingCommandInvoker invoker;

    public ShippingServiceImpl(ShippingRepository shippingRepository,
            ShipperRepository shipperRepository,
            CustomerOrderRepository orderRepository,
            ShippingCostStrategyFactory shippingCostStrategyFactory,
            ShippingCommandInvoker invoker) {
        this.shippingRepository = shippingRepository;
        this.shipperRepository = shipperRepository;
        this.orderRepository = orderRepository;
        this.shippingCostStrategyFactory = shippingCostStrategyFactory;
        this.invoker = invoker;
    }

    @Override
    @Transactional
    public void createShippingRecord(Long orderId) {
        shippingRepository.findByOrderId(orderId).ifPresent(shipping -> {
            CustomerOrder order = shipping.getOrder();
            BigDecimal calculatedCost = shippingCostStrategyFactory
                    .getStrategy(shipping.getMethod())
                    .calculateShippingCost(order.getId());

            shipping.setCost(calculatedCost);
            shipping.setCustomerName(order.getReceiverName());
            shipping.setCustomerAddress(order.getAddress());
            shipping.setCustomerPhone(order.getPhoneNumber());
            shipping.setNote(order.getNote());
            shipping.setShipper(null);
            shipping.setStatus(Shipping.ShippingStatus.PENDING);
            shippingRepository.save(shipping);
        });
    }

    @Override
    @Transactional
    public void assignShipperToShipping(Long shippingId, Long shipperId) {
        Shipping shipping = shippingRepository.findById(shippingId)
                .orElseThrow(() -> new RuntimeException("Shipping record not found"));
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Invalid Shipper"));

        invoker.execute(new AcceptOrderCommand(shipping, shipper, shippingRepository));
    }

    @Override
    public List<ShippingDTO> getAllAvailableShippings() {
        return shippingRepository.findByShipperIsNullAndStatus(Shipping.ShippingStatus.PENDING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public List<ShippingDTO> getActiveShippingsForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdAndStatus(shipperId, Shipping.ShippingStatus.SHIPPING)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void updateStatus(Long orderId, Shipping.ShippingStatus status) {
        CustomerOrder order = orderRepository.findById(orderId).orElseThrow();
        Shipping shipping = order.getShipping();
        if (shipping == null)
            return;

        if (status == Shipping.ShippingStatus.DELIVERED) {
            invoker.execute(new CompleteDeliveryCommand(shipping, order, shippingRepository, orderRepository));
        } else {
            shipping.setStatus(status);
            shippingRepository.save(shipping);
        }
    }

    @Override
    public List<ShippingDTO> getShippingHistoryForShipper(Long shipperId) {
        return shippingRepository.findByShipperIdOrderByCreatedAtDesc(shipperId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<ShippingDTO> getAdminShippingPage(String orderId, Shipping.ShippingStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return shippingRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    @Transactional
    public void handleOrderCancelled(Long orderId) {
        shippingRepository.findByOrderId(orderId).ifPresent(s -> {
            s.setStatus(Shipping.ShippingStatus.FAILED);
            shippingRepository.save(s);
        });
    }

    @Override
    @Transactional
    public void manualAssignShipper(Long orderId, Long shipperId) {
        Shipping shipping = shippingRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping record not found"));
        Shipper shipper = shipperRepository.findById(shipperId)
                .orElseThrow(() -> new RuntimeException("Invalid Shipper"));

        shipping.setShipper(shipper);
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);
        shippingRepository.save(shipping);
    }

    @Override
    public List<ShippingDTO> getShippingsByShipperId(Long shipperId) {
        return shippingRepository.findByShipperId(shipperId)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void undoLastShippingAction() {
        invoker.undoLast();
    }

    private ShippingDTO toDto(Shipping shipping) {
        if (shipping == null)
            return null;
        return new ShippingDTO(
                shipping.getId(), shipping.getCost(), shipping.getStatus(),
                shipping.getMethod(),
                (shipping.getShipper() != null ? shipping.getShipper().getId() : null),
                (shipping.getOrder() != null ? shipping.getOrder().getId() : null),
                shipping.getCreatedAt(), shipping.getCustomerName(),
                shipping.getCustomerAddress(), shipping.getCustomerPhone(), shipping.getNote());
    }
}