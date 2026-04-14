<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
        .btn-undo { background-color: #ffc107; color: #212529; font-weight: bold; border: none; transition: 0.3s; }
        .btn-undo:hover { background-color: #e0a800; transform: scale(1.02); }
    </style>
</head>

<body>
    <div class="shipper-dashboard container" style="max-width: 600px; margin: auto; padding: 20px;">

        <%-- Header --%>
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
            <div class="col-8">
                <a href="/shipper/history" class="btn btn-white border w-100 py-2 shadow-sm text-dark fw-bold">
                    <i class="fas fa-history text-info me-2"></i> Lịch sử của tôi
                </a>
            </div>
            <div class="col-4">
                <form action="/shipper/undo" method="post">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                    <button type="submit" class="btn btn-undo w-100 py-2 shadow-sm">
                        <i class="fas fa-undo-alt me-1"></i> Sửa lỗi
                    </button>
                </form>
            </div>
        </div>

        <%-- Thông báo --%>
        <c:if test="${param.success == 'undone'}">
            <div class="alert alert-warning alert-dismissible fade show border-0 shadow-sm" role="alert">
                <i class="fas fa-history me-2"></i> Đã hoàn tác thao tác vừa rồi!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>
        
        <c:if test="${param.success == 'picked'}">
            <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm" role="alert">
                <i class="fas fa-check-circle me-2"></i> Nhận đơn thành công!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <c:if test="${param.success == 'delivered'}">
            <div class="alert alert-primary alert-dismissible fade show border-0 shadow-sm" role="alert">
                <i class="fas fa-medal me-2"></i> Giao hàng thành công!
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <%-- PHẦN 1: CHỢ ĐƠN HÀNG --%>
        <div class="d-flex align-items-center mb-3">
            <h5 class="mb-0 text-danger fw-bold"><i class="fas fa-store me-2"></i>Chợ đơn mới</h5>
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
                            </div>
                            <form action="/shipper/pick-up" method="post">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
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
                    <p class="text-muted mb-0 small italic">Chợ đang trống...</p>
                </div>
            </c:otherwise>
        </c:choose>

        <hr class="my-4">

        <%-- PHẦN 2: NHIỆM VỤ ĐANG GIAO --%>
        <h5 class="mb-3 text-primary fw-bold"><i class="fas fa-truck me-2"></i>Nhiệm vụ đang giao:</h5>
        
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
                                <strong>${order.customerName}</strong><br>
                                <span class="text-danger"><i class="fas fa-map-marker-alt me-1"></i>${order.customerAddress}</span>
                            </div>
                            <button type="button" class="btn btn-success w-100 fw-bold py-2 shadow-sm btn-confirm-delivered"
                                data-bs-toggle="modal" 
                                data-bs-target="#confirmDeliveredModal"
                                data-order-id="${order.orderId}">
                                <i class="fas fa-check-double me-2"></i> GIAO THÀNH CÔNG
                            </button>
                        </div>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <div class="text-center py-5 bg-white rounded border">
                    <p class="text-muted mb-0 small">Bạn chưa có đơn hàng nào.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%-- MODAL XÁC NHẬN (PHẢI CÓ PHẦN NÀY) --%>
    <div class="modal fade" id="confirmDeliveredModal" tabindex="-1" aria-hidden="true">
        <div class="modal-dialog modal-dialog-centered">
            <div class="modal-content border-0 shadow">
                <div class="modal-header">
                    <h5 class="modal-title"><i class="fas fa-check-circle text-success me-2"></i>Xác nhận giao xong</h5>
                    <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    Xác nhận đã thu tiền và giao xong đơn hàng <strong id="deliveredOrderIdText"></strong>?
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                    <form action="/shipper/complete" method="post">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                        <input type="hidden" name="orderId" id="deliveredOrderIdInput" value="">
                        <button type="submit" class="btn btn-success">Xác nhận</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <%-- SCRIPT XỬ LÝ (PHẢI CÓ PHẦN NÀY) --%>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        document.addEventListener('DOMContentLoaded', function () {
            const modalInput = document.getElementById('deliveredOrderIdInput');
            const modalText = document.getElementById('deliveredOrderIdText');

            // Lắng nghe sự kiện click vào các nút "GIAO THÀNH CÔNG"
            document.querySelectorAll('.btn-confirm-delivered').forEach(button => {
                button.addEventListener('click', function () {
                    const orderId = this.getAttribute('data-order-id');
                    modalInput.value = orderId;
                    modalText.textContent = '#' + orderId;
                });
            });
        });
    </script>
</body>
</html>