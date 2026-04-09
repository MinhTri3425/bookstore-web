package com.example.book_webstore.service;

import com.example.book_webstore.dto.BookDTO;
import java.util.List;

public interface BookService {
        BookDTO addBook(BookDTO bookDTO);

        BookDTO getBookById(Long id);

        List<BookDTO> getAllBooks();

        BookDTO updateBook(Long id, BookDTO bookDTO);

        void deleteBook(Long id);

        List<BookDTO> searchBooks(String keyword);

        List<BookDTO> filterBooks(String keyword, Long categoryId, Long authorId, Double minPrice, Double maxPrice);

}