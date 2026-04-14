package com.example.book_webstore.controller;

import java.security.Principal;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.service.OrderService;
import com.example.book_webstore.service.ShippingService;

@Controller
@RequestMapping
public class OrderController {

    private final OrderService orderService;
    private final ShippingService shippingService;
    private final UserRepository userRepository;

    public OrderController(
            OrderService orderService,
            ShippingService shippingService,
            UserRepository userRepository) {
        this.orderService = orderService;
        this.shippingService = shippingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/order")
    public String orderHub(Principal principal) {
        User currentUser = requireCurrentUser(principal);
        return currentUser.getRole() == User.Role.ADMIN
                ? "redirect:/admin/orders"
                : "redirect:/my-orders";
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
    public String adminDetail(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            CustomerOrderDTO order = orderService.getAdminOrderDetail(id);

            if (order == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng không tồn tại!");
                return "redirect:/admin/orders";
            }

            model.addAttribute("pageTitle", "Chi tiết đơn hàng #" + order.getId());
            model.addAttribute("order", order);
            model.addAttribute("viewMode", "admin");
            model.addAttribute("backPath", "/admin/orders");

            // Đổ dữ liệu Enum để Admin có thể cập nhật trạng thái thủ công nếu cần
            model.addAttribute("orderStatusOptions", CustomerOrder.OrderStatus.values());
            model.addAttribute("paymentStatusOptions", Payment.PaymentStatus.values());
            model.addAttribute("shippingStatusOptions", Shipping.ShippingStatus.values());

            return "admin/order-detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @PostMapping("/admin/orders/{id}/status")
    public String updateOrderStatus(
            @PathVariable Long id,
            @RequestParam CustomerOrder.OrderStatus status,
            RedirectAttributes ra) {
        try {
            orderService.updateOrderStatus(id, status);
            ra.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng thành công.");

            // Lưu ý: Nếu Admin chọn CONFIRMED, Service sẽ tự gọi autoAssignShipper
            // nên chúng ta không cần làm gì thêm ở đây.
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/admin/orders/{id}/shipping")
    public String updateShipping(
            @PathVariable Long id,
            @RequestParam Shipping.ShippingStatus status,
            RedirectAttributes ra) {
        try {
            // Chỉ cập nhật trạng thái vận chuyển (Xóa bỏ logic shipperId)
            shippingService.updateStatus(id, status);
            ra.addFlashAttribute("successMessage", "Đã cập nhật trạng thái giao hàng.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi vận chuyển: " + e.getMessage());
        }
        return "redirect:/admin/orders/" + id;
    }

    // --- CÁC HÀM CỦA CUSTOMER GIỮ NGUYÊN ---

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
    public String customerDetail(@PathVariable Long id, Principal principal, Model model) {
        Long customerId = requireCurrentUser(principal).getId();
        CustomerOrderDTO order = orderService.getCustomerOrderDetail(customerId, id);
        model.addAttribute("order", order);
        model.addAttribute("viewMode", "customer");
        model.addAttribute("backPath", "/my-orders");
        return "user/order-detail";
    }

    @PostMapping("/my-orders/{id}/cancel")
    public String cancelCustomerOrder(@PathVariable Long id, Principal principal, RedirectAttributes ra) {
        Long customerId = requireCurrentUser(principal).getId();
        orderService.cancelCustomerOrder(customerId, id);
        ra.addFlashAttribute("successMessage", "Đã hủy đơn hàng.");
        return "redirect:/my-orders/" + id;
    }

    // --- PRIVATE HELPERS ---

    private void populateAdminListModel(Model model, Page<CustomerOrderDTO> orderPage, String orderId,
            CustomerOrder.OrderStatus status) {
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("searchOrderId", orderId == null ? "" : orderId.trim());
        model.addAttribute("listPath", "/admin/orders");
    }

    private void populateCustomerListModel(Model model, Page<CustomerOrderDTO> orderPage,
            CustomerOrder.OrderStatus status) {
        model.addAttribute("orders", orderPage.getContent());
        model.addAttribute("currentPage", orderPage.getNumber());
        model.addAttribute("totalPages", orderPage.getTotalPages());
        model.addAttribute("statusOptions", CustomerOrder.OrderStatus.values());
        model.addAttribute("selectedStatus", status == null ? "" : status.name());
        model.addAttribute("listPath", "/my-orders");
    }

    private User requireCurrentUser(Principal principal) {
        User user = userRepository.findByEmail(principal.getName());
        if (user == null)
            throw new IllegalStateException("User not found");
        return user;
    }
}