package com.example.book_webstore.service.facade;

import com.example.book_webstore.dto.BookDetailDTO;
import com.example.book_webstore.dto.BookDTO;

public interface ProductFacade {
    BookDetailDTO getBookDetailById(Long id);

    BookDetailDTO createBookFull(BookDTO dto);
}
