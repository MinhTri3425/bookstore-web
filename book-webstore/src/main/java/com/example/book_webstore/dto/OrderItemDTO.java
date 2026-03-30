package com.example.book_webstore.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemDTO {
    private Long id;
    private BookDTO book;
    private String bookTitle;
    private int quantity;
    private BigDecimal price;
    private CustomerOrderDTO order;
}
