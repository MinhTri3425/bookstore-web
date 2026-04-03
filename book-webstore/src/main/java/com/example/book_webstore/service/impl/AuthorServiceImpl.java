package com.example.book_webstore.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.repository.AuthorRepository;
import com.example.book_webstore.service.AuthorService;

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
        return toDto(authorRepository.save(author));
    }

    @Override
    public AuthorDTO getAuthorById(Long id) {
        return authorRepository.findById(id)
                .map(this::toDto)
                .orElse(null);
    }

    @Override
    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    public AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO) {
        return authorRepository.findById(id)
                .map(author -> {
                    author.setName(authorDTO.getName());
                    return toDto(authorRepository.save(author));
                })
                .orElse(null);
    }

    @Override
    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }

    private AuthorDTO toDto(Author author) {
        return new AuthorDTO(author.getId(), author.getName());
    }
}
