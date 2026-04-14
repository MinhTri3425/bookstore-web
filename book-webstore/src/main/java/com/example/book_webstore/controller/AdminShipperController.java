package com.example.book_webstore.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.transaction.annotation.Transactional;
import com.example.book_webstore.model.Shipper;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.repository.ShipperRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shippers")
public class AdminShipperController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ShipperRepository shipperRepository;

    @GetMapping
    public String listShippers(Model model) {
        // Lấy tất cả user là shipper
        List<User> shippers = userRepository.findByIsShipperTrue();
        model.addAttribute("shippers", shippers);
        return "admin/shipper-list";
    }

    @PostMapping("/{id}/revoke")
    public String revokeShipper(@PathVariable Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user != null) {
            user.setShipper(false);
            userRepository.save(user);
        }
        return "redirect:/admin/shippers";
    }

    @PostMapping("/promote")
    @Transactional
    public String promoteToShipper(@RequestParam String email, RedirectAttributes ra) {
        // 1. Tìm User trong hệ thống dựa trên email
        User user = userRepository.findByEmail(email);

        if (user != null) {
            // 2. Bật cờ isShipper ở bảng User (để qua cổng Security)
            user.setShipper(true);
            userRepository.save(user);

            // 3. Kiểm tra xem đã có snapshot trong bảng Shippers chưa
            // Vì không có liên kết ID, ta kiểm tra qua Email
            if (!shipperRepository.existsByEmail(email)) {
                Shipper snapshot = new Shipper();

                // "Snapshot" - Chép dữ liệu từ User sang Shipper
                snapshot.setEmail(user.getEmail());
                snapshot.setName(user.getName());
                snapshot.setPhone(user.getPhoneNumber());

                shipperRepository.save(snapshot);
                ra.addFlashAttribute("success", "Đã lưu snapshot Shipper cho " + email);
            } else {
                ra.addFlashAttribute("success", "Tài khoản này đã có hồ sơ Shipper.");
            }
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy email: " + email);
        }
        return "redirect:/admin/shippers";
    }
}