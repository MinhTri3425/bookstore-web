package com.example.book_webstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookImageDTO {
    private Long id;
    private String url;
    private String altText;
    private int sortOrder;
    private Long bookId;
}
