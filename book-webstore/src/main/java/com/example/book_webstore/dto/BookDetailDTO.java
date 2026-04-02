package com.example.book_webstore.dto;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@Builder
public class BookDetailDTO {
    private Long id;
    private String title;
    private String isbn;
    private String description;
    private BigDecimal price;

    private AuthorDTO author;
    private CategoryDTO category;
    private List<BookImageDTO> images;

    private int stock;
}
