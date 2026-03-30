package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
