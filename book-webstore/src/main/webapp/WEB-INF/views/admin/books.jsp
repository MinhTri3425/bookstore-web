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

<body class="bg-light">

<jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

<div class="container py-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2 class="fw-bold text-dark mb-1"><i class="fas fa-book-open me-2 text-primary"></i>Kho Sách</h2>
            <nav aria-label="breadcrumb">
                <ol class="breadcrumb mb-0">
                    <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a></li>
                    <li class="breadcrumb-item active">Quản lý sách</li>
                </ol>
            </nav>
        </div>
        <a href="${pageContext.request.contextPath}/admin/books/add" class="btn btn-primary shadow-sm px-4 py-2 fw-bold">
            <i class="fas fa-plus-circle me-2"></i>Thêm sách mới
        </a>
    </div>

    <div class="card border-0 shadow-sm mb-4">
        <div class="card-body p-4">
            <form method="get" action="${pageContext.request.contextPath}/admin/books" class="row g-3">
                <div class="col-md-4">
                    <div class="input-group">
                        <span class="input-group-text bg-white border-end-0 text-muted"><i class="fas fa-search"></i></span>
                        <input type="text" name="keyword" class="form-control border-start-0" placeholder="Tên sách, ISBN..." value="${keyword}">
                    </div>
                </div>
                <div class="col-md-3">
                    <select name="categoryId" class="form-select">
                        <option value="">-- Tất cả danh mục --</option>
                        <c:forEach var="c" items="${categories}">
                            <option value="${c.id}" ${categoryId == c.id ? 'selected' : ''}>${c.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-3">
                    <select name="authorId" class="form-select">
                        <option value="">-- Tất cả tác giả --</option>
                        <c:forEach var="a" items="${authors}">
                            <option value="${a.id}" ${authorId == a.id ? 'selected' : ''}>${a.name}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <button class="btn btn-primary w-100 fw-bold"><i class="fas fa-filter me-1"></i> Lọc</button>
                </div>
            </form>
        </div>
    </div>

    <div class="card border-0 shadow-sm overflow-hidden">
        <div class="card-header bg-white py-3 border-bottom d-flex justify-content-between align-items-center">
            <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách hàng tồn kho</h5>
            <span class="badge bg-light text-dark border">${books.size()} cuốn sách</span>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                <tr>
                    <th class="px-4" style="width: 80px;">ID</th>
                    <th>Thông tin Sách</th>
                    <th class="text-center">Giá bán</th>
                    <th class="text-center">Tồn kho</th>
                    <th class="text-center">Trạng thái</th>
                    <th class="text-center">Thao tác</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="book" items="${books}">
                    <tr>
                        <td class="px-4 text-muted fw-bold">#${book.id}</td>
                        <td>
                            <div class="d-flex align-items-center">
                                <div>
                                    <div class="fw-bold text-dark fs-6">${book.title}</div>
                                    <div class="small text-muted">
                                        <span class="badge bg-light text-dark border-0 p-0 me-2"><i class="fas fa-user-edit"></i> ${book.authorName}</span>
                                        <span class="text-secondary">ISBN: ${book.isbn}</span>
                                    </div>
                                </div>
                            </div>
                        </td>
                        <td class="text-center">
                            <span class="text-danger fw-bold">
                                <fmt:formatNumber value="${book.price}" pattern="#,###"/> ₫
                            </span>
                        </td>
                        <td class="text-center fw-bold">${not empty book.stock ? book.stock : 0}</td>
                        <td class="text-center">
                            <c:set var="badgeClass" value="${book.stock == 0 ? 'bg-danger' : (book.stock <= 10 ? 'bg-warning text-dark' : 'bg-success')}" />
                            <c:set var="statusText" value="${book.stock == 0 ? 'Hết hàng' : (book.stock <= 10 ? 'Sắp hết' : 'Còn hàng')}" />
                            <span class="badge rounded-pill ${badgeClass} px-3">${statusText}</span>
                        </td>
                        <td class="text-center">
                            <div class="btn-group shadow-sm">
                                <a href="${pageContext.request.contextPath}/admin/books/edit?id=${book.id}" class="btn btn-white btn-sm border" title="Chỉnh sửa">
                                    <i class="fas fa-edit text-primary"></i>
                                </a>
                                <button type="button" class="btn btn-white btn-sm border" 
                                        onclick="confirmDelete('${book.id}', '${book.title}')" title="Xóa">
                                    <i class="fas fa-trash text-danger"></i>
                                </button>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                
                <c:if test="${empty books}">
                    <tr>
                        <td colspan="6" class="text-center py-5">
                            <i class="fas fa-box-open fa-3x mb-3 text-muted opacity-50"></i>
                            <p class="text-muted">Không tìm thấy cuốn sách nào trong kho.</p>
                            <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-sm btn-link text-primary">Xóa bộ lọc</a>
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<div class="modal fade" id="deleteModal" tabindex="-1">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content border-0 shadow">
            <div class="modal-header bg-danger text-white border-0">
                <h5 class="modal-title fw-bold"><i class="fas fa-exclamation-triangle me-2"></i>Xác nhận xóa</h5>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
            </div>
            <form id="deleteForm" method="post" action="${pageContext.request.contextPath}/admin/books/delete">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <div class="modal-body py-4 text-center">
                    Bạn có chắc chắn muốn xóa cuốn sách:
                    <h4 id="deleteBookTitle" class="text-danger fw-bold mt-2"></h4>
                    <p class="text-muted small mb-0">Hành động này sẽ gỡ sách khỏi danh sách hiển thị và không thể hoàn tác.</p>
                    <input type="hidden" id="deleteBookId" name="id">
                </div>
                <div class="modal-footer border-0 justify-content-center">
                    <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Hủy bỏ</button>
                    <button type="submit" class="btn btn-danger px-4 shadow-sm fw-bold">Xóa vĩnh viễn</button>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function confirmDelete(id, title) {
        document.getElementById('deleteBookId').value = id;
        document.getElementById('deleteBookTitle').innerText = title;
        new bootstrap.Modal(document.getElementById('deleteModal')).show();
    }
</script>

</body>
</html>
