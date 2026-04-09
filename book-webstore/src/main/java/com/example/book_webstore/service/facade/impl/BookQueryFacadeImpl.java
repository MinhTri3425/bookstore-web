package com.example.book_webstore.service.facade.impl;

import org.springframework.stereotype.Service;

import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookDetailDTO;
import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.dto.CategoryDTO;
import com.example.book_webstore.service.AuthorService;
import com.example.book_webstore.service.BookImageService;
import com.example.book_webstore.service.BookService;
import com.example.book_webstore.service.CategoryService;
import com.example.book_webstore.service.facade.BookQueryFacade;

import java.util.List;

@Service
public class BookQueryFacadeImpl implements BookQueryFacade {

    private final CategoryService categoryService;
    private final BookService bookService;
    private final AuthorService authorService;
    private final BookImageService bookImageService;

    public BookQueryFacadeImpl(
            CategoryService categoryService,
            BookService bookService,
            AuthorService authorService,
            BookImageService bookImageService) {

        this.categoryService = categoryService;
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookImageService = bookImageService;
    }

    @Override
    public BookDetailDTO getBookDetailById(Long id) {

        BookDTO book = bookService.getBookById(id);
        if (book == null)
            return null;

        AuthorDTO author = authorService.getAuthorById(book.getAuthorId());
        CategoryDTO category = categoryService.getCategoryById(book.getCategoryId());
        List<BookImageDTO> images = bookImageService.getBookImagesByBookId(id);

        return BookDetailDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .author(author)
                .category(category)
                .images(images)
                .stock(book.getStock())
                .build();
    }

    @Override
    public BookDTO getBookById(Long id) {
        BookDTO book = bookService.getBookById(id);
        if (book == null)
            return null;

        return populateBooksWithDetails(List.of(book)).get(0);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return populateBooksWithDetails(bookService.getAllBooks());
    }

    @Override
    public List<BookDTO> searchBooks(String keyword) {
        return populateBooksWithDetails(bookService.searchBooks(keyword));
    }

    @Override
    public List<BookDTO> filterBooks(String keyword, Long categoryId, Long authorId, Double minPrice, Double maxPrice) {
        return populateBooksWithDetails(
                bookService.filterBooks(keyword, categoryId, authorId, minPrice, maxPrice));
    }

    private List<BookDTO> populateBooksWithDetails(List<BookDTO> books) {
        return books.stream().map(book -> {
            String authorName = "Unknown";
            String categoryName = "Unknown";
            List<BookImageDTO> images = List.of(); // Mặc định là list rỗng

            // 1. Lấy tên tác giả
            try {
                AuthorDTO author = authorService.getAuthorById(book.getAuthorId());
                if (author != null)
                    authorName = author.getName();
            } catch (Exception ignored) {
            }

            // 2. Lấy tên danh mục
            try {
                CategoryDTO category = categoryService.getCategoryById(book.getCategoryId());
                if (category != null)
                    categoryName = category.getName();
            } catch (Exception ignored) {
            }

            // 3. QUAN TRỌNG: Phải lấy danh sách ảnh từ bookImageService
            try {
                images = bookImageService.getBookImagesByBookId(book.getId());
            } catch (Exception ignored) {
            }

            return BookDTO.builder()
                    .id(book.getId())
                    .title(book.getTitle())
                    .isbn(book.getIsbn())
                    .description(book.getDescription())
                    .price(book.getPrice())
                    .stock(book.getStock())
                    .authorId(book.getAuthorId())
                    .authorName(authorName)
                    .categoryId(book.getCategoryId())
                    .categoryName(categoryName)
                    .images(images) // Gán list ảnh vừa lấy được vào đây
                    .build();
        }).toList();
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        return authorService.getAllAuthors();
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryService.getAllCategories();
    }
}