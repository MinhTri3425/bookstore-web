package com.example.book_webstore.service.impl;

import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.repository.AuthorRepository;
import com.example.book_webstore.service.AuthorService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorServiceImpl(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    @Override
    public AuthorDTO addAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setName(authorDTO.getName());
        Author saved = authorRepository.save(author);
        return toDTO(saved);
    }

    @Override
    public AuthorDTO getAuthorById(Long id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        return toDTO(author);
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Author not found"));
        author.setName(authorDTO.getName());
        Author updated = authorRepository.save(author);
        return toDTO(updated);
    }

    @Override
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDTO toDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setId(author.getId());
        dto.setName(author.getName());
        return dto;
    }
}
