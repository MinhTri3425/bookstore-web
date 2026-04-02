package com.example.book_webstore.service.Implement;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.Category;
import com.example.book_webstore.repository.AuthorRepository;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.CategoryRepository;
import com.example.book_webstore.service.BookService;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class BookServiceImpl implements BookService {
    private BookRepository bookRepository;
    private AuthorRepository authorRepository;
    private CategoryRepository categoryRepository;

    public BookServiceImpl(BookRepository bookRepository, AuthorRepository authorRepository,
            CategoryRepository categoryRepository) {
        this.bookRepository = bookRepository;
        this.authorRepository = authorRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public BookDTO addBook(BookDTO dto) {
        Author author = authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.getAuthorId()));
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + dto.getCategoryId()));

        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setIsbn(dto.getIsbn());
        book.setDescription(dto.getDescription());
        book.setPrice(dto.getPrice());
        book.setAuthor(author);
        book.setCategory(category);

        Book saved = bookRepository.save(book);

        return BookDTO.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .isbn(saved.getIsbn())
                .description(saved.getDescription())
                .price(saved.getPrice())
                .authorId(saved.getAuthor().getId())
                .categoryId(saved.getCategory().getId())
                .build();
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .authorId(book.getAuthor().getId())
                .categoryId(book.getCategory().getId())
                .build();
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(book -> BookDTO.builder()
                        .id(book.getId())
                        .title(book.getTitle())
                        .isbn(book.getIsbn())
                        .description(book.getDescription())
                        .price(book.getPrice())
                        .authorId(book.getAuthor().getId())
                        .categoryId(book.getCategory().getId())
                        .build())
                .toList();
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        Author author = authorRepository.findById(bookDTO.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + bookDTO.getAuthorId()));
        Category category = categoryRepository.findById(bookDTO.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + bookDTO.getCategoryId()));
        book.setTitle(bookDTO.getTitle());
        book.setIsbn(bookDTO.getIsbn());
        book.setDescription(bookDTO.getDescription());
        book.setPrice(bookDTO.getPrice());
        book.setAuthor(author);
        book.setCategory(category);
        Book updated = bookRepository.save(book);
        return BookDTO.builder()
                .id(updated.getId())
                .title(updated.getTitle())
                .isbn(updated.getIsbn())
                .description(updated.getDescription())
                .price(updated.getPrice())
                .authorId(updated.getAuthor().getId())
                .categoryId(updated.getCategory().getId())
                .build();

    }

    @Override
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }
}
