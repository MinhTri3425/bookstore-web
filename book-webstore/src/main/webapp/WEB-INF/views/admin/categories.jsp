<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Danh mục - Admin</title>
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
                    <h2 class="fw-bold"><i class="fas fa-tags text-primary me-2"></i>Quản lý Danh mục</h2>
                    <a href="${pageContext.request.contextPath}/admin/categories/add" class="btn btn-primary shadow-sm px-4">
                        <i class="fas fa-plus me-2"></i>Thêm Danh mục
                    </a>
                </div>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                        <i class="fas fa-check-circle me-2"></i> ${successMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                        <i class="fas fa-exclamation-triangle me-2"></i> ${errorMessage}
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    </div>
                </c:if>

                <div class="card border-0 shadow-sm">
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th class="px-4" style="width: 100px;">ID</th>
                                    <th>Tên Danh Mục</th>
                                    <th>Slug</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="c" items="${categories}">
                                    <tr>
                                        <td class="px-4 text-muted">${c.id}</td>
                                        <td><strong class="text-dark">${c.name}</strong></td>
                                        <td class="text-muted">${c.slug}</td>
                                        <td class="text-center">
                                            <div class="btn-group btn-group-sm">
                                                <a href="${pageContext.request.contextPath}/admin/categories/edit?id=${c.id}" class="btn btn-outline-primary">
                                                    <i class="fas fa-edit"></i>
                                                </a>
                                                <button type="button" class="btn btn-outline-danger" 
                                                        onclick="confirmDelete('${c.id}', '${c.name}')">
                                                    <i class="fas fa-trash"></i>
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                </c:forEach>
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
                    <h5 class="modal-title"><i class="fas fa-trash-alt me-2"></i>Xác nhận xóa</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form id="categoryDeleteForm" method="post" action="${pageContext.request.contextPath}/admin/categories/delete">
                    <div class="modal-body py-4 text-center">
                        <p class="mb-0">Bạn có chắc muốn xóa danh mục "<strong id="categoryDeleteName"></strong>"?</p>
                        <input type="hidden" id="categoryDeleteId" name="id">
                    </div>
                    <div class="modal-footer border-0">
                        <button type="button" class="btn btn-light" data-bs-dismiss="modal">Hủy</button>
                        <button type="submit" class="btn btn-danger px-4">Xác nhận xóa</button>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmDelete(id, name) {
            // Gán dữ liệu vào Modal
            document.getElementById('categoryDeleteId').value = id;
            document.getElementById('categoryDeleteName').innerText = name;
            
            // Hiện Modal
            const modal = new bootstrap.Modal(document.getElementById('deleteModal'));
            modal.show();
        }
    </script>
</body>
</html>