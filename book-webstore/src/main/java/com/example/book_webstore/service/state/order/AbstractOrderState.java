package com.example.book_webstore.service.state.order;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import com.example.book_webstore.model.CustomerOrder;

abstract class AbstractOrderState implements OrderState {

    private final Set<CustomerOrder.OrderStatus> allowedTargets;

    protected AbstractOrderState(Set<CustomerOrder.OrderStatus> allowedTargets) {
        this.allowedTargets = Collections.unmodifiableSet(EnumSet.copyOf(allowedTargets));
    }

    @Override
    public boolean canTransitionTo(CustomerOrder.OrderStatus target) {
        if (target == null) {
            return false;
        }
        if (status() == target) {
            return true;
        }
        return allowedTargets.contains(target);
    }
}
