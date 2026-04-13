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
        // Lấy email của người đang đăng nhập
        String email = principal.getName();
        
        // Gọi Service lấy thông tin User
        UserDTO userDTO = userService.findByEmail(email);
        
        // Gửi qua giao diện JSP
        model.addAttribute("user", userDTO);
        
        return "user/profile"; // Trỏ tới file profile.jsp
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
