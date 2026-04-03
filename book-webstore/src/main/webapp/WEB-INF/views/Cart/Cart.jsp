<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.example.book_webstore.dto.CartDTO" %>
<%@ page import="com.example.book_webstore.dto.CartItemDTO" %>
<%@ page import="com.example.book_webstore.dto.BookImageDTO" %>
<%@ page import="java.math.BigDecimal" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Gio hang</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/Cart.css">
</head>
<body>
<%
    CartDTO cart = (CartDTO) request.getAttribute("cart");
    Object countObj = request.getAttribute("cartItemCount");
    long cartItemCount = countObj == null ? 0L : Long.parseLong(String.valueOf(countObj));
    BigDecimal cartTotal = (BigDecimal) request.getAttribute("cartTotal");
    String message = (String) request.getAttribute("message");
%>
<div class="page">
    <header class="topbar">
        <div>
            <h1>Gio hang cua ban</h1>
            <p>Danh sach sach da them vao gio hang</p>
        </div>
        <div class="topbar-actions">
            <div class="badge">So luong trong gio: <%= cartItemCount %></div>
            <a class="btn btn-primary" href="<%= request.getContextPath() %>/BookTest/">Tiep tuc mua sach</a>
        </div>
    </header>

    <% if (message != null && !message.isBlank()) { %>
        <div class="notice"><%= message %></div>
    <% } %>

    <section class="catalog">
        <div class="cart-summary">
            <strong>Tong tien gio hang: <%= cartTotal == null ? "0" : cartTotal %> VND</strong>
        </div>

        <% if (cart == null || cart.getItems() == null || cart.getItems().isEmpty()) { %>
            <p>Gio hang trong.</p>
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
            <article class="cart-row">
                <div class="cart-row-left">
                    <% if (imageUrl != null && !imageUrl.isBlank()) { %>
                        <img src="<%= imageUrl %>" alt="Book image" class="cart-thumb">
                    <% } else { %>
                        <div class="cart-thumb placeholder">No Image</div>
                    <% } %>

                    <div>
                        <h3><%= item.getBook() == null ? "Unknown" : item.getBook().getTitle() %></h3>
                        <p>So luong: <strong><%= item.getQuantity() %></strong></p>
                        <p>Gia: <strong><%= item.getBook() == null ? "0" : item.getBook().getPrice() %> VND</strong></p>
                    </div>
                </div>

                <form method="post" action="<%= request.getContextPath() %>/Cart/remove">
                    <input type="hidden" name="bookId" value="<%= item.getBook() == null ? "" : item.getBook().getId() %>">
                    <input type="hidden" name="redirectTo" value="cart">
                    <button type="submit" class="btn btn-danger btn-icon" title="Xoa sach khoi gio">X</button>
                </form>
            </article>
        <%     }
           }
        %>
    </section>
</div>
</body>
</html>
