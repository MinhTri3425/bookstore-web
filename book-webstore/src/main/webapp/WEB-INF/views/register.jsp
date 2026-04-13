<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng Ký - Book Webstore</title>
    <link rel="stylesheet" type="text/css" href="/css/register.css">
</head>
<body>
    <div class="register-overlay"></div>

    <div class="register-container">
        <div class="register-header">
            <img src="/images/book-stack.svg" alt="BookStore Logo">
            <h2>Tạo Tài Khoản</h2>
            <p>Tham gia cùng hàng ngàn mọt sách khác!</p>
        </div>

        <c:if test="${not empty error}">
            <div class="alert-error">
                ${error}
            </div>
        </c:if>

        <form action="/register" method="POST" modelAttribute="userDTO">
            <div class="form-group">
                <label for="name">Họ và Tên</label>
                <input type="text" id="name" name="name" placeholder="Ví dụ: Nguyễn Văn A" required />
            </div>
            
            <div class="form-group">
                <label for="email">Địa chỉ Email</label>
                <input type="email" id="email" name="email" placeholder="email@example.com" required />
            </div>
            
            <div class="form-group">
                <label for="phoneNumber">Số điện thoại</label>
                <input type="text" id="phoneNumber" name="phoneNumber" placeholder="09xx xxx xxx" required />
            </div>
            
            <div class="form-group">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" placeholder="Tạo mật khẩu an toàn" required />
            </div>
            
            <button type="submit" class="btn-submit">Đăng Ký Ngay</button>
        </form>

        <div class="register-footer">
            Đã có tài khoản? <a href="/login">Đăng nhập tại đây</a>
        </div>
    </div>
</body>
</html>