package com.example.book_webstore.service.Implement;

import com.example.book_webstore.model.Category;
import com.example.book_webstore.dto.CategoryDTO;
import com.example.book_webstore.repository.CategoryRepository;
import com.example.book_webstore.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;

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
        // 1. Kiểm tra xem danh mục có tồn tại không
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }

        // 2. CHUẨN: Nếu TÌM THẤY (không có dấu !) danh mục con thì mới báo lỗi
        if (categoryRepository.existsByParentId(id)) {
            throw new RuntimeException("Cannot delete category with existing subcategories");
        }

        // 3. Nếu không vướng gì thì xóa
        categoryRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void saveCategory(CategoryDTO dto) {
        Category category;

        // 1. Kiểm tra là thêm mới hay cập nhật
        if (dto.getId() != null) {
            category = categoryRepository.findById(dto.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
        } else {
            category = new Category();
        }

        // 2. Gán các trường cơ bản
        category.setName(dto.getName());

        // Tự sinh slug nếu người dùng không nhập (Ví dụ: "Sách Kinh Tế" ->
        // "sach-kinh-te")
        if (dto.getSlug() == null || dto.getSlug().isBlank()) {
            category.setSlug(generateSlug(dto.getName()));
        } else {
            category.setSlug(dto.getSlug());
        }

        // 3. Xử lý danh mục cha (Parent Category)
        if (dto.getParentId() != null) {
            Category parent = categoryRepository.findById(dto.getParentId())
                    .orElseThrow(() -> new RuntimeException("Danh mục cha không tồn tại"));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        categoryRepository.save(category);
    }

    // Hàm bổ trợ tạo Slug đơn giản
    private String generateSlug(String input) {
        return input.toLowerCase()
                .replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a")
                .replaceAll("[éèẻẽẹêếềểễệ]", "e")
                .replaceAll("[íìỉĩị]", "i")
                .replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o")
                .replaceAll("[úùủũụưứừửữự]", "u")
                .replaceAll("[ýỳỷỹỵ]", "y")
                .replaceAll("đ", "d")
                .replaceAll("[^a-z0-9\\s]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
