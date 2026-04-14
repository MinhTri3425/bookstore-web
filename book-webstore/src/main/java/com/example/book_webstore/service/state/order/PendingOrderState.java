package com.example.book_webstore.service.state.order;

import java.util.EnumSet;

import org.springframework.stereotype.Component;

import com.example.book_webstore.model.CustomerOrder;

@Component
public class PendingOrderState extends AbstractOrderState {

    public PendingOrderState() {
        super(EnumSet.of(
                CustomerOrder.OrderStatus.CONFIRMED,
                CustomerOrder.OrderStatus.CANCEL_REQUESTED,
                CustomerOrder.OrderStatus.CANCELLED));
    }

    @Override
    public CustomerOrder.OrderStatus status() {
        return CustomerOrder.OrderStatus.PENDING;
    }
}
