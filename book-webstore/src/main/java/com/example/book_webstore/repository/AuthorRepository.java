package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {
}
