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

        return "admin/dashboard";
    }

    @GetMapping("/books")
    public String manageBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String authorId,
            Model model) {

        Long catId = (categoryId == null || categoryId.isBlank())
                ? null
                : Long.parseLong(categoryId);

        Long authId = (authorId == null || authorId.isBlank())
                ? null
                : Long.parseLong(authorId);

        List<BookDTO> books;

        if ((keyword != null && !keyword.isBlank())
                || catId != null
                || authId != null) {

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

    @GetMapping("/books/add")
    public String addBookForm(Model model) {
        model.addAttribute("book", new BookDTO());
        model.addAttribute("authors", bookQueryFacade.getAllAuthors());
        model.addAttribute("categories", bookQueryFacade.getAllCategories());
        return "admin/book-form";
    }

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

    @PostMapping("/books/save")
    public String saveBook(@ModelAttribute BookDTO book,
            @RequestParam("extraImages") MultipartFile[] files, // Nhận mảng nhiều file
            RedirectAttributes redirectAttributes) {

        try {
            List<BookImageDTO> imageDTOs = new ArrayList<>();

            // 1. Kiểm tra nếu người dùng có chọn file mới
            if (files != null && files.length > 0 && !files[0].isEmpty()) {
                String uploadDir = "src/main/resources/static/uploads/";

                for (int i = 0; i < files.length; i++) {
                    MultipartFile file = files[i];

                    // Tạo tên file duy nhất
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    Path uploadPath = Paths.get(uploadDir);

                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }

                    // Lưu file vật lý
                    try (InputStream inputStream = file.getInputStream()) {
                        Path filePath = uploadPath.resolve(fileName);
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    }

                    // Đóng gói vào BookImageDTO
                    BookImageDTO imgDTO = BookImageDTO.builder()
                            .url("/uploads/" + fileName)
                            .altText(book.getTitle() + " - " + (i + 1))
                            .sortOrder(i)
                            .build();

                    imageDTOs.add(imgDTO);
                }

                // Gán danh sách ảnh mới vào book
                book.setImages(imageDTOs);

            } else if (book.getId() != null) {
                // Nếu là UPDATE và không chọn ảnh mới, lấy lại danh sách ảnh cũ từ DB
                BookDTO existingBook = bookQueryFacade.getBookById(book.getId());
                book.setImages(existingBook.getImages());
            }

            // 2. Validate cơ bản
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

            // 3. Gọi Facade lưu
            if (book.getId() != null && book.getId() > 0) {
                bookCommandFacade.updateBook(book.getId(), book);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thành công album ảnh! 🔥");
            } else {
                // Thêm mới thì bắt buộc phải có ít nhất 1 cái ảnh
                if (book.getImages() == null || book.getImages().isEmpty()) {
                    throw new RuntimeException("Vui lòng chọn ít nhất một ảnh cho sách mới!");
                }
                bookCommandFacade.createBookFull(book);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm mới sách và album ảnh thành công! 🔥");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi: " + e.getMessage());
            // Nếu lỗi, quay lại trang add hoặc edit tùy vào ID
            return book.getId() == null ? "redirect:/admin/books/add" : "redirect:/admin/books/edit?id=" + book.getId();
        }

        return "redirect:/admin/books";
    }

    @PostMapping("/books/delete")
    public String deleteBook(@RequestParam Long id,
            RedirectAttributes redirectAttributes) {

        try {
            bookCommandFacade.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa xong rồi 👍");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Fail: " + e.getMessage());
        }

        return "redirect:/admin/books";
    }
}