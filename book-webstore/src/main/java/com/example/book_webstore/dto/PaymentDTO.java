package com.example.book_webstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.example.book_webstore.model.Payment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaymentDTO {
    private Long id;
    private BigDecimal amount;
    private Payment.PaymentMethod method;
    private Payment.PaymentStatus status;
    private LocalDateTime paidAt;
    private UserDTO user;
    private CustomerOrderDTO order;
}
