package com.example.book_webstore.controller;

import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.CartItemDTO;
import com.example.book_webstore.dto.CouponValidationDTO;
import com.example.book_webstore.service.CartService;
import com.example.book_webstore.service.CouponService;
import jakarta.servlet.http.HttpSession;
import java.security.Principal;
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
    private static final String APPLIED_COUPON_CODE_SESSION_KEY = "APPLIED_COUPON_CODE";
    private final CartService cartService;
    private final CouponService couponService;

    public CartController(CartService cartService, CouponService couponService) {
        this.cartService = cartService;
        this.couponService = couponService;
    }

    // Chỉ giữ một Mapping duy nhất cho trang giỏ hàng
    @GetMapping("/cart")
    public String cartPage(Model model,
            HttpSession session,
            Principal principal,
            @RequestParam(value = "message", required = false) String message) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());
        CouponValidationDTO couponResult = resolveCouponForCart(session, cart.getId(), principal);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cartService.getItemCount(cart.getId()));
        model.addAttribute("cartTotal", calculateCartTotal(cart));
        model.addAttribute("couponResult", couponResult);
        model.addAttribute("appliedCouponCode", session.getAttribute(APPLIED_COUPON_CODE_SESSION_KEY));
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

    @PostMapping("/cart/apply-coupon")
    public String applyCoupon(@RequestParam("code") String code,
            HttpSession session,
            Principal principal,
            RedirectAttributes redirectAttributes) {
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        if (cartId == null) {
            redirectAttributes.addFlashAttribute("message", "Chưa có giỏ hàng để áp dụng coupon.");
            return "redirect:/cart";
        }

        try {
            CouponValidationDTO result = couponService.validateCouponForCart(code, cartId,
                    principal != null ? principal.getName() : null);
            session.setAttribute(APPLIED_COUPON_CODE_SESSION_KEY, result.getCode());
            redirectAttributes.addFlashAttribute("message", "Áp dụng coupon thành công: " + result.getCode());
        } catch (Exception e) {
            session.removeAttribute(APPLIED_COUPON_CODE_SESSION_KEY);
            redirectAttributes.addFlashAttribute("message", extractErrorMessage(e));
        }
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove-coupon")
    public String removeCoupon(HttpSession session, RedirectAttributes redirectAttributes) {
        session.removeAttribute(APPLIED_COUPON_CODE_SESSION_KEY);
        redirectAttributes.addFlashAttribute("message", "Đã gỡ coupon khỏi giỏ hàng.");
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

    private CouponValidationDTO resolveCouponForCart(HttpSession session, Long cartId, Principal principal) {
        Object couponCode = session.getAttribute(APPLIED_COUPON_CODE_SESSION_KEY);
        if (couponCode == null || cartId == null || principal == null) {
            return null;
        }

        try {
            return couponService.validateCouponForCart(String.valueOf(couponCode), cartId, principal.getName());
        } catch (Exception e) {
            session.removeAttribute(APPLIED_COUPON_CODE_SESSION_KEY);
            return null;
        }
    }

    private String extractErrorMessage(Exception e) {
        if (e instanceof org.springframework.web.server.ResponseStatusException responseStatusException
                && responseStatusException.getReason() != null) {
            return responseStatusException.getReason();
        }
        return e.getMessage() == null ? "Không thể áp dụng coupon." : e.getMessage();
    }
}
