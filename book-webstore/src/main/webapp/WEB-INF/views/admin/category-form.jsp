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
<body class="bg-light">

<jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

<div class="container py-5">
    <div class="row justify-content-center mb-4">
        <div class="col-lg-10 d-flex justify-content-between align-items-center">
            <div>
                <h2 class="fw-bold mb-1">
                    <i class="${empty category.id ? 'fas fa-plus-circle text-primary' : 'fas fa-edit text-warning'} me-2"></i>
                    ${empty category.id ? 'THÊM DANH MỤC' : 'CHỈNH SỬA DANH MỤC'}
                </h2>
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb mb-0">
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/dashboard">Admin</a></li>
                        <li class="breadcrumb-item"><a href="${pageContext.request.contextPath}/admin/categories">Danh mục</a></li>
                        <li class="breadcrumb-item active">${empty category.id ? 'Thêm mới' : 'Cập nhật'}</li>
                    </ol>
                </nav>
            </div>
            <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-outline-secondary px-4 shadow-sm">
                <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
            </a>
        </div>
    </div>

    <div class="row justify-content-center">
        <div class="col-lg-10">
            <form action="${pageContext.request.contextPath}/admin/categories/save" method="post">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                <input type="hidden" name="id" value="${category.id}">

                <div class="card border-0 shadow-sm mb-4">
                    <div class="card-header bg-white py-3 border-bottom">
                        <h5 class="mb-0 fw-bold text-muted small text-uppercase">Thông tin chi tiết</h5>
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
                                <div class="form-text small">Để trống nếu muốn hệ thống tự động tạo từ tên.</div>
                            </div>

                            <div class="col-12">
                                <label class="form-label fw-bold">Danh mục cấp cha</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light border-end-0"><i class="fas fa-sitemap text-muted"></i></span>
                                    <select class="form-select border-start-0 shadow-none" name="parentId">
                                        <option value="">-- Không có danh mục cha (Cấp cao nhất) --</option>
                                        <c:forEach var="p" items="${parentCategories}">
                                            <c:if test="${p.id != category.id}">
                                                <option value="${p.id}" ${p.id == category.parentId ? 'selected' : ''}>${p.name}</option>
                                            </c:if>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="form-text small text-info"><i class="fas fa-info-circle me-1"></i> Chọn danh mục cha nếu đây là danh mục con (Sub-category).</div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary btn-lg px-5 shadow-sm">
                        <i class="fas fa-save me-2"></i>Lưu danh mục
                    </button>
                    <button type="reset" class="btn btn-light btn-lg px-4 border">Làm lại</button>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-link btn-lg text-muted text-decoration-none">Hủy bỏ</a>
                </div>
            </form>
        </div>
    </div>
</div>
<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>
