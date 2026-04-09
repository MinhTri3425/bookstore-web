<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trang Quản trị | Book Webstore</title>
</head>
<body>
    <h1 style="color: red;">ĐÂY LÀ TRANG QUẢN TRỊ ADMIN</h1>
    <p>Chỉ những ai có quyền ADMIN mới vào được đây.</p>
    
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button type="submit">Đăng xuất</button>
    </form>
</body>
</html>