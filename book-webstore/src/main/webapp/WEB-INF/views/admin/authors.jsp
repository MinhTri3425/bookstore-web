<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quản lý Tác giả - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

    <div class="container py-5">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold mb-1"><i class="fas fa-user-nib text-primary me-2"></i>QUẢN LÝ TÁC GIẢ</h2>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a></li>
                        <li class="breadcrumb-item active">Tác giả</li>
                    </ol>
                </nav>
            </div>
            <a href="${pageContext.request.contextPath}/admin/authors/add" class="btn btn-primary shadow-sm px-4">
                <i class="fas fa-plus me-2"></i>Thêm Tác giả mới
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

        <div class="card border-0 shadow-sm overflow-hidden">
            <div class="table-responsive">
                <table class="table table-hover align-middle mb-0">
                    <thead class="bg-primary text-white">
                        <tr>
                            <th class="px-4 py-3" style="width: 100px;">ID</th>
                            <th class="py-3">Họ và Tên Tác Giả</th>
                            <th class="text-center py-3" style="width: 180px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="a" items="${authors}">
                            <tr>
                                <td class="px-4 text-muted fw-bold">#${a.id}</td>
                                <td>
                                    <div class="d-flex align-items-center">
                                        <div class="avatar-sm bg-light text-primary rounded-circle me-3 d-flex align-items-center justify-content-center" style="width: 40px; height: 40px;">
                                            <i class="fas fa-user"></i>
                                        </div>
                                        <strong class="text-dark">${a.name}</strong>
                                    </div>
                                </td>
                                <td class="text-center">
                                    <div class="btn-group shadow-sm">
                                        <a href="${pageContext.request.contextPath}/admin/authors/edit?id=${a.id}" 
                                           class="btn btn-white btn-sm border" title="Sửa">
                                            <i class="fas fa-edit text-primary"></i>
                                        </a>
                                        <button type="button" class="btn btn-white btn-sm border" 
                                                onclick="confirmDeleteAuthor('${a.id}', '${a.name}')" title="Xóa">
                                            <i class="fas fa-trash text-danger"></i>
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
            <c:if test="${empty authors}">
                <div class="text-center py-5">
                    <i class="fas fa-users-slash fa-3x text-muted mb-3"></i>
                    <p class="text-muted">Chưa có tác giả nào trong hệ thống.</p>
                </div>
            </c:if>
        </div>
    </div>

    <div class="modal fade" id="deleteModal" tabindex="-1">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header bg-danger text-white border-0">
                    <h5 class="modal-title fw-bold"><i class="fas fa-trash-alt me-2"></i>Xác nhận xóa</h5>
                    <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal"></button>
                </div>
                <form id="deleteForm" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                    
                    <div class="modal-body py-4 text-center">
                        <p class="mb-2">Bạn có chắc chắn muốn xóa tác giả:</p>
                        <h4 class="fw-bold text-danger" id="deleteName"></h4>
                        <p class="text-muted small mt-2">Hành động này không thể hoàn tác!</p>
                        <input type="hidden" id="deleteId" name="id">
                    </div>
                    <div class="modal-footer border-0 justify-content-center">
                        <button type="button" class="btn btn-light px-4" data-bs-dismiss="modal">Hủy bỏ</button>
                        <button type="submit" class="btn btn-danger px-4 shadow-sm">Đồng ý xóa</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function confirmDeleteAuthor(id, name) {
            document.getElementById('deleteId').value = id;
            document.getElementById('deleteName').innerText = name;
            document.getElementById('deleteForm').action = '${pageContext.request.contextPath}/admin/authors/delete';
            new bootstrap.Modal(document.getElementById('deleteModal')).show();
        }
    </script>
</body>
</html>