package com.example.book_webstore.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.book_webstore.dto.BookDTO;
import com.example.book_webstore.dto.CouponDTO;
import com.example.book_webstore.dto.CouponUsageDTO;
import com.example.book_webstore.dto.CouponValidationDTO;
import com.example.book_webstore.model.Book;
import com.example.book_webstore.model.CartItem;
import com.example.book_webstore.model.Coupon;
import com.example.book_webstore.model.CouponUsage;
import com.example.book_webstore.model.CustomerOrder;
import com.example.book_webstore.model.Payment;
import com.example.book_webstore.model.User;
import com.example.book_webstore.repository.BookRepository;
import com.example.book_webstore.repository.CartItemRepository;
import com.example.book_webstore.repository.CouponRepository;
import com.example.book_webstore.repository.CouponUsageRepository;
import com.example.book_webstore.repository.CustomerOrderRepository;
import com.example.book_webstore.repository.PaymentRepository;
import com.example.book_webstore.repository.UserRepository;
import com.example.book_webstore.service.CouponService;
import com.example.book_webstore.service.strategy.coupon.CouponCalculationStrategy;
import com.example.book_webstore.service.strategy.coupon.CouponStrategyFactory;

@Service
@Transactional
public class CouponServiceImpl implements CouponService {
    private static final String COUPON_CODE_PREFIX = "CPN";
    private static final int COUPON_CODE_LENGTH = 6;
    private static final String COUPON_CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final BookRepository bookRepository;
    private final CouponStrategyFactory couponStrategyFactory;
    private final CartItemRepository cartItemRepository;
    private final CustomerOrderRepository customerOrderRepository;
    private final UserRepository userRepository;
    private final PaymentRepository paymentRepository;

    public CouponServiceImpl(
            CouponRepository couponRepository,
            CouponUsageRepository couponUsageRepository,
            BookRepository bookRepository,
            CartItemRepository cartItemRepository,
            CustomerOrderRepository customerOrderRepository,
            UserRepository userRepository,
            PaymentRepository paymentRepository, CouponStrategyFactory couponStrategyFactory) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
        this.bookRepository = bookRepository;
        this.couponStrategyFactory = couponStrategyFactory;
        this.cartItemRepository = cartItemRepository;
        this.customerOrderRepository = customerOrderRepository;
        this.userRepository = userRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponDTO> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(this::toCouponDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<CouponDTO> getAllCoupons(org.springframework.data.domain.Pageable pageable) {
        return couponRepository.findAll(pageable)
                .map(this::toCouponDto);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponDTO getCouponById(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        return toCouponDto(coupon);
    }

    @Override
    public CouponDTO createCoupon(CouponDTO couponDTO) {
        validateCouponPayload(couponDTO, null);

        Coupon coupon = new Coupon();
        applyCouponChanges(coupon, couponDTO);
        return toCouponDto(couponRepository.save(coupon));
    }

    @Override
    public CouponDTO updateCoupon(Long id, CouponDTO couponDTO) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));

