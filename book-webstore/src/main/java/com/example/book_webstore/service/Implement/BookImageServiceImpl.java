package com.example.book_webstore.service.Implement;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.model.BookImage;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.repository.BookImageRepository;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.service.BookImageService;

@Service
public class BookImageServiceImpl implements BookImageService {
    private final BookImageRepository bookImageRepository;
    private final BookRepository bookRepository;

    public BookImageServiceImpl(BookImageRepository bookImageRepository, BookRepository bookRepository) {
        this.bookImageRepository = bookImageRepository;
        this.bookRepository = bookRepository;
    }

    @Override
    public List<BookImageDTO> addBookImages(Long bookId, List<BookImageDTO> dtos) {

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new RuntimeException("Book not found"));

        List<BookImage> images = dtos.stream().map(dto -> {
            BookImage img = new BookImage();
            img.setUrl(dto.getUrl());
            img.setAltText(dto.getAltText());
            img.setSortOrder(dto.getSortOrder());
            img.setBook(book);
            return img;
        }).toList();

        List<BookImage> saved = bookImageRepository.saveAll(images);

        return saved.stream().map(img -> BookImageDTO.builder()
                .id(img.getId())
                .url(img.getUrl())
                .altText(img.getAltText())
                .sortOrder(img.getSortOrder())
                .bookId(bookId)
                .build()).toList();
    }

    @Override
    public BookImageDTO getBookImageById(Long id) {
        BookImage bookImage = bookImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book image not found with id: " + id));

        return BookImageDTO.builder()
                .id(bookImage.getId())
                .url(bookImage.getUrl())
                .altText(bookImage.getAltText())
                .sortOrder(bookImage.getSortOrder())
                .bookId(bookImage.getBook().getId())
                .build();
    }

    @Override
    public void deleteBookImage(Long id) {
        if (!bookImageRepository.existsById(id)) {
            throw new RuntimeException("Book image not found with id: " + id);
        }
        bookImageRepository.deleteById(id);
    }

    @Override
    public List<BookImageDTO> getBookImagesByBookId(Long bookId) {
        List<BookImage> images = bookImageRepository.findByBookId(bookId);

        return images.stream().map(img -> BookImageDTO.builder()
                .id(img.getId())
                .url(img.getUrl())
                .altText(img.getAltText())
                .sortOrder(img.getSortOrder())
                .bookId(img.getBook().getId())
                .build()).toList();
    }

    @Override
    @Transactional
    public void deleteImagesByBookId(Long bookId) {
        List<BookImage> images = bookImageRepository.findByBookId(bookId);
        bookImageRepository.deleteAll(images);
    }
}
