package com.example.book_webstore.service.facade;

import com.example.book_webstore.dto.BookDTO;

public interface BookCommandFacade {

    BookDTO createBookFull(BookDTO dto);

    BookDTO updateBook(Long id, BookDTO dto);

    void deleteBook(Long id);
}
