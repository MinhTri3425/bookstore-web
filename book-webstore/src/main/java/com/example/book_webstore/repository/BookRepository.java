package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
