package com.example.book_webstore.service.Implement;

import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.repository.AuthorRepository;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.service.AuthorService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AuthorServiceImpl implements AuthorService {

    private final AuthorRepository authorRepository;
    private final BookRepository bookRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository, BookRepository bookRepository) {
        this.authorRepository = authorRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public AuthorDTO addAuthor(AuthorDTO dto) {

        Author author = new Author();
        author.setName(dto.getName());

        Author saved = authorRepository.save(author);

        return AuthorDTO.builder()
                .id(saved.getId())
                .name(saved.getName())
                .build();
    }

    @Override
    public AuthorDTO getAuthorById(Long id) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        return AuthorDTO.builder()
                .id(author.getId())
                .name(author.getName())
                .build();
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {

        return authorRepository.findAll().stream().map(author -> AuthorDTO.builder()
                .id(author.getId())
                .name(author.getName())
                .build()).toList();
    }

    @Override
    public AuthorDTO updateAuthor(Long id, AuthorDTO dto) {

        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + id));

        author.setName(dto.getName());

        Author updated = authorRepository.save(author);

        return AuthorDTO.builder()
                .id(updated.getId())
                .name(updated.getName())
                .build();
    }

    @Override
    public void deleteAuthor(Long id) {

        if (!authorRepository.existsById(id)) {
            throw new RuntimeException("Author not found with id: " + id);
        }
        if (bookRepository.existsByAuthorId(id)) {
            throw new RuntimeException("Cannot delete author with existing books");
        }

        authorRepository.deleteById(id);
    }
}