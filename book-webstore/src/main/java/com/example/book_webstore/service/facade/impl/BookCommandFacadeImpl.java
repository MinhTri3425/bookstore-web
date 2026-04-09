package com.example.book_webstore.service.facade.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.book_webstore.service.BookService;
import com.example.book_webstore.service.BookImageService;
import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.service.facade.BookCommandFacade;

@Service
public class BookCommandFacadeImpl implements BookCommandFacade {

    private final BookService bookService;
    private final BookImageService bookImageService;

    public BookCommandFacadeImpl(
            BookService bookService,
            BookImageService bookImageService) {

        this.bookService = bookService;
        this.bookImageService = bookImageService;
    }

    @Transactional
    @Override
    public BookDTO createBookFull(BookDTO dto) {

        // 1. Create book (stock nằm trong Book luôn)
        BookDTO createdBook = bookService.addBook(
                BookDTO.builder()
                        .title(dto.getTitle())
                        .isbn(dto.getIsbn())
                        .description(dto.getDescription())
                        .price(dto.getPrice())
                        .authorId(dto.getAuthorId())
                        .categoryId(dto.getCategoryId())
                        .stock(dto.getStock() != null ? dto.getStock() : 0)
                        .build());

        Long bookId = createdBook.getId();

        // 2. Add images
        if (dto.getImages() != null && !dto.getImages().isEmpty()) {
            bookImageService.addBookImages(bookId, dto.getImages());
        }

        return createdBook;
    }

    @Transactional
    @Override
    public BookDTO updateBook(Long id, BookDTO dto) {

        // 1. update book core fields (service tự handle merge)
        BookDTO updated = bookService.updateBook(id, dto);

        Long bookId = updated.getId();

        // 2. handle images tách riêng (ok giữ cách này)
        if (dto.getImages() != null) {

            bookImageService.deleteImagesByBookId(bookId);
            bookImageService.addBookImages(bookId, dto.getImages());
        }

        return updated;
    }

    @Transactional
    @Override
    public void deleteBook(Long id) {
        bookService.deleteBook(id);
    }
}