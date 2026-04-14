package com.example.book_webstore.controller;

import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.CartItemDTO;
import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.repository.CouponRepository;
import com.example.book_webstore.service.CartService;
import com.example.book_webstore.service.OrderService;
import com.example.book_webstore.service.payment.strategy.PaymentStrategyResolver;
import com.example.book_webstore.service.payment.vnpay.VnPayService;
import com.example.book_webstore.service.strategy.coupon.CouponCalculationStrategy;
import com.example.book_webstore.service.strategy.shipping.ShippingCostStrategy;
import com.example.book_webstore.service.strategy.coupon.CouponStrategyFactory;
import com.example.book_webstore.service.strategy.shipping.ShippingCostStrategyFactory;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.security.Principal;
import java.util.Optional;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Controller
public class CartController {
    private static final String CART_SESSION_KEY = "CART_ID";
    private static final String APPLIED_COUPON_CODE_SESSION_KEY = "APPLIED_COUPON_CODE";
    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;
    private final PaymentStrategyResolver paymentStrategyResolver;
    private final ShippingCostStrategyFactory shippingStrategyFactory;
    private final CouponStrategyFactory couponStrategyFactory;
    private final CouponRepository couponRepository;
    private final VnPayService vnPayService;

    public CartController(CartService cartService,
            OrderService orderService,
            UserRepository userRepository,
            PaymentStrategyResolver paymentStrategyResolver,
            ShippingCostStrategyFactory shippingStrategyFactory,
            CouponStrategyFactory couponStrategyFactory,
            CouponRepository couponRepository,
            VnPayService vnPayService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.paymentStrategyResolver = paymentStrategyResolver;
        this.shippingStrategyFactory = shippingStrategyFactory;
        this.couponStrategyFactory = couponStrategyFactory;
        this.couponRepository = couponRepository;
        this.vnPayService = vnPayService;
    }

