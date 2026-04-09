package com.example.book_webstore.service;

import java.util.List;

import com.example.book_webstore.dto.BookImageDTO;

public interface BookImageService {
    List<BookImageDTO> addBookImages(Long bookId, List<BookImageDTO> bookImageDTOs);

    BookImageDTO getBookImageById(Long id);

    void deleteBookImage(Long id);

    List<BookImageDTO> getBookImagesByBookId(Long bookId);

    void deleteImagesByBookId(Long bookId);
}
