package com.example.book_webstore.service.state.order;

import java.util.EnumSet;

import org.springframework.stereotype.Component;

import com.example.book_webstore.model.CustomerOrder;

@Component
public class CancelledOrderState extends AbstractOrderState {

    public CancelledOrderState() {
        super(EnumSet.noneOf(CustomerOrder.OrderStatus.class));
    }

    @Override
    public CustomerOrder.OrderStatus status() {
        return CustomerOrder.OrderStatus.CANCELLED;
    }
}
