package com.example.book_webstore.service.command.concrete;

import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.repository.ShippingRepository;
import com.example.book_webstore.service.command.ShippingCommand;

import java.time.LocalDateTime;

public class CompleteDeliveryCommand implements ShippingCommand {
    private final Shipping shipping;
    private final CustomerOrder order;
    private final ShippingRepository shippingRepo;
    private final CustomerOrderRepository orderRepo;

    public CompleteDeliveryCommand(Shipping shipping, CustomerOrder order,
            ShippingRepository shippingRepo, CustomerOrderRepository orderRepo) {
        this.shipping = shipping;
        this.order = order;
        this.shippingRepo = shippingRepo;
        this.orderRepo = orderRepo;
    }

    @Override
    public void execute() {
        shipping.setStatus(Shipping.ShippingStatus.DELIVERED);
        order.setStatus(CustomerOrder.OrderStatus.COMPLETED);

        if (order.getPayment() != null) {
            order.getPayment().setStatus(Payment.PaymentStatus.PAID);
            order.getPayment().setPaidAt(LocalDateTime.now());
        }

        shippingRepo.save(shipping);
        orderRepo.save(order);
    }

    @Override
    public void undo() {
        // Logic khôi phục về trạng thái SHIPPING
        shipping.setStatus(Shipping.ShippingStatus.SHIPPING);
        order.setStatus(CustomerOrder.OrderStatus.PENDING); // Hoặc trạng thái trước đó
        shippingRepo.save(shipping);
        orderRepo.save(order);
    }
}
