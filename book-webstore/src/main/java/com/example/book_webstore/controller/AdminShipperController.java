package com.example.book_webstore.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/shippers")
public class AdminShipperController {

    @Autowired
    private UserRepository userRepository;

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
    public String promoteToShipper(@RequestParam String email, RedirectAttributes ra) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            user.setShipper(true); // Biến User thành Shipper
            userRepository.save(user);
            ra.addFlashAttribute("success", "Đã nâng cấp " + email + " thành Shipper thành công!");
        } else {
            ra.addFlashAttribute("error", "Không tìm thấy người dùng này.");
        }
        return "redirect:/admin/shippers";
    }
}
