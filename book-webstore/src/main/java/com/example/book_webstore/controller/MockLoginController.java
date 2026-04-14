package com.example.book_webstore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.book_webstore.model.User;
import com.example.book_webstore.service.strategy.login.LoginFactory;
import com.example.book_webstore.service.strategy.login.LoginStrategy;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class MockLoginController {

    @Autowired
    private LoginFactory strategyFactory;

    // THÊM MỚI: Gọi Service chuẩn của Spring Security
    @Autowired
    private UserDetailsService userDetailsService;

    private SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    @GetMapping("/mock-login")
    public String handleMockSocialLogin(@RequestParam String provider, HttpServletRequest request,
            HttpServletResponse response) {

        // 1. Áp dụng Strategy để lấy User từ DB
        LoginStrategy strategy = strategyFactory.getStrategy(provider);
        User user = strategy.login();

        // 2. KHẮC PHỤC LỖI 500: Tải đối tượng UserDetails chuẩn mực từ DB
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());

        // 3. Tạo thẻ chứng nhận bằng userDetails chuẩn (thay vì dùng String như trước)
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
                userDetails.getAuthorities());

        // 4. Lưu ngữ cảnh đăng nhập
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context, request, response);

        // 5. Chuyển hướng về trang chủ
        return "redirect:/";
    }
}