<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Admin Dashboard - Book Webstore</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body class="bg-light">

    <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

    <div class="container py-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <div>
                <h2 class="fw-bold mb-1"><i class="fas fa-chart-line text-primary me-2"></i>TỔNG QUAN HỆ THỐNG</h2>
                <p class="text-muted small mb-0">Chào mừng bạn trở lại, Admin. Đây là tình hình kinh doanh hôm nay.</p>
            </div>
            <button class="btn btn-white shadow-sm border" onclick="location.reload()">
                <i class="fas fa-sync-alt"></i> Làm mới dữ liệu
            </button>
        </div>

        <div class="row g-4 mb-4">
            <div class="col-md-4">
                <div class="card border-0 shadow-sm bg-primary text-white">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase small opacity-75">Tổng số đầu sách</h6>
                            <h2 class="fw-bold mb-0">${not empty totalBooks ? totalBooks : 0}</h2>
                        </div>
                        <i class="fas fa-book fa-3x opacity-25"></i>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/books" class="card-footer bg-dark bg-opacity-10 text-white text-decoration-none small text-center">
                        Xem chi tiết kho <i class="fas fa-chevron-right ms-1"></i>
                    </a>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border-0 shadow-sm bg-success text-white">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase small opacity-75">Sách còn hàng</h6>
                            <h2 class="fw-bold mb-0">${not empty inStockBooks ? inStockBooks : 0}</h2>
                        </div>
                        <i class="fas fa-check-double fa-3x opacity-25"></i>
                    </div>
                    <div class="card-footer bg-dark bg-opacity-10 small text-center">Tình trạng kho ổn định</div>
                </div>
            </div>
            <div class="col-md-4">
                <div class="card border-0 shadow-sm bg-danger text-white">
                    <div class="card-body d-flex justify-content-between align-items-center">
                        <div>
                            <h6 class="text-uppercase small opacity-75">Sách hết hàng</h6>
                            <h2 class="fw-bold mb-0">${not empty outOfStockBooks ? outOfStockBooks : 0}</h2>
                        </div>
                        <i class="fas fa-exclamation-circle fa-3x opacity-25"></i>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/books?stock=0" class="card-footer bg-dark bg-opacity-10 text-white text-decoration-none small text-center">
                        Cần nhập thêm hàng <i class="fas fa-arrow-up ms-1"></i>
                    </a>
                </div>
            </div>
        </div>

        <div class="row g-4 mb-4">
            <div class="col-md-6">
                <div class="card border-0 shadow-sm border-start border-primary border-4">
                    <div class="card-body">
                        <div class="d-flex justify-content-between">
                            <div>
                                <h6 class="text-muted small text-uppercase">Đơn hàng mới chờ xác nhận</h6>
                                <h3 class="fw-bold text-primary">${not empty pendingOrders ? pendingOrders : 0}</h3>
                            </div>
                            <div class="icon-shape bg-primary-subtle text-primary rounded-circle p-3">
                                <i class="fas fa-shopping-cart fa-lg"></i>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/orders?status=PENDING" class="small text-primary text-decoration-none fw-bold">Xử lý ngay -></a>
                    </div>
                </div>
            </div>
            <div class="col-md-6">
                <div class="card border-0 shadow-sm border-start border-warning border-4">
                    <div class="card-body">
                        <div class="d-flex justify-content-between">
                            <div>
                                <h6 class="text-muted small text-uppercase">Vận đơn đang di chuyển</h6>
                                <h3 class="fw-bold text-warning">${not empty shippingOrders ? shippingOrders : 0}</h3>
                            </div>
                            <div class="icon-shape bg-warning-subtle text-warning rounded-circle p-3">
                                <i class="fas fa-truck-fast fa-lg"></i>
                            </div>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/shipping" class="small text-warning text-decoration-none fw-bold">Theo dõi lộ trình -></a>
                    </div>
                </div>
            </div>
        </div>

        <div class="card border-0 shadow-sm">
            <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                <h5 class="mb-0 fw-bold"><i class="fas fa-history text-primary me-2"></i>Sách vừa cập nhật</h5>
                <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-sm btn-outline-primary">Tất cả sách</a>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="ps-4">Mã</th>
                                <th>Tiêu đề</th>
                                <th>Tác giả</th>
                                <th class="text-end">Giá</th>
                                <th class="text-center">Kho</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="book" items="${books}" varStatus="status">
                                <c:if test="${status.index < 5}">
                                    <tr>
                                        <td class="ps-4 text-muted">#${book.id}</td>
                                        <td class="fw-bold">${book.title}</td>
                                        <td><span class="badge bg-light text-dark border-0 fw-normal">${book.authorName}</span></td>
                                        <td class="text-end text-danger fw-bold">
                                            <fmt:formatNumber value="${book.price}" pattern="#,###"/> ₫
                                        </td>
                                        <td class="text-center">
                                            <c:choose>
                                                <c:when test="${book.stock > 10}">
                                                    <span class="badge bg-success-subtle text-success">${book.stock}</span>
                                                </c:when>
                                                <c:when test="${book.stock > 0}">
                                                    <span class="badge bg-warning-subtle text-warning">${book.stock}</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge bg-danger-subtle text-danger">Hết hàng</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                        <td class="text-center">
                                            <a href="${pageContext.request.contextPath}/admin/books/edit?id=${book.id}" class="btn btn-sm btn-link text-primary p-0">
                                                <i class="fas fa-pencil-alt"></i> Sửa
                                            </a>
                                        </td>
                                    </tr>
                                </c:if>
                            </c:forEach>
                            <c:if test="${empty books}">
                                <tr>
                                    <td colspan="6" class="text-center py-5 text-muted">Hệ thống chưa có dữ liệu sách.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>