<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Hồ sơ người dùng | BookStore</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/profile.css">
</head>
<body>

    <jsp:include page="../fragments/header.jsp" />

    <div class="container profile-container">
        <div class="row justify-content-center">
            <div class="col-xl-8 col-lg-10">
                
                <div class="mb-3">
                    <sec:authorize access="hasRole('ADMIN')">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="text-decoration-none text-danger fw-bold">
                            <i class="fas fa-arrow-left me-2"></i> Quay lại Dashboard Quản trị
                        </a>
                    </sec:authorize>

                    <sec:authorize access="!hasRole('ADMIN')">
                        <a href="${pageContext.request.contextPath}/books" class="text-decoration-none text-muted fw-semibold">
                            <i class="fas fa-arrow-left me-2"></i> Quay lại cửa hàng
                        </a>
                    </sec:authorize>
                </div>

                <c:if test="${param.success != null}">
                    <div class="alert alert-success border-0 shadow-sm rounded-4 mb-4 p-3 animate__animated animate__fadeInDown">
                        <i class="fas fa-check-circle me-2"></i> Cập nhật hồ sơ thành công!
                    </div>
                </c:if>

                <div class="card profile-card">
                    <div class="profile-banner"></div>
                    
                    <div class="avatar-section">
                        <div class="avatar-wrapper">
                            <div class="avatar-main">
                                <i class="fas fa-user-ninja"></i>
                            </div>
                        </div>
                        <div class="user-identity">
                            <h3 class="text-dark">${user.name}</h3>
                            <div class="d-flex justify-content-center gap-2 mt-2">
                                <span class="badge bg-primary badge-custom">${user.role}</span>
                                <c:if test="${user.shipper}">
                                    <span class="badge bg-info text-dark badge-custom">
                                        <i class="fas fa-truck-fast me-1"></i> Đối tác giao hàng
                                    </span>
                                </c:if>
                            </div>
                        </div>
                    </div>

                    <div class="info-grid">
                        <form action="${pageContext.request.contextPath}/profile/update" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>

                            <div class="row">
                                <div class="col-12 form-group-custom">
                                    <label class="label-custom">Email tài khoản</label>
                                    <div class="input-wrapper">
                                        <i class="fas fa-envelope-open input-icon"></i>
                                        <input type="email" class="form-control-modern" value="${user.email}" readonly disabled>
                                    </div>
                                    <p class="text-muted small mt-2 ml-2">Email dùng để định danh tài khoản và không thể thay đổi.</p>
                                </div>

                                <div class="col-md-6 form-group-custom">
                                    <label for="name" class="label-custom">Họ và tên</label>
                                    <div class="input-wrapper">
                                        <i class="fas fa-id-card input-icon"></i>
                                        <input type="text" id="name" name="name" class="form-control-modern" 
                                               value="${user.name}" placeholder="Nhập tên hiển thị" required>
                                    </div>
                                </div>

                                <div class="col-md-6 form-group-custom">
                                    <label for="phoneNumber" class="label-custom">Số điện thoại</label>
                                    <div class="input-wrapper">
                                        <i class="fas fa-mobile-button input-icon"></i>
                                        <input type="text" id="phoneNumber" name="phoneNumber" class="form-control-modern" 
                                               value="${user.phoneNumber}" placeholder="Số liên lạc">
                                    </div>
                                </div>

                                <div class="col-12 mt-4 d-flex gap-3">
                                    <!-- <a href="${pageContext.request.contextPath}/books" class="btn btn-light border py-3 px-4 rounded-4 fw-bold flex-grow-1">
                                        Tiếp tục mua sắm
                                    </a> -->
                                    <button type="submit" class="btn btn-save py-3 px-4 rounded-4 fw-bold flex-grow-1">
                                        <span>Lưu thay đổi</span>
                                        <i class="fas fa-check ms-2"></i>
                                    </button>
                                </div>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../fragments/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>