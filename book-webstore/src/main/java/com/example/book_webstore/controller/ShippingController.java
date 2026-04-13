package com.example.book_webstore.controller;

import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.ShipperService;
import com.example.book_webstore.service.ShippingService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shipping")
public class ShippingController {

    private final ShippingService shippingService;
    private final ShipperService shipperService;

    public ShippingController(ShippingService shippingService, ShipperService shipperService) {
        this.shippingService = shippingService;
        this.shipperService = shipperService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String orderId, // Chuyển thành String để khớp với Service
            @RequestParam(required = false) Shipping.ShippingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        // Gọi hàm phân trang từ Service
        Page<ShippingDTO> shippingPage = shippingService.getAdminShippingPage(orderId, status, page, size);

        model.addAttribute("shippings", shippingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shippingPage.getTotalPages());
        model.addAttribute("totalElements", shippingPage.getTotalElements());

        // Load dữ liệu cho bộ lọc (Filter)
        model.addAttribute("shipperOptions", shipperService.getAllShippers());
        model.addAttribute("statusOptions", Shipping.ShippingStatus.values());
        model.addAttribute("searchOrderId", orderId);
        model.addAttribute("selectedStatus", status);

        return "admin/shipping/list";
    }

    @PostMapping("/{orderId}/update") // Đổi tên path variable thành orderId cho rõ ràng
    public String update(@PathVariable Long orderId,
            @RequestParam Shipping.ShippingStatus status,
            @RequestParam(required = false) Long shipperId,
            RedirectAttributes redirectAttributes) {

        // 1. Cập nhật trạng thái (Hàm updateStatus của bạn đang nhận orderId)
        shippingService.updateStatus(orderId, status);

        // 2. Nếu Admin chọn Shipper (Gán thủ công hoặc đổi người)
        if (shipperId != null) {
            shippingService.assignShipperManual(orderId, shipperId);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin vận chuyển thành công!");
        return "redirect:/admin/shipping";
    }
}