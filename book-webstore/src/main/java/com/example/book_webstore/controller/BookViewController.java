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

    @GetMapping
    public String showBooks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long authorId,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        List<BookDTO> books;

        // Nếu có bộ lọc, dùng hàm filter, nếu không thì lấy tất cả
        if (keyword != null || categoryId != null || authorId != null || minPrice != null || maxPrice != null) {
            books = bookQueryFacade.filterBooks(keyword, categoryId, authorId, minPrice, maxPrice);
        } else {
            books = bookQueryFacade.getAllBooks();
        }

        model.addAttribute("books", books);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("authorId", authorId);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);
        model.addAttribute("authors", bookQueryFacade.getAllAuthors());
        model.addAttribute("categories", bookQueryFacade.getAllCategories());

        return "books/index";
    }

    @GetMapping("/{id}")
    public String showBookDetail(@PathVariable Long id, Model model) {
        BookDTO book = bookQueryFacade.getBookById(id);
        if (book == null) {
            return "redirect:/books";
        }

        model.addAttribute("book", book);
        return "books/detail";
    }
}
