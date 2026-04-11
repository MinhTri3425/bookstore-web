package com.example.book_webstore.service.strategy.impl;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.service.strategy.LoginStrategy;

@Component
public class FacebookLoginImpl implements LoginStrategy {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User authenticate() {
        String mockEmail = "MinhTri@gmail.com";

        // Tìm user theo email
        User existingUser = userRepository.findByEmail(mockEmail);

        // Nếu đã tồn tại trong DB thì lấy ra dùng luôn
        if (existingUser != null) {
            return existingUser;
        }

        // Nếu chưa có (existingUser == null) thì tạo mới
        User newUser = new User();
        newUser.setEmail(mockEmail);
        newUser.setName("Hồ Minh Trí");
        newUser.setPhoneNumber("0901234567");
        newUser.setRole(User.Role.USER);
        newUser.setPassword(passwordEncoder.encode("123456"));

        return userRepository.save(newUser);
    }
}
