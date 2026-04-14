package com.example.book_webstore.controller;

import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.Shipping;
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

    public ShippingController(ShippingService shippingService) {
        this.shippingService = shippingService;
    }

    /**
     * Hiển thị danh sách vận chuyển (Có tìm kiếm và phân trang)
     */
    @GetMapping
    public String list(@RequestParam(required = false) String orderId,
            @RequestParam(required = false) Shipping.ShippingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Page<ShippingDTO> shippingPage = shippingService.getAdminShippingPage(orderId, status, page, size);

        model.addAttribute("shippings", shippingPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", shippingPage.getTotalPages());
        model.addAttribute("totalElements", shippingPage.getTotalElements());

        // Dữ liệu cho các dropdown bộ lọc
        model.addAttribute("statusOptions", Shipping.ShippingStatus.values());
        model.addAttribute("searchOrderId", orderId);
        model.addAttribute("selectedStatus", status);

        return "admin/shipping/list";
    }

    /**
     * Admin chỉ can thiệp cập nhật trạng thái đơn hàng
     * (Ví dụ: Đánh dấu đơn thất bại, đơn đã giao nếu shipper quên bấm,...)
     */
    @PostMapping("/{orderId}/update-status")
    public String updateStatus(@PathVariable Long orderId,
            @RequestParam Shipping.ShippingStatus status,
            RedirectAttributes ra) {
        try {
            shippingService.updateStatus(orderId, status);
            ra.addFlashAttribute("successMessage", "Cập nhật trạng thái đơn hàng #" + orderId + " thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/shipping";
    }

}