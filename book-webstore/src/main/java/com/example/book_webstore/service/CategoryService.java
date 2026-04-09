package com.example.book_webstore.service;

import com.example.book_webstore.dto.CategoryDTO;
import java.util.List;

public interface CategoryService {
    CategoryDTO addCategory(CategoryDTO categoryDTO);

    CategoryDTO getCategoryById(Long id);

    List<CategoryDTO> getAllCategories();

    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);

    void deleteCategory(Long id);

    void saveCategory(CategoryDTO dto);
}