    // Hỗ trợ cả đường dẫn cũ (/Cart) và đường dẫn chuẩn (/cart)
    @GetMapping({ "/cart", "/Cart", "/Cart/" })
    public String cartPage(Model model,
            HttpSession session,
            Authentication authentication,
            Principal principal,
            @RequestParam(value = "message", required = false) String message) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cartService.getItemCount(cart.getId()));
        model.addAttribute("cartTotal", calculateCartTotal(cart));
        model.addAttribute("appliedCouponCode", session.getAttribute(APPLIED_COUPON_CODE_SESSION_KEY));
        model.addAttribute("message", message);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());

        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            model.addAttribute("userAddresses", cartService.getUserAddresses(authentication.getName()));
        }

        return "cart/cart";
    }

    @GetMapping("/api/checkout/preview")
    @ResponseBody // Trả về JSON
    public Map<String, Object> previewOrder(
            @RequestParam String shippingMethod,
            @RequestParam List<Long> bookIds,
            @RequestParam(required = false) String couponCode,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        // 1. Lấy giỏ hàng và tính Subtotal cho các sách được chọn
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        List<CartItemDTO> selectedItems = filterSelectedItems(cart, bookIds);

        BigDecimal subtotal = selectedItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Gọi Strategy tính phí ship (Không cần OrderId vì tính nháp)
        BigDecimal shippingFee = BigDecimal.ZERO;
        try {
            Shipping.ShippingMethod method = Shipping.ShippingMethod.valueOf(shippingMethod.toUpperCase());
            ShippingCostStrategy strategy = shippingStrategyFactory.getStrategy(method);
            if (strategy != null) {
                // Vì bạn dùng Long orderId trong strategy, ở đây ta truyền null hoặc -1
                // Nếu strategy của bạn check DB, hãy đảm bảo nó handle được trường hợp null
                shippingFee = strategy.calculateShippingCost(null);
            }
        } catch (Exception ignored) {
        }

        // 3. Gọi Strategy tính giảm giá coupon
        BigDecimal discount = BigDecimal.ZERO;
        if (couponCode != null && !couponCode.isBlank()) {
            Optional<Coupon> couponOpt = couponRepository.findByCodeIgnoreCase(couponCode.trim());
            if (couponOpt.isPresent()) {
                Coupon coupon = couponOpt.get();
                CouponCalculationStrategy strategy = couponStrategyFactory.getStrategy(coupon.getType());
                if (strategy != null) {
                    discount = strategy.calculateDiscount(coupon, subtotal);
                }
            }
        }

        // 4. Tổng hợp dữ liệu trả về
        BigDecimal finalTotal = subtotal.add(shippingFee).subtract(discount).max(BigDecimal.ZERO);

        response.put("subtotal", subtotal);
        response.put("shippingFee", shippingFee);
        response.put("discount", discount);
        response.put("finalTotal", finalTotal);

        return response;
    }

    // Nếu muốn hỗ trợ các link cũ, hãy đổi đường dẫn khác, KHÔNG trùng với /cart ở
    // trên
    @GetMapping("/shop/cart")
    public String cartPageAlias() {
        return "redirect:/cart";
    }

    @PostMapping({ "/cart/add", "/Cart/add" })
    public String addToCart(@RequestParam("bookId") Long bookId,
            @RequestParam(value = "quantity", defaultValue = "1") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.addToCart(cartId, bookId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        redirectAttributes.addFlashAttribute("message", "Đã thêm sách vào giỏ hàng!");
        return "redirect:/books";
    }

    @PostMapping({ "/cart/remove", "/Cart/remove" })
    public String removeFromCart(@RequestParam("bookId") Long bookId,
            @RequestParam(value = "redirectTo", defaultValue = "cart") String redirectTo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        cartService.removeFromCart(cartId, bookId);

        redirectAttributes.addFlashAttribute("message", "Đã xóa sách khỏi giỏ hàng");

        return "redirect:/cart";
    }

    @PostMapping({ "/cart/update", "/Cart/update" })
    public String updateCartItemQuantity(@RequestParam("bookId") Long bookId,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);

        try {
            CartDTO cart = cartService.updateItemQuantity(cartId, bookId, quantity);
            session.setAttribute(CART_SESSION_KEY, cart.getId());
            redirectAttributes.addFlashAttribute("message", "Đã cập nhật số lượng sản phẩm");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkoutPage(@RequestParam(value = "selectedBookIds", required = false) List<Long> selectedBookIds,
            HttpSession session,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (selectedBookIds == null || selectedBookIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn sản phẩm trước khi đặt hàng");
            return "redirect:/cart";
        }

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng đăng nhập để đặt hàng");
            return "redirect:/login";
        }

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        List<CartItemDTO> selectedItems = filterSelectedItems(cart, selectedBookIds);
        if (selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Các sản phẩm đã chọn không hợp lệ");
            return "redirect:/cart";
        }

        CartDTO checkoutCart = new CartDTO();
        checkoutCart.setId(cart.getId());
        checkoutCart.setItems(selectedItems);

        return renderCheckoutPage(model, authentication.getName(), selectedItems, selectedBookIds,
                calculateCartTotal(checkoutCart), null, null);
    }

    @PostMapping("/checkout/address/add")
    public String addCheckoutAddress(
            @RequestParam(value = "selectedBookIds", required = false) List<Long> selectedBookIds,
            @RequestParam("street") String street,
            @RequestParam("ward") String ward,
            @RequestParam("district") String district,
            @RequestParam("city") String city,
            HttpSession session,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (selectedBookIds == null || selectedBookIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn sản phẩm trước khi đặt hàng");
            return "redirect:/cart";
        }

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng đăng nhập để đặt hàng");
            return "redirect:/login";
        }

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        List<CartItemDTO> selectedItems = filterSelectedItems(cart, selectedBookIds);
        if (selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Các sản phẩm đã chọn không hợp lệ");
            return "redirect:/cart";
        }

        CartDTO checkoutCart = new CartDTO();
        checkoutCart.setId(cart.getId());
        checkoutCart.setItems(selectedItems);

        Long selectedAddressId = null;
        String message;
        try {
            selectedAddressId = cartService.addUserAddress(authentication.getName(), street, ward, district, city)
                    .getId();
            message = "Đã thêm địa chỉ mới thành công";
        } catch (IllegalArgumentException ex) {
            message = ex.getMessage();
        }

        return renderCheckoutPage(model, authentication.getName(), selectedItems, selectedBookIds,
                calculateCartTotal(checkoutCart), selectedAddressId, message);
    }

    @PostMapping("/checkout/place")
    public String checkoutSelectedItems(
            @RequestParam(value = "selectedBookIds", required = false) List<Long> selectedBookIds,
            @RequestParam(value = "selectedAddressId", required = false) Long selectedAddressId,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "paymentMethod", defaultValue = "CASH") Payment.PaymentMethod paymentMethod,

            // CẦN THÊM 2 DÒNG NÀY ĐỂ NHẬN DỮ LIỆU TỪ JSP
            @RequestParam(value = "shippingMethod", defaultValue = "STANDARD") String shippingMethod,
            @RequestParam(value = "couponCode", required = false) String couponCode,

            HttpServletRequest request,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (selectedBookIds == null || selectedBookIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng chọn sản phẩm trước khi đặt hàng");
            return "redirect:/cart";
        }

        if (authentication == null || authentication.getName() == null || authentication.getName().isBlank()) {
            redirectAttributes.addFlashAttribute("message", "Vui lòng đăng nhập để đặt hàng");
            return "redirect:/login";
        }

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        Long orderId;

        try {
            // Cập nhật hàm này để lưu cả shippingMethod và couponCode vào Order trước
            orderId = cartService.checkoutSelectedItems(
                    cartId, selectedBookIds, authentication.getName(),
                    selectedAddressId, receiverName, phoneNumber, note, paymentMethod,
                    shippingMethod, couponCode); // Truyền thêm vào đây

            // SAU ĐÓ MỚI GỌI HÀM NÀY ĐỂ TÍNH TIỀN CHUẨN
            orderService.refreshOrderTotal(orderId);

        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/cart";
        }

        // Logic xử lý VNPAY và Redirect giữ nguyên như bạn đã viết...
        if (paymentMethod == Payment.PaymentMethod.VNPAY) {
            try {
                BigDecimal orderPaymentAmount = cartService.getOrderPaymentAmount(orderId);
                String returnUrl = ServletUriComponentsBuilder.fromRequestUri(request)
                        .replacePath(request.getContextPath() + "/payment/vnpay-return")
                        .replaceQuery(null).build().toUriString();
                String clientIp = extractClientIp(request);
                String paymentUrl = paymentStrategyResolver.resolve(paymentMethod)
                        .createCheckoutPaymentUrl(orderId, orderPaymentAmount, clientIp, returnUrl);
                return "redirect:" + paymentUrl;
            } catch (Exception ex) {
                redirectAttributes.addFlashAttribute("message", "Lỗi thanh toán: " + ex.getMessage());
                return "redirect:/cart";
            }
        }

        return "redirect:/checkout/success?orderId=" + orderId;
    }

    @GetMapping("/checkout/success")
    public String checkoutSuccess(@RequestParam("orderId") Long orderId,
            Model model) {
        attachPaymentOrder(model, orderId);
        model.addAttribute("paymentSuccess", true);
        model.addAttribute("paymentTitle", "Đặt hàng thành công");
        model.addAttribute("paymentMessage",
                "Đơn hàng #" + orderId + " đã được ghi nhận. Bạn sẽ thanh toán khi nhận hàng.");
        model.addAttribute("orderId", orderId);
        model.addAttribute("responseCode", "");
        model.addAttribute("transactionNo", "");
        return "order/payment-result";
    }

    @GetMapping("/payment/vnpay-return")
    public String vnPayReturn(@RequestParam Map<String, String> queryParams,
            Model model) {
        boolean validSignature = paymentStrategyResolver
                .resolve(Payment.PaymentMethod.VNPAY)
                .validateCallbackSignature(queryParams);
        if (!validSignature) {
            model.addAttribute("paymentSuccess", false);
            model.addAttribute("paymentTitle", "Chữ ký VNPAY không hợp lệ");
            model.addAttribute("paymentMessage", "Hệ thống không xác thực được dữ liệu trả về từ VNPAY.");
            model.addAttribute("orderId", queryParams.getOrDefault("vnp_TxnRef", ""));
            model.addAttribute("responseCode", queryParams.getOrDefault("vnp_ResponseCode", ""));
            model.addAttribute("transactionNo", queryParams.getOrDefault("vnp_TransactionNo", ""));
            return "order/payment-result";
        }

        Long orderId;
        try {
            orderId = vnPayService.extractOrderIdFromTxnRef(queryParams.getOrDefault("vnp_TxnRef", ""));
        } catch (IllegalArgumentException ex) {
            model.addAttribute("paymentSuccess", false);
            model.addAttribute("paymentTitle", "Không đọc được mã đơn hàng");
            model.addAttribute("paymentMessage", "Dữ liệu trả về từ VNPAY không chứa mã đơn hợp lệ.");
            model.addAttribute("orderId", queryParams.getOrDefault("vnp_TxnRef", ""));
            model.addAttribute("responseCode", queryParams.getOrDefault("vnp_ResponseCode", ""));
            model.addAttribute("transactionNo", queryParams.getOrDefault("vnp_TransactionNo", ""));
            return "order/payment-result";
        }
        attachPaymentOrder(model, orderId);

        String responseCode = queryParams.getOrDefault("vnp_ResponseCode", "");
        String transactionStatus = queryParams.getOrDefault("vnp_TransactionStatus", "");
        boolean paid = "00".equals(responseCode) && "00".equals(transactionStatus);

        try {
            cartService.updatePaymentStatus(orderId, paid);
        } catch (IllegalArgumentException ex) {
            model.addAttribute("paymentSuccess", false);
            model.addAttribute("paymentTitle", "Cập nhật thanh toán thất bại");
            model.addAttribute("paymentMessage", ex.getMessage());
            model.addAttribute("orderId", orderId);
            model.addAttribute("responseCode", responseCode);
            model.addAttribute("transactionNo", queryParams.getOrDefault("vnp_TransactionNo", ""));
            return "order/payment-result";
        }

        if (paid) {
            model.addAttribute("paymentSuccess", true);
            model.addAttribute("paymentTitle", "Thanh toán thành công");
            model.addAttribute("paymentMessage", "Đơn hàng #" + orderId + " đã được thanh toán qua VNPAY.");
        } else {
            model.addAttribute("paymentSuccess", false);
            model.addAttribute("paymentTitle", "Thanh toán thất bại");
            model.addAttribute("paymentMessage", "VNPAY trả về kết quả thất bại cho đơn #" + orderId + ".");
        }
        model.addAttribute("orderId", orderId);
        model.addAttribute("responseCode", responseCode);
        model.addAttribute("transactionNo", queryParams.getOrDefault("vnp_TransactionNo", ""));
        return "order/payment-result";
    }

    private void attachPaymentOrder(Model model, Long orderId) {
        try {
            CustomerOrderDTO order = orderService.getAdminOrderDetail(orderId);
            model.addAttribute("paymentOrder", order);
        } catch (Exception ex) {
            // Keep payment result page available even when order detail loading fails.
            model.addAttribute("paymentOrder", null);
        }
    }

    private List<CartItemDTO> filterSelectedItems(CartDTO cart, List<Long> selectedBookIds) {
        if (cart == null || cart.getItems() == null || selectedBookIds == null || selectedBookIds.isEmpty()) {
            return List.of();
        }

        Set<Long> selectedSet = new HashSet<>(selectedBookIds);
        return cart.getItems().stream()
                .filter(item -> item.getBook() != null && selectedSet.contains(item.getBook().getId()))
                .toList();
    }

    private String renderCheckoutPage(Model model,
            String customerEmail,
            List<CartItemDTO> selectedItems,
            List<Long> selectedBookIds,
            BigDecimal selectedTotal,
            Long selectedAddressId,
            String message) {
        User user = userRepository.findByEmail(customerEmail);

        model.addAttribute("selectedItems", selectedItems);
        model.addAttribute("selectedBookIds", selectedBookIds);
        model.addAttribute("selectedTotal", selectedTotal);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
        model.addAttribute("userAddresses", cartService.getUserAddresses(customerEmail));
        model.addAttribute("selectedAddressId", selectedAddressId);
        model.addAttribute("receiverName", user != null && user.getName() != null ? user.getName() : "");
        model.addAttribute("phoneNumber", user != null && user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        model.addAttribute("message", message);
        return "order/checkout";
    }

    private String extractClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
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
