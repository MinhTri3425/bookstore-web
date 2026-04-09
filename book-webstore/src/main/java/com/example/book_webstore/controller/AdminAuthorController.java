package com.example.book_webstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.book_webstore.dto.AuthorDTO;
import com.example.book_webstore.service.AuthorService;

@Controller
@RequestMapping("/admin/authors")
public class AdminAuthorController {

    private final AuthorService authorService;

    public AdminAuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    // 1. Danh sách tác giả
    @GetMapping
    public String listAuthors(Model model) {
        model.addAttribute("authors", authorService.getAllAuthors());
        return "admin/authors"; // Trỏ đến file authors.html trong folder admin
    }

    // 2. Form thêm mới
    @GetMapping("/add")
    public String addAuthorForm(Model model) {
        model.addAttribute("author", new AuthorDTO());
        return "admin/author-form";
    }

    // 3. Form chỉnh sửa
    @GetMapping("/edit")
    public String editAuthorForm(@RequestParam Long id, Model model) {
        AuthorDTO author = authorService.getAuthorById(id);
        if (author == null)
            return "redirect:/admin/authors";

        model.addAttribute("author", author);
        return "admin/author-form";
    }

    // 4. Lưu dữ liệu (Hợp nhất Add và Update)
    @PostMapping("/save")
    public String saveAuthor(@ModelAttribute AuthorDTO author, RedirectAttributes ra) {
        try {
            if (author.getId() != null) {
                authorService.updateAuthor(author.getId(), author);
                ra.addFlashAttribute("successMessage", "Cập nhật tác giả thành công! ✨");
            } else {
                authorService.addAuthor(author);
                ra.addFlashAttribute("successMessage", "Thêm tác giả mới thành công! 🖋️");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/admin/authors";
    }

    // 5. Xóa tác giả
    @PostMapping("/delete")
    public String deleteAuthor(@RequestParam Long id, RedirectAttributes ra) {
        try {
            authorService.deleteAuthor(id);
            ra.addFlashAttribute("successMessage", "Đã xóa tác giả thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa tác giả này (có thể do đang có sách liên kết)!");
        }
        return "redirect:/admin/authors";
    }
}