package com.example.book_webstore.service.state.order;

import java.util.EnumSet;

import org.springframework.stereotype.Component;

import com.example.book_webstore.model.CustomerOrder;

@Component
public class ConfirmedOrderState extends AbstractOrderState {

    public ConfirmedOrderState() {
        super(EnumSet.of(
                CustomerOrder.OrderStatus.COMPLETED,
                CustomerOrder.OrderStatus.CANCELLED));
    }

    @Override
    public CustomerOrder.OrderStatus status() {
        return CustomerOrder.OrderStatus.CONFIRMED;
    }
}
