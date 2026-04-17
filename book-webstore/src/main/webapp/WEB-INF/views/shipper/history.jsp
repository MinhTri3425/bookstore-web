<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

<div class="container mt-4" style="max-width: 800px;">
    <div class="mb-3">
        <a href="/shipper/dashboard" class="btn btn-sm btn-outline-secondary">
            <i class="fas fa-arrow-left"></i> Quay lại Dashboard
        </a>
    </div>

    <h3 class="mb-4"><i class="fas fa-history text-info"></i> Lịch sử nhận đơn</h3>

    <div class="card border-0 shadow-sm mb-4 bg-primary text-white">
        <div class="card-body d-flex justify-content-between align-items-center">
            <div>
                <p class="mb-0 opacity-75">Tổng thu nhập tích lũy</p>
                <h2 class="mb-0">
                    <fmt:formatNumber value="${totalEarnings}" type="number" /> VND
                </h2>
            </div>
            <i class="fas fa-wallet fa-3x opacity-50"></i>
        </div>
    </div>

    <div class="card border-0 shadow-sm">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="ps-3">Mã đơn</th>
                        <th>Thời gian</th>
                        <th>Thông tin giao</th>
                        <th class="text-end">Phí ship</th>
                        <th class="text-center pe-3">Trạng thái</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${history}">
                        <tr>
                            <td class="ps-3 font-weight-bold">#${s.orderId}</td>
                            <td>
                                <small class="text-muted">${s.createdAtDisplay}</small>
                            </td>
                            <td>
                                <div class="small fw-bold">${s.customerName}</div>
                                <div class="text-muted" style="font-size: 0.75rem;">${s.customerAddress}</div>
                            </td>
                            <td class="text-end fw-bold text-primary">
                                <fmt:formatNumber value="${s.cost}" type="number" />
                            </td>
                            <td class="text-center pe-3">
                                <c:choose>
                                    <c:when test="${s.status == 'DELIVERED'}">
                                        <span class="badge rounded-pill bg-success-subtle text-success border border-success">
                                            <i class="fas fa-check-circle"></i> Thành công
                                        </span>
                                    </c:when>
                                    <c:when test="${s.status == 'CANCELLED' || s.status == 'FAILED'}">
                                        <span class="badge rounded-pill bg-danger-subtle text-danger border border-danger">
                                            <i class="fas fa-times-circle"></i> Thất bại
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge rounded-pill bg-warning-subtle text-warning border border-warning">
                                            <i class="fas fa-clock"></i> ${s.status}
                                        </span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty history}">
                        <tr>
                            <td colspan="5" class="text-center py-5 text-muted">
                                <i class="fas fa-folder-open fa-2x mb-2"></i><br>
                                Chưa có dữ liệu lịch sử.
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>