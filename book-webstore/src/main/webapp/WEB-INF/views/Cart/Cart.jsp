<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.book_webstore.dto.CartDTO" %>
<%@ page import="com.example.book_webstore.dto.CartItemDTO" %>
<%@ page import="com.example.book_webstore.dto.BookImageDTO" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Giỏ hàng</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/cart.css">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<%
    CartDTO cart = (CartDTO) request.getAttribute("cart");
    Object countObj = request.getAttribute("cartItemCount");
    long cartItemCount = countObj == null ? 0L : Long.parseLong(String.valueOf(countObj));
    BigDecimal cartTotal = (BigDecimal) request.getAttribute("cartTotal");
    String message = (String) request.getAttribute("message");
    DecimalFormat priceFormat = new DecimalFormat("#,###");
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
    <c:if test="${not empty successMessage}">
        <div class="alert alert-success alert-dismissible fade show" role="alert">
            ${successMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="alert alert-danger alert-dismissible fade show" role="alert">
            ${errorMessage}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>

    <section class="card shadow-sm p-4">
        <div class="d-flex justify-content-between align-items-center mb-3 border-bottom pb-2">
            <div>
                <h5 class="mb-1">Tổng giỏ hàng: <span class="text-danger"><%= priceFormat.format(cartTotal == null ? BigDecimal.ZERO : cartTotal) %> VND</span></h5>
                <small class="text-muted">Chọn sản phẩm muốn đặt rồi bấm Đặt hàng</small>
            </div>
            <div class="form-check">
                <input class="form-check-input" type="checkbox" id="selectAllItems" checked>
                <label class="form-check-label" for="selectAllItems">Chọn tất cả</label>
            </div>
        </div>

        <% if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) { %>
            <div class="text-center py-5">
                <p class="fs-5 text-muted">Giỏ hàng đang trống.</p>
                <a href="${pageContext.request.contextPath}/books" class="btn btn-primary">Mua ngay</a>
            </div>
        <% } else { %>
                <% for (CartItemDTO item : cart.getItems()) {
                       String imageUrl = null;
                       if (item.getBook() != null && item.getBook().getImages() != null && !item.getBook().getImages().isEmpty()) {
                           BookImageDTO first = item.getBook().getImages().get(0);
                           if (first != null) {
                               imageUrl = first.getUrl();
                           }
                       }
                       BigDecimal unitPrice = item.getBook() == null || item.getBook().getPrice() == null
                               ? BigDecimal.ZERO
                               : item.getBook().getPrice();
                       BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
                %>
                    <div class="row align-items-center mb-3 border-bottom pb-3 cart-item-row" data-line-total="<%= lineTotal %>">
                        <div class="col-md-1 mb-2 mb-md-0 text-center">
                            <input class="form-check-input item-checkbox" type="checkbox"
                                   data-book-id="<%= item.getBook() == null ? "" : item.getBook().getId() %>" checked>
                        </div>

                        <div class="col-md-2 text-center mb-2 mb-md-0">
                            <% if (imageUrl != null && !imageUrl.isBlank()) { %>
                                <img src="<%= imageUrl %>" alt="Book image" class="img-fluid rounded shadow-sm" style="max-height: 100px;">
                            <% } else { %>
                                <div class="bg-light d-flex align-items-center justify-content-center rounded" style="height: 100px; width: 100%;">
                                    <small class="text-muted">No Image</small>
                                </div>
                            <% } %>
                        </div>

                        <div class="col-md-6 mb-2 mb-md-0">
                            <h5 class="mb-1"><%= item.getBook() == null ? "Sách không xác định" : item.getBook().getTitle() %></h5>
                            <p class="mb-0 text-muted">
                                Số lượng: <strong><%= item.getQuantity() %></strong>
                                <span class="ms-2 badge bg-light text-secondary border">Kho: <%= item.getBook() == null || item.getBook().getStock() == null ? 0 : item.getBook().getStock() %></span>
                            </p>
                            <form method="post" action="${pageContext.request.contextPath}/cart/update" class="d-flex align-items-center gap-2 mt-2 update-quantity-form">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <input type="hidden" name="bookId" value="<%= item.getBook() == null ? "" : item.getBook().getId() %>">
                                <input type="number" name="quantity" min="1" max="<%= item.getBook() == null || item.getBook().getStock() == null ? 9999 : item.getBook().getStock() %>" value="<%= item.getQuantity() %>" class="form-control form-control-sm quantity-input" style="max-width: 90px;">
                                <button type="submit" class="btn btn-outline-primary btn-sm">Cập nhật</button>
                            </form>
                            <p class="mb-0 text-primary">Đơn giá: <strong><%= priceFormat.format(unitPrice) %> VND</strong></p>
                            <p class="mb-0 text-danger">Thành tiền: <strong><%= priceFormat.format(lineTotal) %> VND</strong></p>
                        </div>

                        <div class="col-md-3 text-end">
                            <form method="post" action="${pageContext.request.contextPath}/cart/remove">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <input type="hidden" name="bookId" value="<%= item.getBook() == null ? "" : item.getBook().getId() %>">
                                <input type="hidden" name="redirectTo" value="cart">
                                <button type="submit" class="btn btn-danger" title="Xóa sách khỏi giỏ">
                                    Xóa
                                </button>
                            </form>
                        </div>
                    </div>
                <% } %>

                <form method="post" action="${pageContext.request.contextPath}/checkout" id="checkoutForm" class="d-flex justify-content-between align-items-center pt-2 border-top">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    <div id="selectedBookInputs"></div>
                    <h5 class="mb-0">Tổng tiền đã chọn: <span id="selectedTotal" class="text-danger">0 VND</span></h5>
                    <button type="submit" id="checkoutButton" class="btn btn-success px-4">Đặt hàng</button>
                </form>
        <% } %>
    </section>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    (function () {
        const selectAll = document.getElementById('selectAllItems');
        const itemCheckboxes = Array.from(document.querySelectorAll('.item-checkbox'));
        const selectedTotalEl = document.getElementById('selectedTotal');
        const checkoutButton = document.getElementById('checkoutButton');
        const checkoutForm = document.getElementById('checkoutForm');
        const selectedBookInputs = document.getElementById('selectedBookInputs');

        if (!selectedTotalEl || itemCheckboxes.length === 0) {
            return;
        }

        function updateSelectedTotal() {
            let total = 0;
            itemCheckboxes.forEach((checkbox) => {
                if (checkbox.checked) {
                    const row = checkbox.closest('.cart-item-row');
                    const lineTotal = row ? parseFloat(row.dataset.lineTotal || '0') : 0;
                    total += Number.isNaN(lineTotal) ? 0 : lineTotal;
                }
            });

            selectedTotalEl.textContent = total.toLocaleString('vi-VN') + ' VND';

            const checkedCount = itemCheckboxes.filter((cb) => cb.checked).length;
            if (checkoutButton) {
                checkoutButton.disabled = checkedCount === 0;
            }
            if (selectAll) {
                selectAll.checked = checkedCount === itemCheckboxes.length;
                selectAll.indeterminate = checkedCount > 0 && checkedCount < itemCheckboxes.length;
            }

            if (selectedBookInputs) {
                selectedBookInputs.innerHTML = '';
                itemCheckboxes
                    .filter((cb) => cb.checked)
                    .forEach((cb) => {
                        const bookId = cb.dataset.bookId;
                        if (!bookId) {
                            return;
                        }
                        const input = document.createElement('input');
                        input.type = 'hidden';
                        input.name = 'selectedBookIds';
                        input.value = bookId;
                        selectedBookInputs.appendChild(input);
                    });
            }
        }

        if (selectAll) {
            selectAll.addEventListener('change', function () {
                itemCheckboxes.forEach((checkbox) => {
                    checkbox.checked = selectAll.checked;
                });
                updateSelectedTotal();
            });
        }

        itemCheckboxes.forEach((checkbox) => {
            checkbox.addEventListener('change', updateSelectedTotal);
        });

        if (checkoutForm) {
            checkoutForm.addEventListener('submit', function (event) {
                const hasSelected = itemCheckboxes.some((cb) => cb.checked);
                if (!hasSelected) {
                    event.preventDefault();
                    alert('Vui lòng chọn ít nhất một sản phẩm trước khi đặt hàng.');
                }
            });
        }

        const updateForms = document.querySelectorAll('.update-quantity-form');
        updateForms.forEach((form) => {
            form.addEventListener('submit', function (event) {
                const input = form.querySelector('.quantity-input');
                const quantity = parseInt(input.value || '0', 10);
                const max = parseInt(input.getAttribute('max') || '9999', 10);
                if (quantity > max) {
                    event.preventDefault();
                    alert('Số lượng cập nhật vượt quá số lượng còn lại trong kho (' + max + ' sản phẩm có sẵn).');
                }
            });
        });

        updateSelectedTotal();
    })();
</script>
</body>
</html>