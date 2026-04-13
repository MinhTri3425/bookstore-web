<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

<div class="shipper-dashboard container" style="max-width: 600px; margin: auto; padding: 20px;">
    
    <div class="d-flex justify-content-between align-items-center mb-3">
        <h4>Chào, <span class="text-primary">${shipper.name}</span></h4>
        <a href="/logout" class="btn btn-sm btn-outline-danger">Đăng xuất</a>
    </div>

    <div class="row g-2 mb-4">
        <div class="col-6">
            <a href="/shipper/available-orders" class="btn btn-primary w-100 py-3 shadow-sm">
                <i class="fas fa-search-location"></i> <br> Nhặt đơn mới
            </a>
        </div>
        <div class="col-6">
            <a href="/shipper/history" class="btn btn-info text-white w-100 py-3 shadow-sm">
                <i class="fas fa-history"></i> <br> Lịch sử giao
            </a>
        </div>
    </div>

    <hr>
    <h5 class="mb-3"><i class="fas fa-truck text-warning"></i> Nhiệm vụ đang thực hiện:</h5>

    <c:if test="${empty orders}">
        <div class="text-center py-5 border rounded bg-white">
            <i class="fas fa-box-open fa-3x text-muted mb-3"></i>
            <p class="text-muted">Bạn hiện không có đơn hàng nào đang giao.</p>
            <a href="/shipper/available-orders" class="btn btn-sm btn-link">Đi nhặt đơn ngay!</a>
        </div>
    </c:if>

    <c:forEach var="order" items="${orders}">
        <div class="card mb-3 shadow-sm border-0">
            <div class="card-body p-4">
                <div class="d-flex justify-content-between">
                    <h6 class="card-title font-weight-bold">Mã đơn: #${order.orderId}</h6>
                    <span class="badge bg-primary pt-2">${order.status}</span>
                </div>
                
                <hr class="my-2">
                
                <p class="mb-1"><i class="fas fa-user-circle text-secondary"></i> ${order.customerName}</p>
                <p class="mb-1 text-danger font-weight-bold"><i class="fas fa-map-marker-alt"></i> ${order.customerAddress}</p>
                <p class="mb-2"><i class="fas fa-phone-alt text-success"></i> ${order.customerPhone}</p>
                
                <c:if test="${not empty order.note}">
                    <div class="alert alert-warning py-1 px-2 small mb-3">
                        <i class="fas fa-sticky-note"></i> Ghi chú: ${order.note}
                    </div>
                </c:if>

                <div class="d-flex justify-content-between align-items-center mt-3">
                    <span class="h6 mb-0 text-primary font-weight-bold">${order.cost} VND</span>
                    
                    <form action="/shipper/complete" method="post" style="width: 60%;">
                         <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="orderId" value="${order.orderId}">
                        <button type="submit" class="btn btn-success w-100 font-weight-bold" 
                                onclick="return confirm('Bạn đã chắc chắn giao hàng thành công?')">
                            Giao thành công
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </c:forEach>
</div>