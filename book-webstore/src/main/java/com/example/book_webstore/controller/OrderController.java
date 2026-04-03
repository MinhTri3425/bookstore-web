package com.example.book_webstore.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.service.OrderService;

@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/order")
    public String show(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) CustomerOrder.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<CustomerOrderDTO> orderPage = orderService.getOrderPage(orderId, status, page, size);
        model.addAttribute("pageTitle", "Orders");
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalElements", orderPage.getTotalElements());
        model.addAttribute("pageSize", orderPage.getSize());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("searchOrderId", orderId == null ? "" : orderId.trim());
        return "order/show";
    }

    @GetMapping("/order/{id}")
    public String detail(@PathVariable Long id, Model model) {
        CustomerOrderDTO order = orderService.getOrderDetail(id);
        model.addAttribute("pageTitle", "Order #" + order.getId());
        model.addAttribute("order", order);
        return "order/detail";
    }
}
