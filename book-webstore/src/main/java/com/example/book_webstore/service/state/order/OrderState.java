package com.example.book_webstore.service.state.order;

import com.example.book_webstore.model.CustomerOrder;

public interface OrderState {
    CustomerOrder.OrderStatus status();

    boolean canTransitionTo(CustomerOrder.OrderStatus target);
}
