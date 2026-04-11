package com.example.book_webstore.service.strategy;
import com.example.book_webstore.model.User;
public interface LoginStrategy {
    User authenticate();
}
