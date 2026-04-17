<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chợ đơn hàng</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body class="bg-light">

    <div class="container mt-4">
        <div class="d-flex justify-content-between align-items-center mb-4">
            <h2 class="h3"><i class="fas fa-box-open text-primary"></i> Đơn hàng sẵn sàng nhận</h2>
            <a href="/shipper/dashboard" class="btn btn-outline-secondary">
                <i class="fas fa-arrow-left"></i> Dashboard
            </a>
        </div>

        <c:if test="${not empty param.error}">
            <div class="alert alert-danger alert-dismissible fade show shadow-sm" role="alert">
                <strong><i class="fas fa-exclamation-triangle"></i> Lỗi:</strong> ${param.error}
                <button type="button" class="close" data-dismiss="alert" aria-label="Close">
                    <span aria-hidden="true">&times;</span>
                </button>
            </div>
        </c:if>

        <div class="row">
            <c:forEach var="order" items="${pendingOrders}">
                <div class="col-md-6 col-lg-4 mb-4">
                    <div class="card h-100 shadow-sm border-0">
                        <div class="card-header bg-white border-bottom-0 pt-3">
                            <h5 class="card-title text-primary mb-0">Đơn hàng #${order.id}</h5>
                        </div>
                        <div class="card-body">
                            <p class="card-text">
                                <i class="fas fa-map-marker-alt text-danger"></i> <strong>Địa chỉ:</strong> ${order.address}<br>
                                <i class="fas fa-user text-secondary"></i> <strong>Khách:</strong> ${order.customerName}<br>
                                <i class="fas fa-phone text-success"></i> <strong>SĐT:</strong> ${order.customerPhone}
                            </p>
                        </div>
                        <div class="card-footer bg-white border-top-0 pb-3">
                            <form action="/shipper/accept" method="post">
                                <input type="hidden" name="orderId" value="${order.id}">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="btn btn-success btn-block font-weight-bold">
                                    Nhận giao đơn này
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </c:forEach>

            <c:if test="${empty pendingOrders}">
                <div class="col-12 text-center py-5">
                    <div class="mb-3">
                        <i class="fas fa-pouch text-muted" style="font-size: 3rem;"></i>
                    </div>
                    <p class="text-muted h5">Hiện chưa có đơn hàng nào mới.</p>
                    <p class="text-muted small">Hãy quay lại sau vài phút hoặc làm mới trang nhé!</p>
                    <button class="btn btn-sm btn-link" onclick="location.reload()">Làm mới trang</button>
                </div>
            </c:if>
        </div>
    </div>

    <script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@4.6.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>