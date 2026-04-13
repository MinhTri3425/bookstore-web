package com.example.book_webstore.controller;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.service.BookService;
import com.example.book_webstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class BookTestController {
    private static final String CART_SESSION_KEY = "CART_ID";

    private final BookService bookService;
    private final CartService cartService;

    public BookTestController(BookService bookService, CartService cartService) {
        this.bookService = bookService;
        this.cartService = cartService;
    }

    // @GetMapping("/")
    // public String homeRedirect() {
    // return "redirect:/BookTest/";
    // }

    @GetMapping({ "/BookTest", "/BookTest/" })
    public String booksPage(Model model,
            HttpSession session,
            @RequestParam(value = "message", required = false) String message) {
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        List<BookDTO> books = bookService.getAllBooksCart();
        model.addAttribute("books", books);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cartService.getItemCount(cart.getId()));
        model.addAttribute("message", message);
        return "BookTest";
    }
}
