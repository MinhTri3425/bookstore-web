package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.BookImage;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    public BookServiceImpl(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());
        Book saved = bookRepository.save(book);
        return toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findByIdWithImages(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        return toDTO(book);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAllWithImages().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());
        Book updated = bookRepository.save(book);
        return toDTO(updated);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDTO toDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        dto.setAuthorId(book.getAuthor() != null ? String.valueOf(book.getAuthor().getId()) : null);
        dto.setCategoryId(book.getCategory() != null ? String.valueOf(book.getCategory().getId()) : null);
        dto.setImages(toImageDTOs(book.getImages()));
        return dto;
    }

    private List<BookImageDTO> toImageDTOs(List<BookImage> images) {
        if (images == null) {
            return List.of();
        }
        return images.stream().map(image -> {
            BookImageDTO dto = new BookImageDTO();
            dto.setId(image.getId());
            dto.setUrl(image.getUrl());
            dto.setAltText(image.getAltText());
            dto.setSortOrder(image.getSortOrder());
            return dto;
        }).collect(Collectors.toList());
    }
}
