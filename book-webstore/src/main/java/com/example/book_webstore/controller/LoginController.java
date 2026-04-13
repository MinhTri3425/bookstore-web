package com.example.book_webstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;
import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.service.UserService;
@Controller
public class LoginController {
    @Autowired
    private UserService userService;
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        // Truyền một đối tượng DTO rỗng xuống View để bind dữ liệu từ form
        model.addAttribute("userDTO", new UserDTO());
        return "register"; 
    }

    @PostMapping("/register")
    public String registerUserAccount(@ModelAttribute("userDTO") UserDTO userDTO, Model model) {
        try {
            userService.registerNewUser(userDTO);
            // Đăng ký thành công thì chuyển hướng về trang login kèm thông báo
            return "redirect:/login?registerSuccess=true";
        } catch (Exception e) {
            // Nếu có lỗi (như trùng email), hiển thị lại form kèm câu báo lỗi
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }
}