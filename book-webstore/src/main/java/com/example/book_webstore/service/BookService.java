package com.example.book_webstore.service;

import org.springframework.stereotype.Service;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.repository.BookRepository;
import java.util.List;

@Service
public class BookService {
    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public BookDTO addBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setQuantity(bookDTO.getQuantity());
        book.setPrice(bookDTO.getPrice());
        book.setStatus(bookDTO.getStatus());
        Book savedBook = bookRepository.save(book);
        return new BookDTO(savedBook.getTitle(), savedBook.getQuantity(), savedBook.getPrice(), savedBook.getStatus());
    }

    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> new BookDTO(book.getTitle(), book.getQuantity(), book.getPrice(), book.getStatus()))
                .toList();
    }

    public BookDTO updateBook(Long id, BookDTO updatedBook) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(updatedBook.getTitle());
        book.setQuantity(updatedBook.getQuantity());
        book.setPrice(updatedBook.getPrice());
        book.setStatus(updatedBook.getStatus());
        Book savedBook = bookRepository.save(book);
        return new BookDTO(savedBook.getTitle(), savedBook.getQuantity(), savedBook.getPrice(), savedBook.getStatus());
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
