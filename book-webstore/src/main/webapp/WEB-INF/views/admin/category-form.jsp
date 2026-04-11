<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty category.id ? 'Thêm Danh Mục Mới' : 'Sửa Danh Mục'} - Admin</title>

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
                    <a href="${pageContext.request.contextPath}/admin/books/add" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-plus me-2"></i> Thêm sách mới
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-receipt me-2"></i> Đơn hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/authors" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-user me-2"></i> Tác giả
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                        <i class="fas fa-tags me-2"></i> Danh mục
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-9">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold">
                    <i class="${empty category.id ? 'fas fa-plus-circle text-primary' : 'fas fa-edit text-warning'} me-2"></i>
                    <span>${empty category.id ? 'Thêm Danh Mục Mới' : 'Chỉnh Sửa Danh Mục'}</span>
                </h2>
                <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-outline-secondary px-4 shadow-sm">
                    <i class="fas fa-arrow-left me-2"></i>Quay lại
                </a>
            </div>

            <div class="row justify-content-center">
                <div class="col-lg-10">
                    <form action="${pageContext.request.contextPath}/admin/categories/save" method="post">
                        <input type="hidden" name="id" value="${category.id}">

                        <div class="card border-0 shadow-sm mb-4">
                            <div class="card-header bg-white py-3">
                                <h5 class="mb-0 fw-bold text-muted">Thông tin danh mục</h5>
                            </div>
                            <div class="card-body p-4">
                                <div class="row g-4">
                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Tên Danh Mục <span class="text-danger">*</span></label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-end-0"><i class="fas fa-tag text-muted"></i></span>
                                            <input type="text" class="form-control border-start-0 shadow-none" 
                                                   name="name" id="categoryName" value="${category.name}" 
                                                   placeholder="Ví dụ: Văn học nước ngoài" required>
                                        </div>
                                    </div>

                                    <div class="col-md-6">
                                        <label class="form-label fw-bold">Slug (Đường dẫn tĩnh)</label>
                                        <div class="input-group">
                                            <span class="input-group-text bg-light border-end-0"><i class="fas fa-link text-muted"></i></span>
                                            <input type="text" class="form-control border-start-0 shadow-none" 
                                                   name="slug" value="${category.slug}" placeholder="van-hoc-nuoc-ngoai">
                                        </div>
                                        <small class="text-muted small">Để trống nếu muốn tự động tạo từ tên.</small>
                                    </div>

                                    <div class="col-12">
                                        <label class="form-label fw-bold">Danh mục cha</label>
                                        <select class="form-select shadow-none" name="parentId">
                                            <option value="">-- Không có danh mục cha (Cấp cao nhất) --</option>
                                            <c:forEach var="p" items="${parentCategories}">
                                                <c:if test="${p.id != category.id}">
                                                    <option value="${p.id}" ${p.id == category.parentId ? 'selected' : ''}>${p.name}</option>
                                                </c:if>
                                            </c:forEach>
                                        </select>
                                        <small class="text-muted">Chọn nếu đây là danh mục con (Sub-category).</small>
                                    </div>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex gap-2">
                            <button type="submit" class="btn btn-primary btn-lg px-5 shadow-sm">
                                <i class="fas fa-save me-2"></i>Lưu danh mục
                            </button>
                            <button type="reset" class="btn btn-light btn-lg px-4 border">Nhập lại</button>
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
