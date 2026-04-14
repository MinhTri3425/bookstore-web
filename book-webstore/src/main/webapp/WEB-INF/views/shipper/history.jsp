<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Lịch sử nhận đơn - Shipper</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { background-color: #f4f7f6; }
        .table-container { background: white; border-radius: 15px; box-shadow: 0 4px 12px rgba(0,0,0,0.05); }
        .status-badge { font-size: 0.75rem; font-weight: 600; }
    </style>
</head>
<body>

<div class="container mt-4 mb-5" style="max-width: 850px;">
    
    <%-- Nút quay lại Dashboard --%>
    <div class="mb-4">
        <a href="/shipper/dashboard" class="btn btn-sm btn-light border shadow-sm">
            <i class="fas fa-arrow-left me-1"></i> Quay lại Dashboard
        </a>
    </div>

    <div class="d-flex align-items-center mb-4">
        <div class="bg-info text-white rounded-3 p-3 me-3">
            <i class="fas fa-history fa-lg"></i>
        </div>
        <div>
            <h3 class="fw-bold mb-0">Lịch sử nhận đơn</h3>
            <p class="text-muted small mb-0">Danh sách các đơn hàng bạn đã đảm nhận</p>
        </div>
    </div>

    <div class="table-container overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-4 py-3 text-uppercase small fw-bold text-muted">Mã đơn</th>
                        <th class="py-3 text-uppercase small fw-bold text-muted">Thời gian</th>
                        <th class="py-3 text-uppercase small fw-bold text-muted">Thông tin khách</th>
                        <th class="py-3 text-uppercase small fw-bold text-muted text-end">Phí ship</th>
                        <th class="py-3 text-uppercase small fw-bold text-muted text-center">Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${history}">
                        <tr>
                            <%-- Mã đơn hàng --%>
                            <td class="ps-4">
                                <span class="fw-bold text-dark">#${s.orderId}</span>
                            </td>

                            <%-- Thời gian tạo snapshot --%>
                            <td>
                                <div class="small text-muted">
                                    <i class="far fa-clock me-1"></i>${s.createdAtDisplay}
                                </div>
                            </td>

                            <%-- Thông tin khách hàng (Snapshot) --%>
                            <td>
                                <div class="fw-bold text-dark small">${s.customerName}</div>
                                <div class="text-muted truncate" style="font-size: 0.75rem; max-width: 200px;" title="${s.customerAddress}">
                                    <i class="fas fa-map-marker-alt me-1 text-danger"></i>${s.customerAddress}
                                </div>
                            </td>

                            <%-- Phí Ship (Dữ liệu Snapshot từ Strategy) --%>
                            <td class="text-end">
                                <div class="fw-bold text-primary">
                                    <c:choose>
                                        <c:when test="${s.cost > 0}">
                                            <fmt:formatNumber value="${s.cost}" groupingUsed="true" /> ₫
                                        </c:when>
                                        <c:otherwise>
                                            <span class="text-success small">Miễn phí</span>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </td>

                            <%-- Trạng thái vận chuyển --%>
                            <td class="text-center pe-3">
                                <c:choose>
                                    <c:when test="${s.status == 'DELIVERED'}">
                                        <span class="badge bg-success-subtle text-success border border-success px-2 py-1 status-badge">
                                            <i class="fas fa-check me-1"></i>THÀNH CÔNG
                                        </span>
                                    </c:when>
                                    <c:when test="${s.status == 'CANCELLED' || s.status == 'FAILED'}">
                                        <span class="badge bg-danger-subtle text-danger border border-danger px-2 py-1 status-badge">
                                            <i class="fas fa-times me-1"></i>THẤT BẠI
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning-subtle text-warning border border-warning px-2 py-1 status-badge">
                                            ${s.status}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <%-- Trường hợp chưa có dữ liệu --%>
                    <c:if test="${empty history}">
                        <tr>
                            <td colspan="5" class="text-center py-5">
                                <div class="py-3">
                                    <i class="fas fa-clipboard-list fa-3x text-light mb-3"></i>
                                    <p class="text-muted">Bạn chưa hoàn thành bất kỳ đơn hàng nào.</p>
                                    <a href="/shipper/dashboard" class="btn btn-sm btn-primary">Đến chợ nhận đơn ngay</a>
                                </div>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>