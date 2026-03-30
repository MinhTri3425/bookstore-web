package com.example.book_webstore.service;

import com.example.book_webstore.dto.AuthorDTO;
import java.util.List;

public interface AuthorService {
        AuthorDTO addAuthor(AuthorDTO authorDTO);

        AuthorDTO getAuthorById(Long id);

        List<AuthorDTO> getAllAuthors();

        AuthorDTO updateAuthor(Long id, AuthorDTO authorDTO);

        void deleteAuthor(Long id);
}