        validateCouponPayload(couponDTO, id);
        applyCouponChanges(coupon, couponDTO);
        return toCouponDto(couponRepository.save(coupon));
    }

    @Override
    public void deleteCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
        try {
            couponRepository.delete(coupon);
            couponRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Không thể xóa mã giảm giá này vì đã được áp dụng trong đơn hàng. Vui lòng vô hiệu hóa thay vì xóa.");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponUsageDTO> getCouponUsages(Long couponId) {
        if (!couponRepository.existsById(couponId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found");
        }

        return couponUsageRepository.findByCouponIdOrderByIdDesc(couponId).stream()
                .map(this::toCouponUsageDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CouponValidationDTO validateCouponForCart(String code, Long cartId, String customerEmail) {
        if (cartId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart id is required");
        }

        User customer = requireCustomer(customerEmail);
        Coupon coupon = findCouponByCode(code);
        if (coupon.getTarget() != Coupon.CouponTarget.PRODUCT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon target is not PRODUCT");
        }
        List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        CouponCalculation calculation = evaluateCoupon(coupon, customer, toBookQuantityLinesFromCart(cartItems));
        return toValidationDto(coupon, calculation, "Coupon is valid");
    }

    @Override
    public CouponValidationDTO applyCouponToOrder(Long orderId, String code, String customerEmail) {
        User customer = requireCustomer(customerEmail);
        CustomerOrder order = customerOrderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        ensureOrderOwnership(order, customer);
        if (order.getStatus() != CustomerOrder.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can apply coupons");
        }

        if (order.getCoupon() != null) {
            decrementUsage(order.getCoupon(), customer);
        }

        Coupon coupon = findCouponByCode(code);
        if (coupon.getTarget() != Coupon.CouponTarget.PRODUCT) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon target is not PRODUCT");
        }
        CouponCalculation calculation = evaluateCoupon(coupon, customer, toBookQuantityLinesFromOrder(order));

        order.setCoupon(coupon);
        order.setDiscountAmount(calculation.discountAmount());
        customerOrderRepository.save(order);

        Payment payment = ensurePayment(order, customer);
        payment.setAmount(calculation.finalTotal());
        paymentRepository.save(payment);

        incrementUsage(coupon, customer);
        return toValidationDto(coupon, calculation, "Coupon applied successfully");
    }

    @Override
    public void removeCouponFromOrder(Long orderId, String customerEmail) {
        User customer = requireCustomer(customerEmail);
        CustomerOrder order = customerOrderRepository.findDetailById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        ensureOrderOwnership(order, customer);
        if (order.getCoupon() == null) {
            return;
        }
        if (order.getStatus() != CustomerOrder.OrderStatus.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only pending orders can remove coupons");
        }

        Coupon coupon = order.getCoupon();
        decrementUsage(coupon, customer);

        order.setCoupon(null);
        order.setDiscountAmount(BigDecimal.ZERO);
        customerOrderRepository.save(order);

        Payment payment = ensurePayment(order, customer);
        payment.setAmount(calculateSubtotal(toBookQuantityLinesFromOrder(order)));
        paymentRepository.save(payment);
    }

    private Coupon findCouponByCode(String code) {
        return couponRepository.findByCodeIgnoreCase(normalizeCode(code))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Coupon not found"));
    }

    private User requireCustomer(String customerEmail) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user is required");
        }

        User customer = userRepository.findByEmail(customerEmail);
        if (customer == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        return customer;
    }

    private void validateCouponPayload(CouponDTO couponDTO, Long currentCouponId) {
        if (couponDTO == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon payload is required");
        }
        if (couponDTO.getType() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon type is required");
        }
        if (couponDTO.getTarget() == null) {
            couponDTO.setTarget(Coupon.CouponTarget.PRODUCT);
        }
        if (couponDTO.getValue() == null || couponDTO.getValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon value must be greater than zero");
        }
        if (couponDTO.getType() == Coupon.CouponType.PERCENTAGE
                && couponDTO.getValue().compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Percentage coupon cannot exceed 100%");
        }
        if (couponDTO.getMaxUsePerUser() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Max use per user must be at least 1");
        }
        if (couponDTO.getStartAt() != null && couponDTO.getEndAt() != null
                && couponDTO.getEndAt().isBefore(couponDTO.getStartAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon end time must be after start time");
        }
        if (couponDTO.getTotalUsageLimit() != null && couponDTO.getTotalUsageLimit() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Total usage limit must be at least 1");
        }
        if (couponDTO.getMinOrderValue() != null && couponDTO.getMinOrderValue().compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Minimum order value cannot be negative");
        }
        if (couponDTO.getType() == Coupon.CouponType.PERCENTAGE
                && couponDTO.getMaxDiscountValue() != null
                && couponDTO.getMaxDiscountValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Maximum discount must be greater than zero");
        }
        if (couponDTO.getTarget() == Coupon.CouponTarget.SHIPPING
                && couponDTO.getApplicableBookIds() != null
                && !couponDTO.getApplicableBookIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Shipping coupon cannot be assigned to books");
        }

        if (currentCouponId != null || hasText(couponDTO.getCode())) {
            String normalizedCode = normalizeCode(couponDTO.getCode());
            boolean duplicated = currentCouponId == null
                    ? couponRepository.existsByCodeIgnoreCase(normalizedCode)
                    : couponRepository.existsByCodeIgnoreCaseAndIdNot(normalizedCode, currentCouponId);
            if (duplicated) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon code already exists");
            }
        }
    }

    private void applyCouponChanges(Coupon coupon, CouponDTO couponDTO) {
        coupon.setCode(resolveCouponCode(coupon, couponDTO));
        coupon.setType(couponDTO.getType());
        coupon.setTarget(couponDTO.getTarget() == null ? Coupon.CouponTarget.PRODUCT : couponDTO.getTarget());
        coupon.setValue(couponDTO.getValue().setScale(2, RoundingMode.HALF_UP));
        coupon.setActive(couponDTO.isActive());
        coupon.setMaxUsePerUser(couponDTO.getMaxUsePerUser());
        coupon.setTotalUsageLimit(couponDTO.getTotalUsageLimit());
        coupon.setMinOrderValue(scaleOrNull(couponDTO.getMinOrderValue()));
        if (couponDTO.getType() == Coupon.CouponType.FIXED) {
            coupon.setMaxDiscountValue(null);
        } else {
            coupon.setMaxDiscountValue(scaleOrNull(couponDTO.getMaxDiscountValue()));
        }
        coupon.setStartAt(couponDTO.getStartAt());
        coupon.setEndAt(couponDTO.getEndAt());

        List<Book> applicableBooks = (coupon.getTarget() == Coupon.CouponTarget.SHIPPING
                || couponDTO.getApplicableBookIds() == null)
                        ? new ArrayList<>()
                        : couponDTO.getApplicableBookIds().stream()
                                .distinct()
                                .map(bookId -> bookRepository.findById(bookId)
                                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                                "Book not found: " + bookId)))
                                .collect(Collectors.toCollection(ArrayList::new));

        if (coupon.getApplicableBooks() == null) {
            coupon.setApplicableBooks(new ArrayList<>());
        } else {
            coupon.getApplicableBooks().clear();
        }
        coupon.getApplicableBooks().addAll(applicableBooks);
    }

    private CouponCalculation evaluateCoupon(Coupon coupon, User customer, List<BookQuantityLine> lines) {
        validateCouponState(coupon, customer);

        BigDecimal subtotal = calculateSubtotal(lines);
        if (subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order subtotal must be greater than zero");
        }

        if (coupon.getMinOrderValue() != null && subtotal.compareTo(coupon.getMinOrderValue()) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order does not meet coupon minimum value");
        }

        List<BookQuantityLine> applicableLines = filterApplicableLines(coupon, lines);
        if (applicableLines.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon does not apply to selected books");
        }

        BigDecimal applicableSubtotal = calculateSubtotal(applicableLines);
        CouponCalculationStrategy strategy = couponStrategyFactory.getStrategy(coupon.getType());
        BigDecimal discountAmount = strategy.calculateDiscount(coupon, applicableSubtotal);

        if (coupon.getMaxDiscountValue() != null && discountAmount.compareTo(coupon.getMaxDiscountValue()) > 0) {
            discountAmount = coupon.getMaxDiscountValue();
        }

        if (discountAmount.compareTo(applicableSubtotal) > 0) {
            discountAmount = applicableSubtotal;
        }
        if (discountAmount.compareTo(subtotal) > 0) {
            discountAmount = subtotal;
        }

        BigDecimal finalTotal = subtotal.subtract(discountAmount).max(BigDecimal.ZERO).setScale(2,
                RoundingMode.HALF_UP);
        int usedCount = getUsageCount(coupon.getId(), customer.getId());
        int remainingUses = Math.max(coupon.getMaxUsePerUser() - usedCount - 1, 0);

        return new CouponCalculation(
                subtotal.setScale(2, RoundingMode.HALF_UP),
                discountAmount.setScale(2, RoundingMode.HALF_UP),
                finalTotal,
                remainingUses);
    }

    private void validateCouponState(Coupon coupon, User customer) {
        if (!coupon.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is inactive");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartAt() != null && now.isBefore(coupon.getStartAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon is not active yet");
        }
        if (coupon.getEndAt() != null && now.isAfter(coupon.getEndAt())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon has expired");
        }

        int usageCount = getUsageCount(coupon.getId(), customer.getId());
        if (usageCount >= coupon.getMaxUsePerUser()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon usage limit reached for this user");
        }

        if (coupon.getTotalUsageLimit() != null) {
            long totalUsage = couponUsageRepository.sumUsageCountByCouponId(coupon.getId());
            if (totalUsage >= coupon.getTotalUsageLimit()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon usage limit reached");
            }
        }
    }

    private List<BookQuantityLine> filterApplicableLines(Coupon coupon, List<BookQuantityLine> lines) {
        if (coupon.getApplicableBooks() == null || coupon.getApplicableBooks().isEmpty()) {
            return lines;
        }

        Set<Long> applicableBookIds = coupon.getApplicableBooks().stream()
                .map(Book::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        return lines.stream()
                .filter(line -> line.bookId() != null && applicableBookIds.contains(line.bookId()))
                .toList();
    }

    private BigDecimal calculateSubtotal(List<BookQuantityLine> lines) {
        return lines.stream()
                .map(line -> line.price().multiply(BigDecimal.valueOf(line.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
    }

    private List<BookQuantityLine> toBookQuantityLinesFromCart(List<CartItem> cartItems) {
        return cartItems.stream()
                .filter(item -> item.getBook() != null && item.getBook().getPrice() != null)
                .map(item -> new BookQuantityLine(item.getBook().getId(), item.getBook().getPrice(),
                        item.getQuantity()))
                .toList();
    }

    private List<BookQuantityLine> toBookQuantityLinesFromOrder(CustomerOrder order) {
        return order.getItems().stream()
                .filter(item -> item.getBook() != null && item.getBook().getPrice() != null)
                .map(item -> new BookQuantityLine(item.getBook().getId(), item.getBook().getPrice(),
                        item.getQuantity()))
                .toList();
    }

    private int getUsageCount(Long couponId, Long customerId) {
        return couponUsageRepository.findByCouponIdAndCustomerId(couponId, customerId)
                .map(CouponUsage::getUsageCount)
                .orElse(0);
    }

    private void incrementUsage(Coupon coupon, User customer) {
        CouponUsage usage = couponUsageRepository.findByCouponIdAndCustomerId(coupon.getId(), customer.getId())
                .orElseGet(() -> {
                    CouponUsage created = new CouponUsage();
                    created.setCoupon(coupon);
                    created.setCustomer(customer);
                    created.setUsageCount(0);
                    return created;
                });

        usage.setUsageCount(usage.getUsageCount() + 1);
        couponUsageRepository.save(usage);
    }

    private void decrementUsage(Coupon coupon, User customer) {
        couponUsageRepository.findByCouponIdAndCustomerId(coupon.getId(), customer.getId())
                .ifPresent(usage -> {
                    int nextValue = usage.getUsageCount() - 1;
                    if (nextValue <= 0) {
                        couponUsageRepository.delete(usage);
                    } else {
                        usage.setUsageCount(nextValue);
                        couponUsageRepository.save(usage);
                    }
                });
    }

    private Payment ensurePayment(CustomerOrder order, User customer) {
        if (order.getPayment() != null) {
            return order.getPayment();
        }

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setUser(customer);
        payment.setPaymentMethod(Payment.PaymentMethod.CASH);
        payment.setStatus(Payment.PaymentStatus.PENDING);
        payment.setAmount(calculateSubtotal(toBookQuantityLinesFromOrder(order)));
        order.setPayment(payment);
        return paymentRepository.save(payment);
    }

    private void ensureOrderOwnership(CustomerOrder order, User customer) {
        User owner = order.getCustomer();
        if (owner == null && order.getPayment() != null) {
            owner = order.getPayment().getUser();
        }
        if (owner == null || !Objects.equals(owner.getId(), customer.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You cannot apply coupons to this order");
        }
    }

    private CouponDTO toCouponDto(Coupon coupon) {
        return new CouponDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getType(),
                coupon.getTarget(),
                coupon.getValue(),
                coupon.isActive(),
                coupon.getMaxUsePerUser(),
                coupon.getTotalUsageLimit(),
                coupon.getMinOrderValue(),
                coupon.getMaxDiscountValue(),
                coupon.getStartAt(),
                coupon.getEndAt(),
                couponUsageRepository.sumUsageCountByCouponId(coupon.getId()),
                coupon.getApplicableBooks().stream().map(Book::getId).toList(),
                coupon.getApplicableBooks().stream().map(this::toBookDto).toList());
    }

    private CouponUsageDTO toCouponUsageDto(CouponUsage couponUsage) {
        return new CouponUsageDTO(
                couponUsage.getId(),
                couponUsage.getCoupon() != null ? couponUsage.getCoupon().getId() : null,
                couponUsage.getCoupon() != null ? couponUsage.getCoupon().getCode() : null,
                couponUsage.getCustomer() != null ? couponUsage.getCustomer().getId() : null,
                couponUsage.getCustomer() != null ? couponUsage.getCustomer().getEmail() : null,
                couponUsage.getCustomer() != null ? couponUsage.getCustomer().getName() : null,
                couponUsage.getUsageCount());
    }

    private CouponValidationDTO toValidationDto(Coupon coupon, CouponCalculation calculation, String message) {
        return new CouponValidationDTO(
                true,
                coupon.getCode(),
                message,
                calculation.subtotal(),
                calculation.discountAmount(),
                calculation.finalTotal(),
                calculation.remainingUses());
    }

    private BookDTO toBookDto(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .isbn(book.getIsbn())
                .description(book.getDescription())
                .price(book.getPrice())
                .authorId(book.getAuthor() != null ? book.getAuthor().getId() : null)
                .authorName(book.getAuthor() != null ? book.getAuthor().getName() : null)
                .categoryId(book.getCategory() != null ? book.getCategory().getId() : null)
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : null)
                .images(List.of())
                .stock(book.getStock())
                .build();
    }

    private String normalizeCode(String code) {
        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon code is required");
        }
        return code.trim().toUpperCase();
    }

    private String resolveCouponCode(Coupon coupon, CouponDTO couponDTO) {
        if (hasText(couponDTO.getCode())) {
            return normalizeCode(couponDTO.getCode());
        }
        if (coupon.getId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Coupon code is required");
        }
        return generateUniqueCouponCode();
    }

    private String generateUniqueCouponCode() {
        Random random = new Random();
        for (int attempt = 0; attempt < 20; attempt++) {
            StringBuilder builder = new StringBuilder(COUPON_CODE_PREFIX).append('-');
            for (int index = 0; index < COUPON_CODE_LENGTH; index++) {
                builder.append(COUPON_CODE_CHARS.charAt(random.nextInt(COUPON_CODE_CHARS.length())));
            }

            String code = builder.toString();
            if (!couponRepository.existsByCodeIgnoreCase(code)) {
                return code;
            }
        }
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to generate coupon code");
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private BigDecimal scaleOrNull(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }

    private record BookQuantityLine(Long bookId, BigDecimal price, int quantity) {
    }

    private record CouponCalculation(
            BigDecimal subtotal,
            BigDecimal discountAmount,
            BigDecimal finalTotal,
            int remainingUses) {
    }
}
