package com.example.book_webstore.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.service.BookService;

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
        return toDto(bookRepository.save(book));
    }

    @Override
    public BookDTO getBookById(Long id) {
        return bookRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(bookDTO.getTitle());
                    book.setIsbn(bookDTO.getIsbn());
                    book.setDescription(bookDTO.getDescription());
                    book.setPrice(bookDTO.getPrice());
                    return toDto(bookRepository.save(book));
                })
                .orElse(null);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDTO toDto(Book book) {
        return new BookDTO(
                book.getId(),
                book.getTitle(),
                book.getIsbn(),
                book.getDescription(),
                book.getPrice(),
                book.getAuthor() != null ? String.valueOf(book.getAuthor().getId()) : null,
                book.getCategory() != null ? String.valueOf(book.getCategory().getId()) : null,
                List.of());
    }
}
