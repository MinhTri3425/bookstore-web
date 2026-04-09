package com.example.book_webstore.controller;

import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.CartItemDTO;
import com.example.book_webstore.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class CartController {
    private static final String CART_SESSION_KEY = "CART_ID";
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    // Chỉ giữ một Mapping duy nhất cho trang giỏ hàng
    @GetMapping("/cart")
    public String cartPage(Model model,
            HttpSession session,
            @RequestParam(value = "message", required = false) String message) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cartService.getItemCount(cart.getId()));
        model.addAttribute("cartTotal", calculateCartTotal(cart));
        model.addAttribute("message", message);

        // Theo ảnh: thư mục là 'cart', file là 'cart.jsp' (viết thường toàn bộ)
        return "cart/cart";
    }

    // Nếu muốn hỗ trợ các link cũ, hãy đổi đường dẫn khác, KHÔNG trùng với /cart ở
    // trên
    @GetMapping("/shop/cart")
    public String cartPageAlias() {
        return "redirect:/cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(@RequestParam("bookId") Long bookId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.addToCart(cartId, bookId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        redirectAttributes.addFlashAttribute("message", "Đã thêm sách vào giỏ hàng!");
        return "redirect:/cart"; // Thêm xong thì chuyển hướng thẳng về giỏ hàng để xem
    }

    @PostMapping("/cart/remove")
    public String removeFromCart(@RequestParam("bookId") Long bookId,
            @RequestParam(value = "redirectTo", defaultValue = "cart") String redirectTo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        cartService.removeFromCart(cartId, bookId);

        redirectAttributes.addFlashAttribute("message", "Đã xóa sách khỏi giỏ hàng");

        return "redirect:/cart";
    }

    private BigDecimal calculateCartTotal(CartDTO cart) {
        if (cart == null || cart.getItems() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal total = BigDecimal.ZERO;
        for (CartItemDTO item : cart.getItems()) {
            if (item == null || item.getBook() == null || item.getBook().getPrice() == null) {
                continue;
            }
            total = total.add(item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        return total;
    }
}