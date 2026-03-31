package com.example.book_webstore.dto;

import com.example.book_webstore.model.CustomerOrder;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private CustomerOrder.OrderStatus status;
    private String userId;
    private List<OrderItemDTO> items;

}
