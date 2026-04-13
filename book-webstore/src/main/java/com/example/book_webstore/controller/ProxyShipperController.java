package com.example.book_webstore.controller;

import com.example.book_webstore.dto.ShipperDTO;
import com.example.book_webstore.dto.ShippingDTO;
import com.example.book_webstore.dto.CustomerOrderDTO; // Giả sử bạn có DTO này
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.service.ShipperService;
import com.example.book_webstore.service.ShippingService;
import com.example.book_webstore.service.OrderService; // Inject thêm để lấy đơn chờ
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/shipper")
public class ProxyShipperController {

    private final ShipperService shipperService;
    private final ShippingService shippingService;
    private final OrderService orderService; // Thêm OrderService

    public ProxyShipperController(ShipperService shipperService,
            ShippingService shippingService,
            OrderService orderService) {
        this.shipperService = shipperService;
        this.shippingService = shippingService;
        this.orderService = orderService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        ShipperDTO shipper = shipperService.findShipperByEmail(email);

        if (shipper == null)
            return "redirect:/access-denied";

        // Lấy danh sách các đơn hàng MÌNH ĐANG GIAO
        List<ShippingDTO> activeOrders = shippingService.getActiveShippingsForShipper(shipper.getId());

        model.addAttribute("shipper", shipper);
        model.addAttribute("orders", activeOrders);

        return "shipper/dashboard";
    }

    // --- MỚI: Trang hiển thị các đơn hàng đang chờ Shipper nhận ---
    @GetMapping("/available-orders")
    public String availableOrders(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";

        // Gọi hàm vừa tạo để lấy "chợ" đơn hàng
        List<CustomerOrderDTO> pendingOrders = orderService.getOrdersReadyForPickup();

        model.addAttribute("pendingOrders", pendingOrders);
        return "shipper/available_orders";
    }

    // --- MỚI: Xử lý khi Shipper bấm "Nhận đơn" ---
    @PostMapping("/accept")
    public String acceptOrder(@RequestParam Long orderId, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        ShipperDTO shipper = shipperService.findShipperByEmail(email);

        if (shipper == null)
            return "redirect:/access-denied";

        try {
            // Gọi hàm acceptOrder mà chúng ta đã viết trong ShippingServiceImpl
            shippingService.acceptOrder(orderId, shipper.getId());
            return "redirect:/shipper/dashboard?success=accepted";
        } catch (Exception e) {
            // Trường hợp có người khác nhanh tay nhận trước hoặc lỗi logic
            return "redirect:/shipper/available-orders?error=" + e.getMessage();
        }
    }

    @PostMapping("/complete")
    public String completeShipping(@RequestParam Long orderId, Principal principal) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        ShipperDTO shipper = shipperService.findShipperByEmail(email);

        if (shipper == null)
            return "redirect:/access-denied";

        // BẢO MẬT: Kiểm tra xem đơn này có đúng là của shipper này không trước khi cho
        // "Complete"
        List<ShippingDTO> myOrders = shippingService.getActiveShippingsForShipper(shipper.getId());
        boolean isMyOrder = myOrders.stream().anyMatch(o -> o.getOrderId().equals(orderId));

        if (!isMyOrder) {
            return "redirect:/access-denied";
        }

        shippingService.updateStatus(orderId, Shipping.ShippingStatus.DELIVERED);

        return "redirect:/shipper/dashboard?success=delivered";
    }

    @GetMapping("/history")
    public String shippingHistory(Principal principal, Model model) {
        if (principal == null)
            return "redirect:/login";

        String email = principal.getName();
        ShipperDTO shipper = shipperService.findShipperByEmail(email);

        // Lấy toàn bộ lịch sử (bao gồm cả đơn thành công, thất bại, đã hủy)
        List<ShippingDTO> history = shippingService.getShippingHistoryForShipper(shipper.getId());

        // Tính toán sơ bộ thu nhập (chỉ tính những đơn DELIVERED)
        BigDecimal totalEarnings = history.stream()
                .filter(s -> s.getStatus() == Shipping.ShippingStatus.DELIVERED)
                .map(ShippingDTO::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("history", history);
        model.addAttribute("totalEarnings", totalEarnings);
        return "shipper/history";
    }
}