package com.example.book_webstore.service.payment.strategy.impl;

import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.User;
import com.example.book_webstore.service.payment.strategy.PaymentStrategy;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class CashPaymentStrategy implements PaymentStrategy {
    @Override
    public Payment.PaymentMethod supportedMethod() {
        return Payment.PaymentMethod.CASH;
    }

    @Override
    public Payment createPayment(CustomerOrder order, User customer, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setUser(customer);
        payment.setPaidAt(null);
        return payment;
    }
}
