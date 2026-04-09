package com.example.book_webstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    List<User> findByRoleOrderByNameAsc(User.Role role);

    User findByEmail(String email);
}
