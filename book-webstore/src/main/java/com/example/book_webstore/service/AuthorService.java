package com.example.book_webstore.service;

import org.springframework.stereotype.Service;
import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.model.Author;
import com.example.book_webstore.repository.AuthorRepository;
import java.util.List;

@Service
public class AuthorService {
    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public AuthorDTO addAuthor(AuthorDTO authorDTO) {
        Author author = new Author();
        author.setName(authorDTO.getName());
        author.setDescription(authorDTO.getDescription());
        author.setStatus(authorDTO.getStatus());
        Author savedAuthor = authorRepository.save(author);
        return new AuthorDTO(savedAuthor.getId(), savedAuthor.getName(), savedAuthor.getDescription(),
                savedAuthor.getStatus());
    }

    public List<AuthorDTO> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(author -> new AuthorDTO(author.getId(), author.getName(), author.getDescription(),
                        author.getStatus()))
                .toList();
    }

    public AuthorDTO updateAuthor(Long id, AuthorDTO updatedAuthor) {
        Author author = authorRepository.findById(id).orElseThrow(() -> new RuntimeException("Author not found"));
        author.setName(updatedAuthor.getName());
        author.setDescription(updatedAuthor.getDescription());
        author.setStatus(updatedAuthor.getStatus());
        Author savedAuthor = authorRepository.save(author);
        return new AuthorDTO(savedAuthor.getId(), savedAuthor.getName(), savedAuthor.getDescription(),
                savedAuthor.getStatus());
    }

    public void deleteAuthor(Long id) {
        authorRepository.deleteById(id);
    }
}
