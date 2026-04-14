package com.example.book_webstore.controller;

import com.example.book_webstore.dto.ShipperDTO;
import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.ShipperService;
import com.example.book_webstore.service.ShippingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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

        // 1. Lấy danh sách "Chợ đơn" (Đơn chưa có ai nhận - shipperId = null)
        // Bạn cần viết thêm hàm này: findAllAvailableShippings()
        List<ShippingDTO> marketOrders = shippingService.getAllAvailableShippings();

        // 2. Lấy danh sách đơn hàng Shipper này ĐÃ NHẬN và ĐANG GIAO
        // Hàm này lấy shippings theo shipperId và status = SHIPPING
        List<ShippingDTO> myActiveOrders = shippingService.getActiveShippingsForShipper(shipper.getId());

        model.addAttribute("shipper", shipper);
        model.addAttribute("marketOrders", marketOrders); // Đơn ở chợ
        model.addAttribute("myActiveOrders", myActiveOrders); // Đơn của tôi

        return "shipper/dashboard";
    }

    /**
     * Hành động "Nhặt đơn" từ chợ
     */
    @PostMapping("/pick-up")
    public String pickUpOrder(@RequestParam Long shippingId, Principal principal) {
        ShipperDTO shipper = shipperService.findShipperByEmail(principal.getName());
        try {
            // Hàm này sẽ gán shipperId vào Shipping và đổi status sang SHIPPING
            shippingService.assignShipperToShipping(shippingId, shipper.getId());
            return "redirect:/shipper/dashboard?success=picked";
        } catch (Exception e) {
            return "redirect:/shipper/dashboard?error=" + e.getMessage();
        }
    }

    @PostMapping("/complete")
    public String completeShipping(@RequestParam("orderId") Long orderId) {
        // Thêm dòng log này để kiểm tra xem request có vào được đến đây không
        System.out.println("Shipper dang xac nhan hoan thanh don hang: " + orderId);

        shippingService.updateStatus(orderId, Shipping.ShippingStatus.DELIVERED);
        return "redirect:/shipper/dashboard?success=delivered";
    }

    @GetMapping("/history")
    public String shippingHistory(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";

        ShipperDTO shipper = shipperService.findShipperByEmail(principal.getName());
        if (shipper == null)
            return "redirect:/access-denied";

        List<ShippingDTO> history = shippingService.getShippingHistoryForShipper(shipper.getId());

        model.addAttribute("history", history);
        return "shipper/history";
    }

    @PostMapping("/undo")
    public String undo(RedirectAttributes ra) {
        try {
            // Gọi Service để bốc lệnh cuối ra và hoàn tác
            shippingService.undoLastShippingAction();

            // Cách 1: Dùng Parameter (Khớp với file JSP của bạn đang check param.success)
            return "redirect:/shipper/dashboard?success=undone";

        } catch (Exception e) {
            // Trả về lỗi nếu không có gì để undo
            return "redirect:/shipper/dashboard?error="
                    + java.net.URLEncoder.encode(e.getMessage(), java.nio.charset.StandardCharsets.UTF_8);
        }
    }
}