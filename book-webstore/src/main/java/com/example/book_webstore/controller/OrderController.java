package com.example.book_webstore.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.OrderService;
import com.example.book_webstore.service.ShippingService;
import com.example.book_webstore.service.ShipperService;

@Controller
@RequestMapping
public class OrderController {

    private final OrderService orderService;
    private final ShippingService shippingService;
    private final ShipperService shipperService;

    // Inject đầy đủ các service liên quan
    public OrderController(OrderService orderService,
            ShippingService shippingService,
            ShipperService shipperService) {
        this.orderService = orderService;
        this.shippingService = shippingService;
        this.shipperService = shipperService;
    }

    @GetMapping({ "/order", "/admin/orders" })
    public String adminList(
            @RequestParam(required = false) String orderId,
            @RequestParam(required = false) CustomerOrder.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<CustomerOrderDTO> orderPage = orderService.getAdminOrderPage(orderId, status, page, size);
        populateAdminListModel(model, orderPage, orderId, status);
        return "order/show";
    }

    @GetMapping({ "/order/{id}", "/admin/orders/{id}" })
    public String adminDetail(@PathVariable Long id, Model model) {
        CustomerOrderDTO order = orderService.getAdminOrderDetail(id);

        model.addAttribute("pageTitle", "Admin Order #" + order.getId());
        model.addAttribute("order", order);
        model.addAttribute("viewMode", "admin");
        model.addAttribute("backPath", "/admin/orders");

        // Load các tùy chọn cho dropdown
        model.addAttribute("orderStatusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("paymentStatusOptions", Payment.PaymentStatus.values());
        model.addAttribute("shippingStatusOptions", Shipping.ShippingStatus.values());

        // LẤY TỪ SHIPPER SERVICE thay vì OrderService
        model.addAttribute("shipperOptions", shipperService.getAllShippers());

        return "order/detail";
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

        // GỌI TRỰC TIẾP SHIPPING SERVICE để xử lý logic vận chuyển
        shippingService.updateStatus(id, status);

        // Nếu có shipperId từ admin truyền xuống (chọn tay), thực hiện gán thủ công
        if (shipperId != null) {
            shippingService.assignShipperManual(id, shipperId);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Shipping information updated.");
        return "redirect:/admin/orders/" + id;
    }

    @GetMapping("/my-orders")
    public String customerList(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false) CustomerOrder.OrderStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Page<CustomerOrderDTO> orderPage = orderService.getCustomerOrderPage(customerId, status, page, size);
        populateCustomerListModel(model, orderPage, customerId, status);
        return "order/show";
    }

    @GetMapping("/my-orders/{id}")
    public String customerDetail(
            @PathVariable Long id,
            @RequestParam Long customerId,
            Model model) {
        CustomerOrderDTO order = orderService.getCustomerOrderDetail(customerId, id);
        model.addAttribute("pageTitle", "My Order #" + order.getId());
        model.addAttribute("order", order);
        model.addAttribute("viewMode", "customer");
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("backPath", "/my-orders");
        return "order/detail";
    }

    @PostMapping("/my-orders/{id}/cancel")
    public String cancelCustomerOrder(
            @PathVariable Long id,
            @RequestParam Long customerId,
            RedirectAttributes redirectAttributes) {
        orderService.cancelCustomerOrder(customerId, id);
        redirectAttributes.addFlashAttribute("successMessage", "Order cancelled.");
        return "redirect:/my-orders/" + id + "?customerId=" + customerId;
    }

    // --- Helper Methods ---

    private void populateAdminListModel(Model model, Page<CustomerOrderDTO> orderPage, String orderId,
            CustomerOrder.OrderStatus status) {
        model.addAttribute("pageTitle", "Admin Orders");
        model.addAttribute("pageHeading", "Order Management");
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("searchOrderId", orderId == null ? "" : orderId.trim());
        model.addAttribute("viewMode", "admin");
        model.addAttribute("listPath", "/admin/orders");
        model.addAttribute("detailBasePath", "/admin/orders");
    }

    private void populateCustomerListModel(Model model, Page<CustomerOrderDTO> orderPage, Long customerId,
            CustomerOrder.OrderStatus status) {
        model.addAttribute("pageTitle", "My Orders");
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("viewMode", "customer");
        model.addAttribute("listPath", "/my-orders");
        model.addAttribute("detailBasePath", "/my-orders");
        model.addAttribute("selectedCustomerId", customerId);
        model.addAttribute("customerOptions", orderService.getCustomerOptions());
    }
}