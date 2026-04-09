package com.example.book_webstore.service.facade;

import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookDetailDTO;
import com.example.book_webstore.dto.CategoryDTO;
import java.util.List;

public interface BookQueryFacade {
    BookDetailDTO getBookDetailById(Long id);

    BookDTO getBookById(Long id);

    List<BookDTO> getAllBooks();

    List<BookDTO> searchBooks(String keyword);

    List<BookDTO> filterBooks(String keyword, Long categoryId, Long authorId, Double minPrice, Double maxPrice);

    List<AuthorDTO> getAllAuthors();

    List<CategoryDTO> getAllCategories();
}