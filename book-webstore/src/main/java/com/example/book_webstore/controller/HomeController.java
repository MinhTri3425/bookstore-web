package com.example.book_webstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 1. Trang chủ chính thức
    @GetMapping("/")
    public String home() {
        // Trỏ đến /WEB-INF/views/index.jsp
        return "index";
    }

    // 2. Dự phòng nếu người dùng gõ /home
    @GetMapping("/home")
    public String homePage() {
        return "index";
    }

    // 3. Điều hướng nhanh vào khu vực Admin
    @GetMapping("/admin")
    public String adminRedirect() {
        // Redirect không phụ thuộc vào View Engine nên giữ nguyên
        return "redirect:/admin/dashboard";
    }
}