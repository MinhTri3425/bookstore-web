<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.book_webstore.dto.*" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Giỏ hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>

<body>

<%
    CartDTO cart = (CartDTO) request.getAttribute("cart");
    BigDecimal cartTotal = (BigDecimal) request.getAttribute("cartTotal");
    CouponValidationDTO couponResult = (CouponValidationDTO) request.getAttribute("couponResult");
    String appliedCouponCode = (String) request.getAttribute("appliedCouponCode");
    DecimalFormat priceFormat = new DecimalFormat("#,###");
%>

<div class="container mt-5">

    <h2>Giỏ hàng</h2>

    <!-- COUPON -->
    <form method="post" action="${pageContext.request.contextPath}/cart/apply-coupon" class="mb-3">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <input type="text" name="code" placeholder="Nhập mã giảm giá"
               value="<%= appliedCouponCode == null ? "" : appliedCouponCode %>">
        <button class="btn btn-primary">Áp dụng</button>
    </form>

    <form method="post" action="${pageContext.request.contextPath}/cart/remove-coupon" class="mb-3">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <button class="btn btn-secondary" <%= appliedCouponCode == null ? "disabled" : "" %>>
            Gỡ coupon
        </button>
    </form>

    <% if (couponResult != null) { %>
        <div class="alert alert-success">
            Giảm: <%= couponResult.getDiscountAmount() %> VND |
            Tổng: <%= couponResult.getFinalTotal() %> VND
        </div>
    <% } %>

    <!-- CART ITEMS -->
    <% if (cart != null && cart.getItems() != null) {
        for (CartItemDTO item : cart.getItems()) {

            BigDecimal price = item.getBook().getPrice();
            BigDecimal lineTotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
    %>

    <div class="row border p-3 mb-2 cart-item-row" data-line-total="<%= lineTotal %>">

        <!-- CHECKBOX -->
        <div class="col-md-1">
            <input type="checkbox" class="item-checkbox"
                   data-book-id="<%= item.getBook().getId() %>" checked>
        </div>

        <!-- INFO -->
        <div class="col-md-6">
            <h5><%= item.getBook().getTitle() %></h5>
            <p>Giá: <%= priceFormat.format(price) %> VND</p>

            <!-- UPDATE -->
            <form method="post" action="${pageContext.request.contextPath}/cart/update" class="d-flex">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="bookId" value="<%= item.getBook().getId() %>">
                <input type="number" name="quantity" value="<%= item.getQuantity() %>" min="1">
                <button class="btn btn-sm btn-primary">Update</button>
            </form>

            <p class="text-danger">
                Thành tiền: <%= priceFormat.format(lineTotal) %> VND
            </p>
        </div>

        <!-- DELETE -->
        <div class="col-md-3">
            <form method="post" action="${pageContext.request.contextPath}/cart/remove">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="bookId" value="<%= item.getBook().getId() %>">
                <button class="btn btn-danger">Xóa</button>
            </form>
        </div>

    </div>

    <% }} %>

    <!-- CHECKOUT -->
    <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm">
        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
        <div id="selectedBookInputs"></div>

        <h4>
            Tổng chọn: <span id="selectedTotal">0 VND</span>
        </h4>

        <button class="btn btn-success" id="checkoutButton">
            Đặt hàng
        </button>
    </form>

</div>

<script>
    const checkboxes = document.querySelectorAll(".item-checkbox");
    const totalEl = document.getElementById("selectedTotal");
    const inputsDiv = document.getElementById("selectedBookInputs");

    function updateTotal() {
        let total = 0;
        inputsDiv.innerHTML = "";

        checkboxes.forEach(cb => {
            if (cb.checked) {
                const row = cb.closest(".cart-item-row");
                total += parseFloat(row.dataset.lineTotal);

                const input = document.createElement("input");
                input.type = "hidden";
                input.name = "selectedBookIds";
                input.value = cb.dataset.bookId;
                inputsDiv.appendChild(input);
            }
        });

        totalEl.innerText = total.toLocaleString("vi-VN") + " VND";
    }

    checkboxes.forEach(cb => cb.addEventListener("change", updateTotal));
    updateTotal();
</script>

</body>
</html>