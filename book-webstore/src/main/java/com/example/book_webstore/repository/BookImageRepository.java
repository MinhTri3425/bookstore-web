package com.example.book_webstore.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.book_webstore.model.BookImage;

public interface BookImageRepository extends JpaRepository<BookImage, Long> {

}
