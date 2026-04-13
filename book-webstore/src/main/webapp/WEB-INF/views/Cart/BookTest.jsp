<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.book_webstore.dto.BookDTO" %>
<%@ page import="com.example.book_webstore.dto.BookImageDTO" %>
<%@ page import="com.example.book_webstore.dto.CartDTO" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>BookTest</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/BookTest.css">
</head>
<body>
<%
    List<BookDTO> books = (List<BookDTO>) request.getAttribute("books");
    CartDTO cart = (CartDTO) request.getAttribute("cart");
    Object countObj = request.getAttribute("cartItemCount");
    long cartItemCount = countObj == null ? 0L : Long.parseLong(String.valueOf(countObj));
    String message = (String) request.getAttribute("message");
%>
<div class="page">
    <header class="topbar">
        <div>
            <h1>BookTest</h1>
            <p>Danh sach sach de test gio hang</p>
        </div>
        <div class="topbar-actions">
            <div class="badge">So luong trong gio: <%= cartItemCount %></div>
            <a class="btn btn-primary" href="<%= request.getContextPath() %>/cart">Xem gio hang</a>
        </div>
    </header>

    <% if (message != null && !message.isBlank()) { %>
        <div class="notice"><%= message %></div>
    <% } %>

    <section class="catalog">
        <h2>Danh sach sach</h2>
        <div class="grid">
            <% if (books != null) {
                   for (BookDTO book : books) {
                       List<BookImageDTO> images = book.getImages();
                       BookImageDTO firstImage = (images != null && !images.isEmpty()) ? images.get(0) : null;
            %>
                <article class="card">
                    <div class="thumb-wrap">
                        <% if (firstImage != null && firstImage.getUrl() != null && !firstImage.getUrl().isBlank()) { %>
                            <img src="<%= firstImage.getUrl() %>" alt="<%= firstImage.getAltText() == null ? "Book image" : firstImage.getAltText() %>" class="thumb">
                        <% } else { %>
                            <div class="thumb placeholder">No Image</div>
                        <% } %>
                    </div>

                    <h3><%= book.getTitle() %></h3>
                    <p class="desc"><%= book.getDescription() == null ? "" : book.getDescription() %></p>
                    <p class="price"><%= book.getPrice() %> VND</p>

                    <form method="post" action="<%= request.getContextPath() %>/cart/add" class="add-form">
                        <input type="hidden" name="bookId" value="<%= book.getId() %>">
                        <input type="number" min="1" name="quantity" value="1" class="qty-input">
                        <button type="submit" class="btn btn-primary">Them vao gio</button>
                    </form>
                </article>
            <%     }
               }
            %>
        </div>
    </section>
</div>
</body>
</html>
