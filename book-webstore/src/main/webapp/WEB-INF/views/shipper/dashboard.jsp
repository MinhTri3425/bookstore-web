<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Shipper Dashboard - BookStore</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { background-color: #f8f9fa; }
        .card { border-radius: 12px; }
        .btn-pickup { background-color: #dc3545; color: white; border: none; }
        .btn-pickup:hover { background-color: #bb2d3b; color: white; }
        .badge-status { font-size: 0.75rem; padding: 5px 10px; }
    </style>
</head>
<body>

<div class="shipper-dashboard container" style="max-width: 600px; margin: auto; padding: 20px;">
    
    <%-- Header: Thông tin Shipper --%>
    <div class="card border-0 shadow-sm mb-3">
        <div class="card-body d-flex justify-content-between align-items-center">
            <div>
                <h5 class="mb-0 fw-bold text-dark">Chào, <span class="text-primary">${shipper.name}</span></h5>
                <small class="text-success"><i class="fas fa-circle me-1" style="font-size: 8px;"></i> Đang trực tuyến</small>
            </div>
            <a href="/logout" class="btn btn-sm btn-outline-danger border-0">
                <i class="fas fa-sign-out-alt"></i> Thoát
            </a>
        </div>
    </div>

    <%-- Thanh công cụ nhanh --%>
    <div class="row g-2 mb-4">
        <div class="col-12">
            <a href="/shipper/history" class="btn btn-white border w-100 py-2 shadow-sm text-dark fw-bold">
                <i class="fas fa-history text-info me-2"></i> Lịch sử giao hàng của tôi
            </a>
        </div>
    </div>

    <%-- Thông báo thao tác --%>
    <c:if test="${param.success == 'picked'}">
        <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
            <i class="fas fa-check-circle me-2"></i> Bạn đã nhận đơn hàng thành công!
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${param.success == 'delivered'}">
        <div class="alert alert-primary alert-dismissible fade show border-0 shadow-sm" role="alert">
            <i class="fas fa-medal me-2"></i> Giao hàng thành công! Cố gắng đơn tiếp theo nhé.
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    </c:if>
    <c:if test="${not empty param.error}">
        <div class="alert alert-danger border-0 shadow-sm" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i> ${param.error}
        </div>
    </c:if>

    <%-- PHẦN 1: CHỢ ĐƠN HÀNG (Đơn chung chưa ai nhận) --%>
    <div class="d-flex align-items-center mb-3">
        <h5 class="mb-0 text-danger fw-bold"><i class="fas fa-store me-2"></i>Chợ đơn hàng mới</h5>
        <span class="ms-2 badge rounded-pill bg-danger">${marketOrders.size()} đơn</span>
    </div>

    <c:choose>
        <c:when test="${not empty marketOrders}">
            <c:forEach var="item" items="${marketOrders}">
                <div class="card mb-3 shadow-sm border-0 border-start border-danger border-4">
                    <div class="card-body p-3">
                        <div class="d-flex justify-content-between align-items-start mb-2">
                            <div>
                                <small class="text-muted d-block">Mã đơn: #${item.orderId}</small>
                                <strong class="text-dark d-block mb-1">${item.customerAddress}</strong>
                            </div>
                            <span class="badge bg-light text-danger border border-danger badge-status">CÓ ĐƠN MỚI</span>
                        </div>
                        
                        <div class="small text-muted mb-3">
                            <i class="fas fa-sticky-note me-1"></i> Ghi chú: ${not empty item.note ? item.note : "Không có"}
                        </div>

                        <form action="/shipper/pick-up" method="post">
                            <input type="hidden" name="shippingId" value="${item.id}">
                            <button type="submit" class="btn btn-pickup w-100 fw-bold py-2 shadow-sm">
                                <i class="fas fa-hand-pointer me-2"></i> NHẬN ĐƠN NGAY
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="text-center py-4 bg-white rounded border border-dashed mb-4">
                <p class="text-muted mb-0 small italic">Hiện tại chưa có đơn hàng nào trong chợ...</p>
            </div>
        </c:otherwise>
    </c:choose>

    <hr class="my-4">

    <%-- PHẦN 2: NHIỆM VỤ ĐANG THỰC HIỆN (Đơn đã nhận) --%>
    <h5 class="mb-3 text-primary fw-bold"><i class="fas fa-truck me-2"></i>Nhiệm vụ bạn đang giao:</h5>

    <c:choose>
        <c:when test="${not empty myActiveOrders}">
            <c:forEach var="order" items="${myActiveOrders}">
                <div class="card mb-3 shadow border-0 border-start border-primary border-4">
                    <div class="card-body p-3">
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <h6 class="fw-bold text-primary mb-0">Đơn hàng #${order.orderId}</h6>
                            <span class="badge bg-primary badge-status">ĐANG GIAO</span>
                        </div>
                        
                        <div class="bg-light p-3 rounded mb-3">
                            <div class="mb-2">
                                <i class="fas fa-user-circle text-muted me-2"></i> <strong>${order.customerName}</strong>
                            </div>
                            <div class="mb-2 text-danger">
                                <i class="fas fa-map-marker-alt me-2"></i> <strong>${order.customerAddress}</strong>
                            </div>
                            <div>
                                <a href="tel:${order.customerPhone}" class="text-decoration-none text-success fw-bold">
                                    <i class="fas fa-phone-alt me-2"></i> ${order.customerPhone} (Bấm để gọi)
                                </a>
                            </div>
                        </div>
                        
                        <c:if test="${not empty order.note}">
                            <div class="small text-muted mb-3 italic">
                                <i class="fas fa-quote-left text-warning me-1"></i> ${order.note}
                            </div>
                        </c:if>

                        <form action="/shipper/complete" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <input type="hidden" name="orderId" value="${order.orderId}">
                            <button type="submit" class="btn btn-success w-100 fw-bold py-2 shadow-sm" 
                                    onclick="return confirm('Bạn đã thu tiền và giao hàng thành công?')">
                                <i class="fas fa-check-double me-2"></i> GIAO THÀNH CÔNG
                            </button>
                        </form>
                    </div>
                </div>
            </c:forEach>
        </c:when>
        <c:otherwise>
            <div class="text-center py-5 bg-white rounded border">
                <i class="fas fa-box-open fa-2x text-muted mb-2"></i>
                <p class="text-muted mb-0 small">Bạn chưa đảm nhận đơn hàng nào.</p>
                <small class="text-primary mt-2 d-block">Hãy nhặt đơn ở "Chợ đơn hàng" phía trên!</small>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>