package com.example.book_webstore.service.payment.strategy;

import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.User;

import java.math.BigDecimal;
import java.util.Map;

public interface PaymentStrategy {
    Payment.PaymentMethod supportedMethod();

    Payment createPayment(CustomerOrder order, User customer, BigDecimal amount);

    default String createCheckoutPaymentUrl(Long orderId,
                                            BigDecimal amount,
                                            String clientIp,
                                            String returnUrl) {
        return null;
    }

    default boolean validateCallbackSignature(Map<String, String> callbackParams) {
        return false;
    }
}
