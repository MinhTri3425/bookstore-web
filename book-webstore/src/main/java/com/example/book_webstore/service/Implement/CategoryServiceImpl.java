package com.example.book_webstore.service.Implement;

import com.example.book_webstore.model.Category;
import com.example.book_webstore.dto.CategoryDTO;
import com.example.book_webstore.repository.CategoryRepository;
import com.example.book_webstore.service.CategoryService;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryDTO addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();

        category.setName(categoryDTO.getName());
        category.setSlug(categoryDTO.getSlug());

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Parent category not found with id: " + categoryDTO.getParentId()));
            category.setParent(parent);
        }

        Category savedCategory = categoryRepository.save(category);
        return new CategoryDTO(savedCategory.getId(), savedCategory.getName(), savedCategory.getSlug(),
                savedCategory.getParent() != null ? savedCategory.getParent().getId() : null);

    }

    @Override
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return new CategoryDTO(category.getId(), category.getName(), category.getSlug(),
                category.getParent() != null ? category.getParent().getId() : null);
    }

    @Override
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName(), category.getSlug(),
                        category.getParent() != null ? category.getParent().getId() : null))
                .toList();
    }

    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(categoryDTO.getName());
        category.setSlug(categoryDTO.getSlug());

        if (categoryDTO.getParentId() != null) {
            Category parent = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new RuntimeException(
                            "Parent category not found with id: " + categoryDTO.getParentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category updatedCategory = categoryRepository.save(category);
        return new CategoryDTO(updatedCategory.getId(), updatedCategory.getName(), updatedCategory.getSlug(),
                updatedCategory.getParent() != null ? updatedCategory.getParent().getId() : null);

    }

    @Override
    public void deleteCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        if (!categoryRepository.existsByParentId(id)) {
            throw new RuntimeException("Cannot delete category with existing subcategories");
        }
        categoryRepository.delete(category);

    }
}
