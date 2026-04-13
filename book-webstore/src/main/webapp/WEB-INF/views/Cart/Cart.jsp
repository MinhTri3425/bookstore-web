<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.book_webstore.dto.CartDTO" %>
<%@ page import="com.example.book_webstore.dto.CartItemDTO" %>
<%@ page import="com.example.book_webstore.dto.BookImageDTO" %>
<%@ page import="com.example.book_webstore.dto.CouponValidationDTO" %>
<%@ page import="java.math.BigDecimal" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Giỏ hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Cart.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%
    CartDTO cart = (CartDTO) request.getAttribute("cart");
    Object countObj = request.getAttribute("cartItemCount");
    long cartItemCount = countObj == null ? 0L : Long.parseLong(String.valueOf(countObj));
    BigDecimal cartTotal = (BigDecimal) request.getAttribute("cartTotal");
    CouponValidationDTO couponResult = (CouponValidationDTO) request.getAttribute("couponResult");
    String appliedCouponCode = (String) request.getAttribute("appliedCouponCode");
    String message = (String) request.getAttribute("message");
%>
<div class="container mt-5">
    <header class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h1 class="fw-bold">Giỏ hàng của bạn</h1>
            <p class="text-muted">Danh sách sách đã thêm vào giỏ hàng</p>
        </div>
        <div class="text-end">
            <div class="badge bg-primary mb-2" style="font-size: 1rem;">Số lượng: <%= cartItemCount %></div>
            <br>
            <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/books">Tiếp tục mua sắm</a>
        </div>
    </header>

    <%-- Hiển thị thông báo nếu có --%>
    <c:if test="${not empty message}">
        <div class="alert alert-info alert-dismissible fade show" role="alert">
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <section class="card shadow-sm p-4">
        <div class="mb-3 border-bottom pb-2 text-end">
            <h4 class="text-danger">Tổng tiền: <%= cartTotal == null ? "0" : cartTotal %> VND</h4>
        </div>

        <div class="coupon-panel mb-4">
            <div class="row g-3 align-items-end">
                <div class="col-md-8">
                    <form method="post" action="${pageContext.request.contextPath}/cart/apply-coupon" class="coupon-form">
                        <label for="couponCode" class="form-label fw-semibold">Mã giảm giá</label>
                        <div class="input-group">
                            <input id="couponCode" type="text" name="code" class="form-control" placeholder="Nhập coupon của bạn" value="<%= appliedCouponCode == null ? "" : appliedCouponCode %>">
                            <button type="submit" class="btn btn-primary">Áp dụng</button>
                        </div>
                    </form>
                </div>
                <div class="col-md-4">
                    <form method="post" action="${pageContext.request.contextPath}/cart/remove-coupon" class="d-grid">
                        <button type="submit" class="btn btn-outline-secondary" <%= appliedCouponCode == null ? "disabled" : "" %>>
                            Gỡ coupon
                        </button>
                    </form>
                </div>
            </div>

            <% if (couponResult != null) { %>
                <div class="coupon-result mt-3">
                    <div class="coupon-badge">Đã áp dụng: <strong><%= couponResult.getCode() %></strong></div>
                    <div class="coupon-meta">
                        <span>Tạm tính: <strong><%= couponResult.getSubtotal() %> VND</strong></span>
                        <span>Giảm giá: <strong>-<%= couponResult.getDiscountAmount() %> VND</strong></span>
                        <span>Thanh toán: <strong><%= couponResult.getFinalTotal() %> VND</strong></span>
                    </div>
                </div>
            <% } %>
        </div>

        <% if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) { %>
            <div class="text-center py-5">
                <p class="fs-5 text-muted">Giỏ hàng đang trống.</p>
                <a href="${pageContext.request.contextPath}/books" class="btn btn-primary">Mua ngay</a>
            </div>
        <% } else {
                for (CartItemDTO item : cart.getItems()) {
                    String imageUrl = null;
                    if (item.getBook() != null && item.getBook().getImages() != null && !item.getBook().getImages().isEmpty()) {
                        BookImageDTO first = item.getBook().getImages().get(0);
                        if (first != null) {
                            imageUrl = first.getUrl();
                        }
                    }
        %>
            <div class="row align-items-center mb-3 border-bottom pb-3">
                <div class="col-md-2 text-center">
                    <% if (imageUrl != null && !imageUrl.isBlank()) { %>
                        <img src="<%= imageUrl %>" alt="Book image" class="img-fluid rounded shadow-sm" style="max-height: 100px;">
                    <% } else { %>
                        <div class="bg-light d-flex align-items-center justify-content-center rounded" style="height: 100px; width: 100%;">
                            <small class="text-muted">No Image</small>
                        </div>
                    <% } %>
                </div>

                <div class="col-md-8">
                    <h5 class="mb-1"><%= item.getBook() == null ? "Sách không xác định" : item.getBook().getTitle() %></h5>
                    <p class="mb-0 text-muted">Số lượng: <strong><%= item.getQuantity() %></strong></p>
                    <p class="mb-0 text-primary">Giá: <strong><%= item.getBook() == null ? "0" : item.getBook().getPrice() %> VND</strong></p>
                </div>

                <div class="col-md-2 text-end">
                    <%-- FORM XÓA: Sửa action thành /cart/remove --%>
                    <form method="post" action="${pageContext.request.contextPath}/cart/remove">
                        <%-- Quan trọng: Thêm CSRF token để tránh lỗi 403 nếu có Spring Security --%>
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        
                        <input type="hidden" name="bookId" value="<%= item.getBook() == null ? "" : item.getBook().getId() %>">
                        <input type="hidden" name="redirectTo" value="cart">
                        <button type="submit" class="btn btn-danger" title="Xóa sách khỏi giỏ">
                            <i class="bi bi-trash"></i> Xóa
                        </button>
                    </form>
                </div>
            </div>
        <%      }
           }
        %>

        <% if (cart != null && cart.getItems() != null && !cart.getItems().isEmpty()) { %>
            <div class="checkout-summary mt-4">
                <h5 class="mb-3">Tóm tắt thanh toán</h5>
                <div class="summary-line">
                    <span>Tạm tính</span>
                    <strong><%= cartTotal == null ? "0" : cartTotal %> VND</strong>
                </div>
                <div class="summary-line">
                    <span>Giảm giá coupon</span>
                    <strong><%= couponResult == null ? "0" : couponResult.getDiscountAmount() %> VND</strong>
                </div>
                <div class="summary-line total">
                    <span>Tổng cần thanh toán</span>
                    <strong><%= couponResult == null ? (cartTotal == null ? "0" : cartTotal) : couponResult.getFinalTotal() %> VND</strong>
                </div>
            </div>
        <% } %>
    </section>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
