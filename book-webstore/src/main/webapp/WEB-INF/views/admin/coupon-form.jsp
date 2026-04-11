<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${coupon.id == null ? 'Tạo coupon' : 'Cập nhật coupon'} - Admin</title>
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
                    <a href="${pageContext.request.contextPath}/admin/orders" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-receipt me-2"></i> Đơn hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/coupons" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                        <i class="fas fa-ticket-alt me-2"></i> Coupon
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/coupons/add" class="list-group-item list-group-item-action border-0 px-4 py-3 ${coupon.id == null ? 'active' : ''}">
                        <i class="fas fa-plus me-2"></i> Tạo coupon
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-9">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-dark">
                    <i class="fas fa-ticket-alt me-2 text-primary"></i>${coupon.id == null ? 'Tạo coupon mới' : 'Cập nhật coupon'}
                </h2>
                <a href="${pageContext.request.contextPath}/admin/coupons" class="btn btn-outline-secondary px-4 shadow-sm">
                    <i class="fas fa-arrow-left me-2"></i>Quay lại
                </a>
            </div>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-danger">${errorMessage}</div>
            </c:if>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold text-muted small text-uppercase">Thông tin coupon</h5>
                </div>
                <div class="card-body p-4">
                    <form method="post" action="${pageContext.request.contextPath}/admin/coupons/save" class="row g-3">
                        <input type="hidden" name="id" value="${coupon.id}">

                        <c:choose>
                            <c:when test="${coupon.id == null}">
                                <!-- <div class="col-md-4">
                                    <label class="form-label fw-semibold">Mã coupon</label>
                                    <input type="text" class="form-control" value="Tự động tạo khi lưu" disabled>
                                    <div class="form-text">Hệ thống sẽ tự sinh mã coupon mới, bạn không cần nhập tay.</div>
                                </div> -->
                            </c:when>
                            <c:otherwise>
                                <div class="col-md-4">
                                    <label class="form-label fw-semibold">Mã coupon</label>
                                    <input type="text" class="form-control" name="code" value="${coupon.code}" required>
                                </div>
                            </c:otherwise>
                        </c:choose>

                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Loại giảm giá</label>
                            <select class="form-select" name="type" required>
                                <option value="PERCENTAGE" ${coupon.type == 'PERCENTAGE' ? 'selected' : ''}>Phần trăm</option>
                                <option value="FIXED" ${coupon.type == 'FIXED' ? 'selected' : ''}>Số tiền cố định</option>
                            </select>
                        </div>

                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Giá trị</label>
                            <input type="number" step="0.01" min="0.01" class="form-control" name="value" value="${coupon.value}" required>
                        </div>

                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Tối đa mỗi user</label>
                            <input type="number" min="1" class="form-control" name="maxUsePerUser" value="${coupon.maxUsePerUser > 0 ? coupon.maxUsePerUser : 1}" required>
                        </div>

                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Giới hạn tổng lượt</label>
                            <input type="number" min="1" class="form-control" name="totalUsageLimit" value="${coupon.totalUsageLimit}">
                        </div>

                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Đơn tối thiểu</label>
                            <input type="number" step="0.01" min="0" class="form-control" name="minOrderValue" value="${coupon.minOrderValue}">
                        </div>

                        <div class="col-md-3">
                            <label class="form-label fw-semibold">Giảm tối đa</label>
                            <input type="number" step="0.01" min="0" class="form-control" name="maxDiscountValue" value="${coupon.maxDiscountValue}">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Bắt đầu</label>
                            <input type="datetime-local" class="form-control" name="startAt" value="${coupon.startAt}">
                        </div>

                        <div class="col-md-4">
                            <label class="form-label fw-semibold">Kết thúc</label>
                            <input type="datetime-local" class="form-control" name="endAt" value="${coupon.endAt}">
                        </div>

                        <div class="col-md-4 d-flex align-items-end">
                            <div class="form-check form-switch">
                                <input class="form-check-input" type="checkbox" id="active" name="active" value="true" ${coupon.active ? 'checked' : ''}>
                                <label class="form-check-label" for="active">Kích hoạt coupon</label>
                            </div>
                        </div>

                        <div class="col-12">
                            <label class="form-label fw-semibold">Sách áp dụng</label>
                            <select class="form-select" name="applicableBookIds" multiple size="10">
                                <c:forEach var="book" items="${books}">
                                    <c:set var="isSelected" value="false" />
                                    <c:forEach var="selectedId" items="${coupon.applicableBookIds}">
                                        <c:if test="${selectedId == book.id}">
                                            <c:set var="isSelected" value="true" />
                                        </c:if>
                                    </c:forEach>
                                    <option value="${book.id}" ${isSelected ? 'selected' : ''}>
                                        ${book.title}
                                    </option>
                                </c:forEach>
                            </select>
                            <div class="form-text">Để trống nếu coupon áp dụng cho toàn bộ sách.</div>
                        </div>

                        <div class="col-12 d-flex gap-2 pt-2">
                            <button type="submit" class="btn btn-primary px-4">
                                <i class="fas fa-save me-2"></i>Lưu coupon
                            </button>
                            <a href="${pageContext.request.contextPath}/admin/coupons" class="btn btn-light border">Hủy</a>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
