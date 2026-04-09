<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Thông tin Tác giả - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
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
                        <a href="${pageContext.request.contextPath}/admin/authors" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                            <i class="fas fa-user me-2"></i> Tác giả
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-9">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="fw-bold">
                        <c:choose>
                            <c:when test="${empty author.id}">
                                Thêm Tác Giả
                            </c:when>
                            <c:otherwise>
                                Sửa Tác Giả
                            </c:otherwise>
                        </c:choose>
                    </h2>
                    <a href="${pageContext.request.contextPath}/admin/authors" class="btn btn-outline-secondary px-4">
                        <i class="fas fa-arrow-left me-2"></i>Quay lại
                    </a>
                </div>

                <div class="card border-0 shadow-sm">
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/admin/authors/save" method="post">
                            <input type="hidden" name="id" value="${author.id}">
                            
                            <div class="mb-4">
                                <label class="form-label fw-bold">Tên Tác Giả <span class="text-danger">*</span></label>
                                <input type="text" name="name" class="form-control form-control-lg" 
                                       value="${author.name}" placeholder="Nhập tên tác giả..." required>
                            </div>
                            
                            <button type="submit" class="btn btn-primary btn-lg px-5 shadow-sm">
                                <i class="fas fa-save me-2"></i>Lưu lại
                            </button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>