package com.example.book_webstore.service.payment.strategy.impl;

import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.User;
import com.example.book_webstore.service.payment.strategy.PaymentStrategy;
import com.example.book_webstore.service.payment.vnpay.VnPayService;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class VnPayPaymentStrategy implements PaymentStrategy {
    private final VnPayService vnPayService;

    public VnPayPaymentStrategy(VnPayService vnPayService) {
        this.vnPayService = vnPayService;
    }

    @Override
    public Payment.PaymentMethod supportedMethod() {
        return Payment.PaymentMethod.VNPAY;
    }

    @Override
    public Payment createPayment(CustomerOrder order, User customer, BigDecimal amount) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(amount);
        payment.setPaymentMethod(Payment.PaymentMethod.VNPAY);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setUser(customer);
        payment.setPaidAt(null);
        return payment;
    }

    @Override
    public String createCheckoutPaymentUrl(Long orderId,
                                           BigDecimal amount,
                                           String clientIp,
                                           String returnUrl) {
        return vnPayService.createPaymentUrl(orderId, amount, clientIp, returnUrl);
    }

    @Override
    public boolean validateCallbackSignature(Map<String, String> callbackParams) {
        return vnPayService.validateReturnSignature(callbackParams);
    }
}
