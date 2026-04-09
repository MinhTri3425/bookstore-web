package com.example.book_webstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.BookImageDTO;
import com.example.book_webstore.service.facade.BookQueryFacade;
import com.example.book_webstore.service.facade.BookCommandFacade;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookQueryFacade bookQueryFacade;
    private final BookCommandFacade bookCommandFacade;

    public AdminController(BookQueryFacade bookQueryFacade,
            BookCommandFacade bookCommandFacade) {
        this.bookQueryFacade = bookQueryFacade;
        this.bookCommandFacade = bookCommandFacade;
    }

    // 1. Dashboard - Thống kê
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<BookDTO> allBooks = bookQueryFacade.getAllBooks();

        int totalBooks = allBooks.size();
        int inStockBooks = (int) allBooks.stream()
                .filter(b -> b.getStock() != null && b.getStock() > 0)
                .count();
        int outOfStockBooks = totalBooks - inStockBooks;

        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("inStockBooks", inStockBooks);
        model.addAttribute("outOfStockBooks", outOfStockBooks);
        // Trả về admin/dashboard.jsp
        return "admin/dashboard";
    }

    // 2. Quản lý danh sách sách
    @GetMapping("/books")
    public String manageBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String authorId,
            Model model) {

        Long catId = (categoryId == null || categoryId.isBlank()) ? null : Long.parseLong(categoryId);
        Long authId = (authorId == null || authorId.isBlank()) ? null : Long.parseLong(authorId);

        List<BookDTO> books;
        if ((keyword != null && !keyword.isBlank()) || catId != null || authId != null) {
            books = bookQueryFacade.filterBooks(keyword, catId, authId, null, null);
        } else {
            books = bookQueryFacade.getAllBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", catId);
        model.addAttribute("authorId", authId);
        model.addAttribute("authors", bookQueryFacade.getAllAuthors());
        model.addAttribute("categories", bookQueryFacade.getAllCategories());

        return "admin/books";
    }

    // 3. Form thêm sách mới
    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new BookDTO());
        model.addAttribute("authors", bookQueryFacade.getAllAuthors());
        model.addAttribute("categories", bookQueryFacade.getAllCategories());
        return "admin/book-form";
    }

    // 4. Form sửa sách
    @GetMapping("/books/edit")
    public String editBookForm(@RequestParam Long id, Model model) {
        BookDTO book = bookQueryFacade.getBookById(id);
        if (book == null) {
            return "redirect:/admin/books";
        }

        model.addAttribute("book", book);
        model.addAttribute("authors", bookQueryFacade.getAllAuthors());
        model.addAttribute("categories", bookQueryFacade.getAllCategories());
        model.addAttribute("currentStock", book.getStock());

        return "admin/book-form";
    }

    // 5. Lưu sách (Hợp nhất thêm & sửa + Upload ảnh)
    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute("book") BookDTO book,
            @RequestParam("extraImages") MultipartFile[] files,
            RedirectAttributes ra) {

        try {
            List<BookImageDTO> imageDTOs = new ArrayList<>();

            // Xử lý upload ảnh vật lý
            if (files != null && files.length > 0 && !files[0].isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/";
                Path uploadPath = Paths.get(uploadDir);

                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

                    try (InputStream inputStream = file.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    BookImageDTO imgDTO = BookImageDTO.builder()
                            .url("/uploads/" + fileName)
                            .altText(book.getTitle() + " - " + (i + 1))
                            .sortOrder(i)
                            .build();
                    imageDTOs.add(imgDTO);
                }
                book.setImages(imageDTOs);
            } else if (book.getId() != null) {
                // Nếu update mà không chọn ảnh mới, giữ lại ảnh cũ
                BookDTO existingBook = bookQueryFacade.getBookById(book.getId());
                book.setImages(existingBook.getImages());
            }

            // Validate sơ bộ
            validateBook(book);

            // Lưu vào DB
            if (book.getId() != null && book.getId() > 0) {
                bookCommandFacade.updateBook(book.getId(), book);
                ra.addFlashAttribute("successMessage", "Cập nhật sách thành công! 🔥");
            } else {
                if (book.getImages() == null || book.getImages().isEmpty()) {
                    throw new RuntimeException("Vui lòng chọn ít nhất một ảnh cho sách mới!");
                }
                bookCommandFacade.createBookFull(book);
                ra.addFlashAttribute("successMessage", "Thêm mới sách thành công! 🔥");
            }

        } catch (Exception e) {
            e.printStackTrace();
            ra.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            return book.getId() == null ? "redirect:/admin/books/add" : "redirect:/admin/books/edit?id=" + book.getId();
        }

        return "redirect:/admin/books";
    }

    // 6. Xóa sách
    @PostMapping("/books/delete")
    public String deleteBook(@RequestParam Long id, RedirectAttributes ra) {
        try {
            bookCommandFacade.deleteBook(id);
            ra.addFlashAttribute("successMessage", "Đã xóa sách khỏi hệ thống! 👍");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Không thể xóa sách: " + e.getMessage());
        }
        return "redirect:/admin/books";
    }

    // Hàm validate hỗ trợ
    private void validateBook(BookDTO book) {
        if (book.getTitle() == null || book.getTitle().isBlank())
            throw new RuntimeException("Thiếu tiêu đề");
        if (book.getIsbn() == null || book.getIsbn().isBlank())
            throw new RuntimeException("Thiếu ISBN");
        if (book.getPrice() == null)
            throw new RuntimeException("Thiếu giá");
        if (book.getAuthorId() == null)
            throw new RuntimeException("Chưa chọn tác giả");
        if (book.getCategoryId() == null)
            throw new RuntimeException("Chưa chọn danh mục");
    }
}