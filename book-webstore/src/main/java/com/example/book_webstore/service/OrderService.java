package com.example.book_webstore.service;

import org.springframework.data.domain.Page;

import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.CustomerOrder;

public interface OrderService {

    Page<CustomerOrderDTO> getOrderPage(String orderId, CustomerOrder.OrderStatus status, int page, int size);

    CustomerOrderDTO getOrderDetail(Long id);
}
