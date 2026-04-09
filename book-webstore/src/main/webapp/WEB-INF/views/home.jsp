<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Trang chủ mua sách</title>
</head>
<body>
    <h1>Chào mừng bạn đến với Book Webstore!</h1>
    <p>Đây là trang dành cho Khách hàng (CUSTOMER).</p>
    
    <form action="${pageContext.request.contextPath}/logout" method="post">
        <button type="submit">Đăng xuất</button>
    </form>
</body>
</html>