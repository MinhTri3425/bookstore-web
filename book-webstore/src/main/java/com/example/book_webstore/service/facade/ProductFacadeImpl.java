package com.example.book_webstore.service.facade;

import org.springframework.stereotype.Service;
import com.example.book_webstore.service.InventoryService;

import jakarta.transaction.Transactional;

import com.example.book_webstore.service.CategoryService;
import com.example.book_webstore.service.BookService;
import com.example.book_webstore.service.AuthorService;
import com.example.book_webstore.service.BookImageService;
import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookDetailDTO;
import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.dto.CategoryDTO;

import java.util.List;

@Service
public class ProductFacadeImpl implements ProductFacade {
    private final InventoryService inventoryService;
    private final CategoryService categoryService;
    private final BookService bookService;
    private final AuthorService authorService;
    private final BookImageService bookImageService;

    public ProductFacadeImpl(InventoryService inventoryService, CategoryService categoryService,
            BookService bookService,
            AuthorService authorService, BookImageService bookImageService) {
        this.inventoryService = inventoryService;
        this.categoryService = categoryService;
        this.bookService = bookService;
        this.authorService = authorService;
        this.bookImageService = bookImageService;
    }

    @Override
    public BookDetailDTO getBookDetailById(Long id) {
        BookDTO book = bookService.getBookById(id);

        AuthorDTO author = authorService.getAuthorById(book.getAuthorId());

        CategoryDTO category = categoryService.getCategoryById(book.getCategoryId());

        List<BookImageDTO> images = bookImageService.getBookImagesByBookId(id);

        int stock = inventoryService.getStockLevel(id);

        return BookDetailDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .author(author)
                .category(category)
                .images(images)
                .stock(stock)
                .build();
    }

    @Transactional
    @Override
    public BookDetailDTO createBookFull(BookDTO dto) {
        BookDTO createdBook = bookService.addBook(
                BookDTO.builder()
                        .title(dto.getTitle())
                        .isbn(dto.getIsbn())
                        .description(dto.getDescription())
                        .price(dto.getPrice())
                        .authorId(dto.getAuthorId())
                        .categoryId(dto.getCategoryId())
                        .build());
        Long bookId = createdBook.getId();
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            bookImageService.addBookImages(bookId, dto.getImages());
        }
        if (dto.getStock() > 0) {
            inventoryService.increaseStock(bookId, dto.getStock());
        }
        return getBookDetailById(bookId);
    }
}
