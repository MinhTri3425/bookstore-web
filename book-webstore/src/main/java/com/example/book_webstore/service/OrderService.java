package com.example.book_webstore.service;

import java.util.List;
import org.springframework.data.domain.Page;
import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;

public interface OrderService {

    Page<CustomerOrderDTO> getAdminOrderPage(String orderId, CustomerOrder.OrderStatus status, int page, int size);

    CustomerOrderDTO getAdminOrderDetail(Long id);

    Page<CustomerOrderDTO> getCustomerOrderPage(Long customerId, CustomerOrder.OrderStatus status, int page, int size);

    CustomerOrderDTO getCustomerOrderDetail(Long customerId, Long id);

    void cancelCustomerOrder(Long customerId, Long id);

    void updateOrderStatus(Long id, CustomerOrder.OrderStatus status);

    void updatePayment(Long id, Payment.PaymentStatus status);

    List<UserDTO> getCustomerOptions();

    List<CustomerOrderDTO> getOrdersReadyForPickup();

    void refreshOrderTotal(Long orderId);
}