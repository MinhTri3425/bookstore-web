package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.Category;
import com.example.book_webstore.repository.AuthorRepository;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.CategoryRepository;
import com.example.book_webstore.service.BookService;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@Transactional
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
                                .orElseThrow(() -> new RuntimeException(
                                                "Author not found with id: " + dto.getAuthorId()));
                Category category = categoryRepository.findById(dto.getCategoryId())
                                .orElseThrow(() -> new RuntimeException(
                                                "Category not found with id: " + dto.getCategoryId()));

                Book book = new Book();
                book.setTitle(dto.getTitle());
                book.setIsbn(dto.getIsbn());
                book.setDescription(dto.getDescription());
                book.setPrice(dto.getPrice());
                book.setStock(dto.getStock() != null ? dto.getStock() : 0); // 🔥 ADD THIS

                book.setAuthor(author);
                book.setCategory(category);

                Book saved = bookRepository.save(book);

                return mapToDTO(saved);
        }

        @Override
        public BookDTO getBookById(Long id) {
                Book book = bookRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

                return mapToDTO(book);
        }

        @Override
        public List<BookDTO> getAllBooks() {
                return bookRepository.findAll().stream()
                                .map(this::mapToDTO)
                                .toList();
        }

        @Override
        @Transactional
        public BookDTO updateBook(Long id, BookDTO dto) {

                Book book = bookRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

                if (dto.getTitle() != null)
                        book.setTitle(dto.getTitle());
                if (dto.getIsbn() != null)
                        book.setIsbn(dto.getIsbn());
                if (dto.getDescription() != null)
                        book.setDescription(dto.getDescription());
                if (dto.getPrice() != null)
                        book.setPrice(dto.getPrice());

                if (dto.getStock() != null) {
                        book.setStock(dto.getStock());
                }

                if (dto.getAuthorId() != null) {
                        Author author = authorRepository.findById(dto.getAuthorId())
                                        .orElseThrow(() -> new RuntimeException("Author not found"));
                        book.setAuthor(author);
                }

                if (dto.getCategoryId() != null) {
                        Category category = categoryRepository.findById(dto.getCategoryId())
                                        .orElseThrow(() -> new RuntimeException("Category not found"));
                        book.setCategory(category);
                }

                return mapToDTO(bookRepository.save(book));
        }

        @Override
        @Transactional
        public void deleteBook(Long id) {

                Book book = bookRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

                bookRepository.delete(book);
        }

        @Override
        public List<BookDTO> searchBooks(String keyword) {
                return bookRepository.findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(keyword, keyword)
                                .stream()
                                .map(this::mapToDTO)
                                .toList();
        }

        @Override
        public List<BookDTO> filterBooks(String keyword,
                        Long categoryId,
                        Long authorId,
                        Double minPrice,
                        Double maxPrice) {

                Specification<Book> spec = (root, query, cb) -> cb.conjunction();

                if (keyword != null && !keyword.isBlank()) {
                        spec = spec.and((root, query, cb) -> cb.like(
                                        cb.lower(root.get("title")),
                                        "%" + keyword.toLowerCase() + "%"));
                }

                if (categoryId != null) {
                        spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
                }

                if (authorId != null) {
                        spec = spec.and((root, query, cb) -> cb.equal(root.get("author").get("id"), authorId));
                }

                if (minPrice != null) {
                        spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
                }

                if (maxPrice != null) {
                        spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
                }

                return bookRepository.findAll(spec)
                                .stream()
                                .map(this::mapToDTO)
                                .toList();
        }

        private BookDTO mapToDTO(Book book) {
                return BookDTO.builder()
                                .id(book.getId())
                                .title(book.getTitle())
                                .isbn(book.getIsbn())
                                .description(book.getDescription())
                                .price(book.getPrice())
                                .stock(book.getStock())
                                .authorId(book.getAuthor().getId())
                                .categoryId(book.getCategory().getId())
                                .build();
        }

        @Override
        @Transactional(readOnly = true)
        public List<BookDTO> getAllBooksCart() {
                return bookRepository.findAllWithImages().stream().map(this::mapToDTO).collect(Collectors.toList());
        }
}
