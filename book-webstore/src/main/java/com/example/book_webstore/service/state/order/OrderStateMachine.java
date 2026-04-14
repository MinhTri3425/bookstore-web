package com.example.book_webstore.service.state.order;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.example.book_webstore.model.CustomerOrder;

@Component
public class OrderStateMachine {

    private final Map<CustomerOrder.OrderStatus, OrderState> stateMap = new EnumMap<>(CustomerOrder.OrderStatus.class);

    public OrderStateMachine(List<OrderState> states) {
        for (OrderState state : states) {
            stateMap.put(state.status(), state);
        }
    }

    public void assertTransitionAllowed(CustomerOrder.OrderStatus current, CustomerOrder.OrderStatus target) {
        if (current == null || target == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order status is required");
        }

        OrderState state = stateMap.get(current);
        if (state == null || !state.canTransitionTo(target)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Invalid order status transition: " + current + " -> " + target);
        }
    }
}
