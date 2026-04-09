package com.example.book_webstore.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookDTO {
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private BigDecimal price;

    private Long authorId;
    private String authorName; // Thêm tên tác giả
    private Long categoryId;
    private String categoryName; // Thêm tên danh mục
    private List<BookImageDTO> images;

    // Dùng cho request, không cần thiết khi trả về client
    private Integer stock;
}
