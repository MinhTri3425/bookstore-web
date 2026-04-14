package com.example.book_webstore.service.state.order;

import java.util.EnumSet;

import org.springframework.stereotype.Component;

import com.example.book_webstore.model.CustomerOrder;

@Component
public class CancelRequestedOrderState extends AbstractOrderState {

    public CancelRequestedOrderState() {
        super(EnumSet.of(
                CustomerOrder.OrderStatus.PENDING,
                CustomerOrder.OrderStatus.CONFIRMED,
                CustomerOrder.OrderStatus.CANCELLED));
    }

    @Override
    public CustomerOrder.OrderStatus status() {
        return CustomerOrder.OrderStatus.CANCEL_REQUESTED;
    }
}
