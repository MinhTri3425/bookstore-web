package com.example.book_webstore.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.service.OrderService;

@Controller
@RequestMapping
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(OrderService orderService, UserRepository userRepository) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    @GetMapping("/order")
    public String orderHub(Principal principal) {
        User currentUser = requireCurrentUser(principal);
        return currentUser.getRole() == User.Role.ADMIN ? "redirect:/admin/orders" : "redirect:/my-orders";
    }

    @GetMapping("/admin/orders")
    public String adminList(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) CustomerOrder.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<CustomerOrderDTO> orderPage = orderService.getAdminOrderPage(orderId, status, page, size);
        populateAdminListModel(model, orderPage, orderId, status);
        return "admin/orders";
    }

    @GetMapping("/admin/orders/{id}")
    public String adminDetail(@PathVariable Long id, Model model) {
        CustomerOrderDTO order = orderService.getAdminOrderDetail(id);
        model.addAttribute("pageTitle", "Admin Order #" + order.getId());
        model.addAttribute("order", order);
        model.addAttribute("viewMode", "admin");
        model.addAttribute("backPath", "/admin/orders");
        model.addAttribute("orderStatusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("paymentStatusOptions", Payment.PaymentStatus.values());
        model.addAttribute("shippingStatusOptions", Shipping.ShippingStatus.values());
        model.addAttribute("shipperOptions", orderService.getShipperOptions());
        return "admin/order-detail";
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam CustomerOrder.OrderStatus status,
            RedirectAttributes redirectAttributes) {
        orderService.updateOrderStatus(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Order status updated.");
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/admin/orders/{id}/payment")
    public String updatePaymentStatus(
            @PathVariable Long id,
            @RequestParam Payment.PaymentStatus status,
            RedirectAttributes redirectAttributes) {
        orderService.updatePayment(id, status);
        redirectAttributes.addFlashAttribute("successMessage", "Payment status updated.");
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/admin/orders/{id}/shipping")
    public String updateShipping(
            @PathVariable Long id,
            @RequestParam Shipping.ShippingStatus status,
            @RequestParam(required = false) Long shipperId,
            RedirectAttributes redirectAttributes) {
        orderService.updateShipping(id, status, shipperId);
        redirectAttributes.addFlashAttribute("successMessage", "Shipping information updated.");
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/my-orders")
    public String customerList(
            @RequestParam(required = false) CustomerOrder.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Principal principal,
            Model model) {
        Long customerId = requireCurrentUser(principal).getId();
        Page<CustomerOrderDTO> orderPage = orderService.getCustomerOrderPage(customerId, status, page, size);
        populateCustomerListModel(model, orderPage, status);
        return "user/orders";
    }

    @GetMapping("/my-orders/{id}")
    public String customerDetail(
            @PathVariable Long id,
            Principal principal,
            Model model) {
        Long customerId = requireCurrentUser(principal).getId();
        CustomerOrderDTO order = orderService.getCustomerOrderDetail(customerId, id);
        model.addAttribute("pageTitle", "My Order #" + order.getId());
        model.addAttribute("order", order);
        model.addAttribute("viewMode", "customer");
        model.addAttribute("backPath", "/my-orders");
        return "user/order-detail";
    }

    @PostMapping("/my-orders/{id}/cancel")
    public String cancelCustomerOrder(
            @PathVariable Long id,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Long customerId = requireCurrentUser(principal).getId();
        orderService.cancelCustomerOrder(customerId, id);
        redirectAttributes.addFlashAttribute("successMessage", "Order cancelled.");
        return "redirect:/my-orders/" + id;
    }

    private void populateAdminListModel(
            Model model,
            Page<CustomerOrderDTO> orderPage,
            String orderId,
            CustomerOrder.OrderStatus status) {
        model.addAttribute("pageTitle", "Admin Orders");
        model.addAttribute("pageHeading", "Order Management");
        model.addAttribute("pageEyebrow", "Admin console");
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalElements", orderPage.getTotalElements());
        model.addAttribute("pageSize", orderPage.getSize());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("searchOrderId", orderId == null ? "" : orderId.trim());
        model.addAttribute("viewMode", "admin");
        model.addAttribute("listPath", "/admin/orders");
        model.addAttribute("detailBasePath", "/admin/orders");
    }

    private void populateCustomerListModel(
            Model model,
            Page<CustomerOrderDTO> orderPage,
            CustomerOrder.OrderStatus status) {
        model.addAttribute("pageTitle", "My Orders");
        model.addAttribute("pageHeading", "My Orders");
        model.addAttribute("pageEyebrow", "Tai khoan cua toi");
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("totalElements", orderPage.getTotalElements());
        model.addAttribute("pageSize", orderPage.getSize());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("searchOrderId", "");
        model.addAttribute("viewMode", "customer");
        model.addAttribute("listPath", "/my-orders");
        model.addAttribute("detailBasePath", "/my-orders");
    }

    private User requireCurrentUser(Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new IllegalStateException("Authenticated user is required");
        }
        User user = userRepository.findByEmail(principal.getName());
        if (user == null) {
            throw new IllegalStateException("Authenticated user not found");
        }
        return user;
    }
}
