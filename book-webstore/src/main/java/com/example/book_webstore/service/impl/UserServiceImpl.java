package com.example.book_webstore.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.book_webstore.model.User;
import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.service.UserService;
@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder; // Dùng BCrypt đã config ở SecurityConfig
    @Override
    public User registerNewUser(UserDTO userDTO) throws Exception {
        // 1. Kiểm tra xem email đã tồn tại chưa
        if (userRepository.findByEmail(userDTO.getEmail()) != null) {
            throw new Exception("Email này đã được đăng ký!");
        }

        // 2. Chuyển từ DTO sang Entity
        User user = new User();
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setPhoneNumber(userDTO.getPhoneNumber());
        
        // 3. Mã hóa mật khẩu
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        
        // 4. Set quyền mặc định cho người đăng ký mới là USER
        user.setRole(User.Role.USER);

        // 5. Lưu vào database
        return userRepository.save(user);
    }
}
