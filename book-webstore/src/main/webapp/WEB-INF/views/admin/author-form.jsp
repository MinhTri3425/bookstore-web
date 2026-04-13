<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty author.id ? 'Thêm' : 'Sửa'} Tác giả - Admin</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

    <div class="container py-5">
        <div class="row justify-content-center">
            <div class="col-lg-8">
                
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb mb-0">
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a></li>
                            <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></li>
                            <li class="breadcrumb-item active">${empty author.id ? 'Thêm mới' : 'Chỉnh sửa'}</li>
                        </ol>
                    </nav>
                    <a href="${pageContext.request.contextPath}/admin/authors" class="btn btn-outline-secondary btn-sm">
                        <i class="fas fa-arrow-left me-2"></i>Danh sách tác giả
                    </a>
                </div>

                <div class="card border-0 shadow-sm">
                    <div class="card-header bg-white py-3">
                        <h5 class="card-title mb-0 fw-bold text-primary">
                            <i class="fas ${empty author.id ? 'fa-user-plus' : 'fa-user-edit'} me-2"></i>
                            ${empty author.id ? 'THÊM TÁC GIẢ MỚI' : 'CẬP NHẬT THÔNG TIN'}
                        </h5>
                    </div>
                    <div class="card-body p-4">
                        <form action="${pageContext.request.contextPath}/admin/authors/save" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <input type="hidden" name="id" value="${author.id}">
                            
                            <div class="mb-4">
                                <label class="form-label fw-bold">Tên Tác Giả <span class="text-danger">*</span></label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"><i class="fas fa-user"></i></span>
                                    <input type="text" name="name" class="form-control form-control-lg" 
                                           value="${author.name}" placeholder="VD: Nguyễn Nhật Ánh" required>
                                </div>
                                <div class="form-text">Tên tác giả sẽ được hiển thị trên thông tin chi tiết của sách.</div>
                            </div>
                            
                            <hr class="my-4">

                            <div class="d-grid gap-2 d-md-flex justify-content-md-end">
                                <button type="reset" class="btn btn-light px-4">Làm lại</button>
                                <button type="submit" class="btn btn-primary btn-lg px-5 shadow-sm">
                                    <i class="fas fa-save me-2"></i>Lưu thông tin
                                </button>
                            </div>
                        </form>
                    </div>
                </div>

            </div>
        </div>
    </div>
    
    <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>