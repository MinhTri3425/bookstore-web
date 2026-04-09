<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý sách - Admin</title>

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
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">Danh mục</a></li>
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
                    <a href="${pageContext.request.contextPath}/admin/books" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                        <i class="fas fa-book me-2"></i> Quản lý sách
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/books/add" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-plus me-2"></i> Thêm sách mới
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/authors" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-user me-2"></i> Tác giả
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-tags me-2"></i> Danh mục
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-9">

            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2 class="fw-bold text-dark"><i class="fas fa-book me-2 text-primary"></i>Quản lý sách</h2>
                <a href="${pageContext.request.contextPath}/admin/books/add" class="btn btn-primary shadow-sm px-4">
                    <i class="fas fa-plus me-2"></i>Thêm sách
                </a>
            </div>

            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body p-4">
                    <form method="get" action="${pageContext.request.contextPath}/admin/books" class="row g-3">
                        <div class="col-md-4">
                            <input type="text" name="keyword" class="form-control" placeholder="Tìm kiếm tên sách, ISBN..." value="${keyword}">
                        </div>
                        <div class="col-md-3">
                            <select name="categoryId" class="form-select">
                                <option value="">Tất cả danh mục</option>
                                <c:forEach var="c" items="${categories}">
                                    <option value="${c.id}" ${categoryId == c.id ? 'selected' : ''}>${c.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3">
                            <select name="authorId" class="form-select">
                                <option value="">Tất cả tác giả</option>
                                <c:forEach var="a" items="${authors}">
                                    <option value="${a.id}" ${authorId == a.id ? 'selected' : ''}>${a.name}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-2">
                            <button class="btn btn-primary w-100 shadow-sm"><i class="fas fa-search me-1"></i> Lọc</button>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách sách trong kho</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th class="px-4">ID</th>
                            <th>Thông tin Sách</th>
                            <th>Giá bán</th>
                            <th>Tồn kho</th>
                            <th>Trạng thái</th>
                            <th class="text-center">Thao tác</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:forEach var="book" items="${books}">
                            <tr>
                                <td class="px-4 text-muted">${book.id}</td>
                                <td>
                                    <div class="fw-bold text-dark">${book.title}</div>
                                    <div class="small text-muted">
                                        <i class="fas fa-user-edit me-1"></i> ${book.authorName} | 
                                        <span class="ms-1">ISBN: ${book.isbn}</span>
                                    </div>
                                </td>
                                <td>
                                    <span class="text-danger fw-bold">
                                        <fmt:formatNumber value="${book.price}" pattern="#,###"/> ₫
                                    </span>
                                </td>
                                <td>${not empty book.stock ? book.stock : 0}</td>
                                <td>
                                    <c:set var="badgeClass" value="${book.stock == 0 ? 'bg-danger' : (book.stock <= 10 ? 'bg-warning text-dark' : 'bg-success')}" />
                                    <c:set var="statusText" value="${book.stock == 0 ? 'Hết hàng' : (book.stock <= 10 ? 'Sắp hết' : 'Còn hàng')}" />
                                    <span class="badge rounded-pill ${badgeClass}">${statusText}</span>
                                </td>
                                <td class="text-center">
                                    <div class="btn-group btn-group-sm shadow-sm">
                                        <a href="${pageContext.request.contextPath}/admin/books/edit?id=${book.id}" class="btn btn-outline-primary" title="Chỉnh sửa">
                                            <i class="fas fa-edit"></i>
                                        </a>
                                        <button type="button" class="btn btn-outline-danger" 
                                                onclick="confirmDelete('${book.id}', '${book.title}')" title="Xóa">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                        
                        <c:if test="${empty books}">
                            <tr>
                                <td colspan="6" class="text-center py-5 text-muted">
                                    <i class="fas fa-box-open fa-3x mb-3 d-block op-3"></i>
                                    Không tìm thấy cuốn sách nào phù hợp.
                                </td>
                            </tr>
                        </c:if>
                        </tbody>
                    </table>
                </div>
            </div>

        </div>
    </div>
</div>

<div class="modal fade" id="deleteModal" tabindex="-1" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-danger text-white border-0">
                <h5 class="modal-title">Xác nhận xóa sách</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/books/delete">
                <div class="modal-body py-4 text-center">
                    Bạn có chắc chắn muốn xóa cuốn sách "<strong id="deleteBookTitle" class="text-danger"></strong>" không?
                    <input type="hidden" id="deleteBookId" name="id">
                </div>
                <div class="modal-footer border-0">
                    <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-danger px-4">Xóa ngay</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(id, title) {
        document.getElementById('deleteBookId').value = id;
        document.getElementById('deleteBookTitle').innerText = title;
        const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
        modal.show();
    }
</script>

</body>
</html>