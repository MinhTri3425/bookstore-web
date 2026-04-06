package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.Category;
import com.example.book_webstore.repository.AuthorRepository;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.CategoryRepository;
import com.example.book_webstore.service.BookService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public BookDTO addBook(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());

        if (bookDTO.getAuthorId() != null) {
            Author author = authorRepository.findById(Long.parseLong(bookDTO.getAuthorId())).orElse(null);
            book.setAuthor(author);
        }

        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(Long.parseLong(bookDTO.getCategoryId())).orElse(null);
            book.setCategory(category);
        }

        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook);
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        return mapToDTO(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id).orElseThrow(() -> new RuntimeException("Book not found"));
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());

        if (bookDTO.getAuthorId() != null) {
            Author author = authorRepository.findById(Long.parseLong(bookDTO.getAuthorId())).orElse(null);
            book.setAuthor(author);
        }

        if (bookDTO.getCategoryId() != null) {
            Category category = categoryRepository.findById(Long.parseLong(bookDTO.getCategoryId())).orElse(null);
            book.setCategory(category);
        }

        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    private BookDTO mapToDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setIsbn(book.getIsbn());
        dto.setDescription(book.getDescription());
        dto.setPrice(book.getPrice());
        if (book.getAuthor() != null) {
            dto.setAuthorId(String.valueOf(book.getAuthor().getId()));
        }
        if (book.getCategory() != null) {
            dto.setCategoryId(String.valueOf(book.getCategory().getId()));
        }
        // Images mapping can be added later if needed
        return dto;
    }
}