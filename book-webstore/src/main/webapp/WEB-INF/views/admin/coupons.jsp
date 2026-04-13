<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý coupon - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-cog me-2"></i> Admin Panel
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">Danh mục</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/coupons">Coupon</a></li>
            </ul>
            <ul class="navbar-nav">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/"><i class="fas fa-home me-1"></i> Về trang chủ</a>
                </li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid py-4">
    <div class="row">
        <div class="col-md-3">
            <div class="card border-0 shadow-sm overflow-hidden">
                <div class="card-header bg-white py-3 border-0">
                    <h5 class="mb-0 fw-bold">Menu Quản lý</h5>
                </div>
                <div class="list-group list-group-flush sidebar-nav">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/books" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-book me-2"></i> Quản lý sách
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/authors" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-user me-2"></i> Tác giả
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-tags me-2"></i> Danh mục
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/coupons" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                        <i class="fas fa-ticket-alt me-2"></i> Coupon
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-receipt me-2"></i> Đơn hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/coupons/add" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-plus me-2"></i> Tạo coupon
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-9">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-dark"><i class="fas fa-ticket-alt me-2 text-primary"></i>Quản lý coupon</h2>
                <a href="${pageContext.request.contextPath}/admin/coupons/add" class="btn btn-primary shadow-sm px-4">
                    <i class="fas fa-plus me-2"></i>Tạo coupon
                </a>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>
            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách coupon hiện có</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th class="px-4">Mã</th>
                            <th>Loại</th>
                            <th>Giá trị</th>
                            <th>Hiệu lực</th>
                            <th>Sử dụng</th>
                            <th>Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="coupon" items="${coupons}">
                            <tr>
                                <td class="px-4">
                                    <div class="fw-bold">${coupon.code}</div>
                                    <div class="small text-muted">
                                        ${empty coupon.applicableBooks ? 'Áp dụng toàn bộ sách' : 'Áp dụng theo sách chọn'}
                                    </div>
                                </td>
                                <td>${coupon.type}</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${coupon.type == 'PERCENTAGE'}">${coupon.value}%</c:when>
                                        <c:otherwise><fmt:formatNumber value="${coupon.value}" pattern="#,###"/> ₫</c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <div class="small">Từ: ${coupon.startAt}</div>
                                    <div class="small">Đến: ${coupon.endAt}</div>
                                </td>
                                <td>
                                    <div class="small">Đã dùng: ${coupon.currentUsageCount}</div>
                                    <div class="small">Mỗi user: ${coupon.maxUsePerUser}</div>
                                </td>
                                <td>
                                    <span class="badge ${coupon.active ? 'bg-success' : 'bg-secondary'}">
                                        ${coupon.active ? 'Đang bật' : 'Đã tắt'}
                                    </span>
                                </td>
                                <td class="text-center">
                                    <div class="btn-group btn-group-sm">
                                        <a href="${pageContext.request.contextPath}/admin/coupons/edit?id=${coupon.id}" class="btn btn-outline-primary">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <form method="post" action="${pageContext.request.contextPath}/admin/coupons/delete" class="d-inline">
                                            <input type="hidden" name="id" value="${coupon.id}">
                                            <button type="submit" class="btn btn-outline-danger" onclick="return confirm('Xóa coupon ${coupon.code}?')">
                                                <i class="fas fa-trash"></i>
                                            </button>
                                        </form>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty coupons}">
                            <tr>
                                <td colspan="7" class="text-center py-5 text-muted">Chưa có coupon nào.</td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
