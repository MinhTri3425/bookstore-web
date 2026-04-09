package com.example.book_webstore.controller;

import com.example.book_webstore.dto.CategoryDTO;
import com.example.book_webstore.service.CategoryService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    private final CategoryService categoryService;

    public AdminCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // 1. Hiển thị danh sách
    @GetMapping
    public String listCategories(Model model) {
        model.addAttribute("categories", categoryService.getAllCategories());
        return "admin/categories";
    }

    // 2. Mở form thêm mới
    @GetMapping("/add")
    public String addCategoryForm(Model model) {
        // Tên "category" phải khớp với modelAttribute trong JSP
        model.addAttribute("category", new CategoryDTO());
        // Lấy danh sách để chọn danh mục cha
        model.addAttribute("parentCategories", categoryService.getAllCategories());
        return "admin/category-form";
    }

    // 3. Mở form chỉnh sửa
    @GetMapping("/edit")
    public String editCategoryForm(@RequestParam Long id, Model model) {
        CategoryDTO category = categoryService.getCategoryById(id);
        if (category == null) {
            return "redirect:/admin/categories";
        }

        model.addAttribute("category", category);
        model.addAttribute("parentCategories", categoryService.getAllCategories());
        return "admin/category-form";
    }

    // 4. Lưu dữ liệu (Add & Update)
    // Sửa @ModelAttribute để chỉ định rõ định danh "category"
    @PostMapping("/save")
    public String saveCategory(@ModelAttribute("category") CategoryDTO dto, RedirectAttributes ra) {
        try {
            categoryService.saveCategory(dto);
            ra.addFlashAttribute("successMessage", "Lưu danh mục thành công! 🏷️");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    // 5. Xóa danh mục
    @PostMapping("/delete")
    public String deleteCategory(@RequestParam Long id, RedirectAttributes ra) {
        try {
            categoryService.deleteCategory(id);
            ra.addFlashAttribute("successMessage", "Đã xóa danh mục thành công!");
        } catch (DataIntegrityViolationException e) {
            // Lỗi ràng buộc khóa ngoại (đang có sách hoặc category con gắn vào)
            ra.addFlashAttribute("errorMessage", "Không thể xóa: Danh mục này đang chứa sách hoặc có danh mục con!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Lỗi hệ thống: " + e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}