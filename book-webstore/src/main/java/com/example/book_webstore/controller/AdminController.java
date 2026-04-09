package com.example.book_webstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.service.facade.BookQueryFacade;
import com.example.book_webstore.service.facade.BookCommandFacade;

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
            RedirectAttributes redirectAttributes) {

        try {
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

            if (book.getId() != null && book.getId() > 0) {

                bookCommandFacade.updateBook(book.getId(), book);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật ok rồi nha 🔥");

            } else {

                bookCommandFacade.createBookFull(book);
                redirectAttributes.addFlashAttribute("successMessage", "Thêm mới ok luôn 🔥");
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/books/add";
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