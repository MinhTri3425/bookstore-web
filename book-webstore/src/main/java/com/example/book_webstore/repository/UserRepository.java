package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
