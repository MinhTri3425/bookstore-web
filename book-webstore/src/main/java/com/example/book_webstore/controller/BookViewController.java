package com.example.book_webstore.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.book_webstore.service.facade.BookQueryFacade;
import com.example.book_webstore.dto.BookDTO;

import java.util.List;

@Controller
@RequestMapping("/books")
public class BookViewController {

    private final BookQueryFacade bookQueryFacade;

    public BookViewController(BookQueryFacade bookQueryFacade) {
        this.bookQueryFacade = bookQueryFacade;
    }

    // 1. Hiển thị danh sách sách cho khách hàng
    @GetMapping
    public String showBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        List<BookDTO> books;

        // Xử lý logic lọc sách
        if ((keyword != null && !keyword.isBlank()) || categoryId != null || authorId != null || minPrice != null
                || maxPrice != null) {
            books = bookQueryFacade.filterBooks(keyword, categoryId, authorId, minPrice, maxPrice);
        } else {
            books = bookQueryFacade.getAllBooks();
        }

        // Đổ dữ liệu ra Model để JSP hiển thị
        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("authorId", authorId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        // Cần thiết cho bộ lọc Sidebar trong JSP
        model.addAttribute("authors", bookQueryFacade.getAllAuthors());
        model.addAttribute("categories", bookQueryFacade.getAllCategories());

        // LƯU Ý: Đổi từ "books/index" sang "books/list" nếu ông đặt tên file là
        // list.jsp
        return "books/index";
    }

    // 2. Hiển thị chi tiết một cuốn sách
    @GetMapping("/{id}")
    public String showBookDetail(@PathVariable Long id, Model model) {
        BookDTO book = bookQueryFacade.getBookById(id);

        if (book == null) {
            return "redirect:/books";
        }

        model.addAttribute("book", book);
        // Trả về file detail.jsp trong thư mục views/books/
        return "books/detail";
    }
}