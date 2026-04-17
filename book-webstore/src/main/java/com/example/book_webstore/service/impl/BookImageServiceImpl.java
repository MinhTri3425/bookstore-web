package com.example.book_webstore.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Transactional
    public List<BookImageDTO> addBookImages(Long bookId, List<BookImageDTO> dtos) {
        // Sử dụng getReferenceById để lấy Proxy Object (không tốn câu lệnh SELECT xuống
        // DB)
        Book book = bookRepository.getReferenceById(bookId);

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
    @Transactional
    public void deleteBookImage(Long id) {
        BookImage img = bookImageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book image not found"));

        // Xóa file vật lý
        deletePhysicalFile(img.getUrl());

        bookImageRepository.delete(img);
    }

    @Override
    public List<BookImageDTO> getBookImagesByBookId(Long bookId) {
        return bookImageRepository.findByBookId(bookId).stream().map(img -> BookImageDTO.builder()
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

        // Duyệt danh sách để xóa file vật lý từng cái
        for (BookImage img : images) {
            deletePhysicalFile(img.getUrl());
        }

        // Xóa sạch record trong Database
        bookImageRepository.deleteAll(images);
    }

    /**
     * Hàm hỗ trợ xóa file vật lý
     */
    private void deletePhysicalFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty())
            return;

        try {
            // Loại bỏ dấu "/" ở đầu nếu có để tránh nối chuỗi sai
            String relativePath = imageUrl.startsWith("/") ? imageUrl.substring(1) : imageUrl;

            // Sử dụng System.getProperty("user.dir") để lấy thư mục gốc của project
            // Giúp đường dẫn chính xác hơn ở nhiều môi trường
            Path filePath = Paths.get(System.getProperty("user.dir"), "src/main/resources/static", relativePath);

            if (Files.exists(filePath)) {
                Files.delete(filePath);
                System.out.println("--- ĐÃ XÓA FILE THÀNH CÔNG: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("--- LỖI KHI XÓA FILE: " + e.getMessage());
        }
    }
}