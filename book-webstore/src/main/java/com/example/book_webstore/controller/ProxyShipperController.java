package com.example.book_webstore.controller;

import com.example.book_webstore.dto.ShipperDTO;
import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.ShipperService;
import com.example.book_webstore.service.ShippingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/shipper")
public class ProxyShipperController {

    private final ShipperService shipperService;
    private final ShippingService shippingService;

    public ProxyShipperController(ShipperService shipperService, ShippingService shippingService) {
        this.shipperService = shipperService;
        this.shippingService = shippingService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";

        ShipperDTO shipper = shipperService.findShipperByEmail(principal.getName());
        if (shipper == null)
            return "redirect:/access-denied";

        // Lấy tất cả đơn hàng đang được gán cho Shipper này (bao gồm cả PENDING và
        // SHIPPING)
        // Bạn cần viết thêm hàm này trong ShippingService
        List<ShippingDTO> myOrders = shippingService.getShippingsByShipperId(shipper.getId());

        model.addAttribute("shipper", shipper);
        model.addAttribute("orders", myOrders);

        return "shipper/dashboard";
    }

    /**
     * Xử lý khi Shipper bấm "Đồng ý" (Accept) đơn nổ
     */
    @PostMapping("/accept")
    public String acceptOrder(@RequestParam Long orderId, Principal principal) {
        ShipperDTO shipper = shipperService.findShipperByEmail(principal.getName());
        try {
            shippingService.acceptOrder(orderId, shipper.getId());
            return "redirect:/shipper/dashboard?success=accepted";
        } catch (Exception e) {
            return "redirect:/shipper/dashboard?error=" + e.getMessage();
        }
    }

    /**
     * Xử lý khi Shipper bấm "Từ chối" (Reject) đơn nổ
     */
    @PostMapping("/reject")
    public String rejectOrder(@RequestParam Long orderId, Principal principal) {
        ShipperDTO shipper = shipperService.findShipperByEmail(principal.getName());
        try {
            shippingService.rejectOrder(orderId, shipper.getId());
            return "redirect:/shipper/dashboard?info=rejected";
        } catch (Exception e) {
            return "redirect:/shipper/dashboard?error=" + e.getMessage();
        }
    }

    /**
     * Xử lý khi giao hàng thành công
     */
    @PostMapping("/complete")
    public String completeShipping(@RequestParam Long orderId, Principal principal) {
        shippingService.updateStatus(orderId, Shipping.ShippingStatus.DELIVERED);
        return "redirect:/shipper/dashboard?success=delivered";
    }

    @GetMapping("/history")
    public String shippingHistory(Principal principal, Model model) {
        ShipperDTO shipper = shipperService.findShipperByEmail(principal.getName());
        List<ShippingDTO> history = shippingService.getShippingHistoryForShipper(shipper.getId());

        model.addAttribute("history", history);
        return "shipper/history";
    }
}