package com.example.book_webstore.controller;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.service.UserService;

@Controller
@RequestMapping("/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @GetMapping
    public String showProfile(Principal principal, Model model) {
        try {
            if (principal == null) {
                return "redirect:/login";
            }
            String email = principal.getName();
            UserDTO userDTO = userService.findByEmail(email);

            if (userDTO == null) {
                throw new Exception("Không tìm thấy người dùng với email: " + email);
            }

            model.addAttribute("user", userDTO);
            return "user/profile";
        } catch (Exception e) {
            e.printStackTrace(); // In lỗi ra Console để bạn đọc được nó bị gì
            return "error"; // Hoặc trả về một trang báo lỗi tạm thời
        }
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") UserDTO userDTO, Principal principal) {
        String email = principal.getName();
        try {
            userService.updateProfile(email, userDTO);
            return "redirect:/profile?success"; // Load lại trang và báo thành công
        } catch (Exception e) {
            return "redirect:/profile?error";
        }
    }
}
