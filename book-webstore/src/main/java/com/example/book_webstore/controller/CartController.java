package com.example.book_webstore.controller;

import com.example.book_webstore.dto.CartDTO;
import com.example.book_webstore.dto.CartItemDTO;
import com.example.book_webstore.dto.CheckoutCouponOptionDTO;
import com.example.book_webstore.dto.CustomerOrderDTO;
import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.CouponUsage;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.Shipping;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.CouponUsageRepository;
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
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;

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
    private final CouponUsageRepository couponUsageRepository;
    private final VnPayService vnPayService;

    public CartController(CartService cartService,
            OrderService orderService,
            UserRepository userRepository,
            PaymentStrategyResolver paymentStrategyResolver,
            ShippingCostStrategyFactory shippingStrategyFactory,
            CouponStrategyFactory couponStrategyFactory,
            CouponRepository couponRepository,
            CouponUsageRepository couponUsageRepository,
            VnPayService vnPayService) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.userRepository = userRepository;
        this.paymentStrategyResolver = paymentStrategyResolver;
        this.shippingStrategyFactory = shippingStrategyFactory;
        this.couponStrategyFactory = couponStrategyFactory;
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.vnPayService = vnPayService;
    }

    // Hỗ trợ cả đường dẫn cũ (/Cart) và đường dẫn chuẩn (/cart)
    @GetMapping({ "/cart", "/Cart", "/Cart/" })
    public String cartPage(Model model,
            HttpSession session,
            Authentication authentication,
            Principal principal,
            @RequestParam(value = "message", required = false) String message) {

        CartDTO cart = resolveCart(session, authentication, principal);

        model.addAttribute("cart", cart);
        model.addAttribute("cartItemCount", cartService.getItemCount(cart.getId(), resolveCustomerEmail(authentication, principal)));
        model.addAttribute("cartTotal", calculateCartTotal(cart));
        model.addAttribute("appliedCouponCode", session.getAttribute(APPLIED_COUPON_CODE_SESSION_KEY));
        model.addAttribute("message", message);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());

        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            model.addAttribute("userAddresses", cartService.getUserAddresses(authentication.getName()));
        }

        return "Cart/Cart";
    }

    @GetMapping("/api/checkout/preview")
    @ResponseBody // Trả về JSON
    public Map<String, Object> previewOrder(
            @RequestParam String shippingMethod,
            @RequestParam List<Long> bookIds,
            @RequestParam(required = false) String productCouponCode,
            @RequestParam(required = false) String shippingCouponCode,
            HttpSession session,
            Authentication authentication) {

        Map<String, Object> response = new HashMap<>();

        // 1. Lấy giỏ hàng và tính Subtotal cho các sách được chọn
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId, resolveCustomerEmail(authentication, null));
        session.setAttribute(CART_SESSION_KEY, cart.getId());
        List<CartItemDTO> selectedItems = filterSelectedItems(cart, bookIds);

        BigDecimal subtotal = selectedItems.stream()
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Gọi Strategy tính phí ship
        BigDecimal shippingFee = resolveShippingFee(shippingMethod);

        // 3. Validate và tính giảm cho coupon sản phẩm + ship
        List<CheckoutCouponOptionDTO> productOptions = buildCouponOptions(selectedItems, subtotal, shippingFee,
                Coupon.CouponTarget.PRODUCT, authentication != null ? authentication.getName() : null);
        List<CheckoutCouponOptionDTO> shippingOptions = buildCouponOptions(selectedItems, subtotal, shippingFee,
                Coupon.CouponTarget.SHIPPING, authentication != null ? authentication.getName() : null);

        BigDecimal productDiscount = resolveDiscountFromSelectedCode(productOptions, productCouponCode, selectedItems,
                subtotal, shippingFee);
        BigDecimal shippingDiscount = resolveDiscountFromSelectedCode(shippingOptions, shippingCouponCode,
                selectedItems,
                subtotal, shippingFee);

        // 4. Tổng hợp dữ liệu trả về
        BigDecimal finalTotal = subtotal
                .subtract(productDiscount)
                .add(shippingFee)
                .subtract(shippingDiscount)
                .max(BigDecimal.ZERO)
                .setScale(2, RoundingMode.HALF_UP);

        response.put("subtotal", subtotal);
        response.put("shippingFee", shippingFee);
        response.put("productDiscount", productDiscount);
        response.put("shippingDiscount", shippingDiscount);
        response.put("discount", productDiscount.add(shippingDiscount));
        response.put("finalTotal", finalTotal);
        response.put("productCoupons", productOptions);
        response.put("shippingCoupons", shippingOptions);

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
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        String customerEmail = resolveCustomerEmail(authentication, null);
        CartDTO cart = cartService.addToCart(cartId, customerEmail, bookId, quantity);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        redirectAttributes.addFlashAttribute("message", "Đã thêm sách vào giỏ hàng!");
        return "redirect:/books";
    }

    @PostMapping({ "/cart/remove", "/Cart/remove" })
    public String removeFromCart(@RequestParam("bookId") Long bookId,
            @RequestParam(value = "redirectTo", defaultValue = "cart") String redirectTo,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        String customerEmail = resolveCustomerEmail(authentication, null);
        CartDTO cart = cartService.removeFromCart(cartId, customerEmail, bookId);
        session.setAttribute(CART_SESSION_KEY, cart.getId());

        redirectAttributes.addFlashAttribute("message", "Đã xóa sách khỏi giỏ hàng");

        return "redirect:/cart";
    }

    @PostMapping({ "/cart/update", "/Cart/update" })
    public String updateCartItemQuantity(@RequestParam("bookId") Long bookId,
            @RequestParam("quantity") int quantity,
            HttpSession session,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        String customerEmail = resolveCustomerEmail(authentication, null);

        try {
            CartDTO cart = cartService.updateItemQuantity(cartId, customerEmail, bookId, quantity);
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

        CartDTO cart = resolveCart(session, authentication, null);

        List<CartItemDTO> selectedItems = filterSelectedItems(cart, selectedBookIds);
        if (selectedItems.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Các sản phẩm đã chọn không hợp lệ");
            return "redirect:/cart";
        }

        CartDTO checkoutCart = new CartDTO();
        checkoutCart.setId(cart.getId());
        checkoutCart.setItems(selectedItems);

        return renderCheckoutPage(model, authentication.getName(), selectedItems, selectedBookIds,
                calculateCartTotal(checkoutCart), null, null, "STANDARD", null, null);
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

        CartDTO cart = resolveCart(session, authentication, null);

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
                calculateCartTotal(checkoutCart), selectedAddressId, message, "STANDARD", null, null);
    }

    @PostMapping("/checkout/place")
    public String checkoutSelectedItems(
            @RequestParam(value = "selectedBookIds", required = false) List<Long> selectedBookIds,
            @RequestParam(value = "selectedAddressId", required = false) Long selectedAddressId,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "note", required = false) String note,
            @RequestParam(value = "paymentMethod", defaultValue = "CASH") Payment.PaymentMethod paymentMethod,

            @RequestParam(value = "shippingMethod", defaultValue = "STANDARD") String shippingMethod,
            @RequestParam(value = "productCouponCode", required = false) String productCouponCode,
            @RequestParam(value = "shippingCouponCode", required = false) String shippingCouponCode,

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
        CartDTO cart = cartService.getOrCreateCart(cartId, authentication.getName());
        session.setAttribute(CART_SESSION_KEY, cart.getId());
        Long orderId;

        try {
            orderId = cartService.checkoutSelectedItems(
                cart.getId(), selectedBookIds, authentication.getName(),
                    selectedAddressId, receiverName, phoneNumber, note, paymentMethod,
                    shippingMethod, productCouponCode, shippingCouponCode);

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
            String message,
            String shippingMethod,
            String selectedProductCouponCode,
            String selectedShippingCouponCode) {
        User user = userRepository.findByEmail(customerEmail);

        BigDecimal shippingFee = resolveShippingFee(shippingMethod);
        List<CheckoutCouponOptionDTO> productCoupons = buildCouponOptions(selectedItems, selectedTotal, shippingFee,
                Coupon.CouponTarget.PRODUCT, customerEmail);
        List<CheckoutCouponOptionDTO> shippingCoupons = buildCouponOptions(selectedItems, selectedTotal, shippingFee,
                Coupon.CouponTarget.SHIPPING, customerEmail);

        model.addAttribute("selectedItems", selectedItems);
        model.addAttribute("selectedBookIds", selectedBookIds);
        model.addAttribute("selectedTotal", selectedTotal);
        model.addAttribute("selectedShippingMethod", shippingMethod == null ? "STANDARD" : shippingMethod);
        model.addAttribute("selectedProductCouponCode", selectedProductCouponCode);
        model.addAttribute("selectedShippingCouponCode", selectedShippingCouponCode);
        model.addAttribute("productCoupons", productCoupons);
        model.addAttribute("shippingCoupons", shippingCoupons);
        model.addAttribute("paymentMethods", Payment.PaymentMethod.values());
        model.addAttribute("userAddresses", cartService.getUserAddresses(customerEmail));
        model.addAttribute("selectedAddressId", selectedAddressId);
        model.addAttribute("receiverName", user != null && user.getName() != null ? user.getName() : "");
        model.addAttribute("phoneNumber", user != null && user.getPhoneNumber() != null ? user.getPhoneNumber() : "");
        model.addAttribute("message", message);
        return "order/checkout";
    }

    private BigDecimal resolveShippingFee(String shippingMethod) {
        try {
            Shipping.ShippingMethod method = Shipping.ShippingMethod.valueOf(shippingMethod.toUpperCase());
            ShippingCostStrategy strategy = shippingStrategyFactory.getStrategy(method);
            return strategy == null ? BigDecimal.ZERO : strategy.calculateShippingCost(null);
        } catch (Exception ex) {
            return BigDecimal.ZERO;
        }
    }

    private List<CheckoutCouponOptionDTO> buildCouponOptions(List<CartItemDTO> selectedItems,
            BigDecimal subtotal,
            BigDecimal shippingFee,
            Coupon.CouponTarget target,
            String customerEmail) {
        List<Coupon> coupons = couponRepository.findAllByTargetOrderByIdDesc(target);
        List<CheckoutCouponOptionDTO> options = new ArrayList<>();
        for (Coupon coupon : coupons) {
            String reason = evaluateCouponEligibilityReason(coupon, selectedItems, subtotal, shippingFee,
                    customerEmail);
            boolean eligible = reason == null;
            options.add(new CheckoutCouponOptionDTO(
                    coupon.getId(),
                    coupon.getCode(),
                    coupon.getType(),
                    coupon.getTarget(),
                    formatCouponValue(coupon),
                    eligible,
                    eligible ? "Đủ điều kiện áp dụng" : reason));
        }
        return options;
    }

    private String evaluateCouponEligibilityReason(Coupon coupon,
            List<CartItemDTO> selectedItems,
            BigDecimal subtotal,
            BigDecimal shippingFee,
            String customerEmail) {
        if (!coupon.isActive()) {
            return "Coupon đang tắt";
        }
        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
            return "Chưa tới thời gian áp dụng";
        }
        if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
            return "Coupon đã hết hạn";
        }
        if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            return "Chưa đủ giá trị đơn tối thiểu";
        }

        if (customerEmail != null && !customerEmail.isBlank()) {
            User customer = userRepository.findByEmail(customerEmail);
            if (customer != null) {
                CouponUsage usage = couponUsageRepository.findByCouponIdAndCustomerId(coupon.getId(), customer.getId())
                        .orElse(null);
                int usedCount = usage != null ? usage.getUsageCount() : 0;
                if (usedCount >= coupon.getMaxUsePerUser()) {
                    return "Đã hết lượt dùng của bạn";
                }
            }
        }
        if (coupon.getTotalUsageLimit() != null) {
            long totalUsage = couponUsageRepository.sumUsageCountByCouponId(coupon.getId());
            if (totalUsage >= coupon.getTotalUsageLimit()) {
                return "Coupon đã hết lượt";
            }
        }

        if (coupon.getTarget() == Coupon.CouponTarget.PRODUCT) {
            BigDecimal applicableSubtotal = calculateApplicableSubtotal(coupon, selectedItems);
            if (applicableSubtotal.compareTo(BigDecimal.ZERO) <= 0) {
                return "Không áp dụng cho sản phẩm đã chọn";
            }
        }
        if (coupon.getTarget() == Coupon.CouponTarget.SHIPPING && shippingFee.compareTo(BigDecimal.ZERO) <= 0) {
            return "Không có phí ship để áp mã";
        }
        return null;
    }

    private BigDecimal resolveDiscountFromSelectedCode(List<CheckoutCouponOptionDTO> options,
            String selectedCode,
            List<CartItemDTO> selectedItems,
            BigDecimal subtotal,
            BigDecimal shippingFee) {
        if (selectedCode == null || selectedCode.isBlank()) {
            return BigDecimal.ZERO;
        }
        CheckoutCouponOptionDTO selected = options.stream()
                .filter(option -> option.getCode().equalsIgnoreCase(selectedCode.trim()))
                .findFirst()
                .orElse(null);
        if (selected == null || !selected.isEligible()) {
            return BigDecimal.ZERO;
        }

        Coupon coupon = couponRepository.findByCodeIgnoreCase(selected.getCode()).orElse(null);
        if (coupon == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal base = coupon.getTarget() == Coupon.CouponTarget.SHIPPING
                ? shippingFee
                : calculateApplicableSubtotal(coupon, selectedItems);
        if (base.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        CouponCalculationStrategy strategy = couponStrategyFactory.getStrategy(coupon.getType());
        BigDecimal discount = strategy.calculateDiscount(coupon, base);
        if (coupon.getMaxDiscountValue() != null && discount.compareTo(coupon.getMaxDiscountValue()) > 0) {
            discount = coupon.getMaxDiscountValue();
        }
        if (discount.compareTo(base) > 0) {
            discount = base;
        }
        if (coupon.getTarget() == Coupon.CouponTarget.PRODUCT && discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }
        return discount.max(BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateApplicableSubtotal(Coupon coupon, List<CartItemDTO> selectedItems) {
        if (coupon.getApplicableBooks() == null || coupon.getApplicableBooks().isEmpty()) {
            return selectedItems.stream()
                    .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        Set<Long> applicableBookIds = coupon.getApplicableBooks().stream().map(book -> book.getId())
                .collect(java.util.stream.Collectors.toSet());
        return selectedItems.stream()
                .filter(item -> item.getBook() != null && applicableBookIds.contains(item.getBook().getId()))
                .map(item -> item.getBook().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private String formatCouponValue(Coupon coupon) {
        if (coupon.getType() == Coupon.CouponType.PERCENTAGE) {
            return coupon.getValue().stripTrailingZeros().toPlainString() + "%";
        }
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("vi-VN"));
        return nf.format(coupon.getValue()) + " VND";
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

    private CartDTO resolveCart(HttpSession session, Authentication authentication, Principal principal) {
        Long cartId = (Long) session.getAttribute(CART_SESSION_KEY);
        CartDTO cart = cartService.getOrCreateCart(cartId, resolveCustomerEmail(authentication, principal));
        session.setAttribute(CART_SESSION_KEY, cart.getId());
        return cart;
    }

    private String resolveCustomerEmail(Authentication authentication, Principal principal) {
        if (authentication != null && authentication.getName() != null && !authentication.getName().isBlank()) {
            return authentication.getName();
        }
        if (principal != null && principal.getName() != null && !principal.getName().isBlank()) {
            return principal.getName();
        }
        return null;
    }

}
