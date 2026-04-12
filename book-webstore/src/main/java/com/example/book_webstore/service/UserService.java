package com.example.book_webstore.service;

import com.example.book_webstore.dto.UserDTO;
import com.example.book_webstore.model.User;

public interface UserService {
    User registerNewUser(UserDTO userDTO) throws Exception;
    UserDTO findByEmail(String email);
    void updateProfile(String email, UserDTO userDTO) throws Exception;
}
