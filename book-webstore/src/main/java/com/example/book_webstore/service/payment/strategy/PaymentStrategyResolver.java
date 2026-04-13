package com.example.book_webstore.service.payment.strategy;

import com.example.book_webstore.model.Payment;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentStrategyResolver {
    private final List<PaymentStrategy> strategies;

    public PaymentStrategyResolver(List<PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy resolve(Payment.PaymentMethod method) {
        Payment.PaymentMethod targetMethod = method == null ? Payment.PaymentMethod.CASH : method;
        return strategies.stream()
                .filter(strategy -> strategy.supportedMethod() == targetMethod)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Phuong thuc thanh toan chua duoc ho tro: " + targetMethod));
    }
}
