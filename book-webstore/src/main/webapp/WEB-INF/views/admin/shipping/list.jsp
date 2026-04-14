<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

<div class="admin-shipping-container container-fluid mt-4 mb-5">
    <%-- Tiêu đề và Tổng số lượng --%>
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3 class="fw-bold"><i class="fas fa-truck-loading text-primary me-2"></i>Giám sát Vận chuyển</h3>
        <span class="badge bg-secondary shadow-sm px-3 py-2">
            <i class="fas fa-list-ul me-1"></i> Tổng số: ${shippings != null ? shippings.size() : 0} vận đơn
        </span>
    </div>

    <%-- Bảng danh sách vận chuyển --%>
    <div class="card shadow-sm border-0 overflow-hidden">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th class="ps-4 py-3" style="width: 12%;">Mã Đơn</th>
                        <th style="width: 25%;">Khách hàng</th>
                        <th style="width: 18%;">Phí Ship</th>
                        <th style="width: 20%;">Trạng thái</th>
                        <th style="width: 15%;">ID Shipper</th>
                        <th class="text-center pe-4" style="width: 10%;">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${shippings}">
                        <tr>
                            <%-- Mã đơn --%>
                            <td class="ps-4">
                                <span class="fw-bold text-primary">#${s.orderId}</span>
                            </td>

                            <%-- Thông tin khách --%>
                            <td>
                                <div class="fw-bold text-dark small">${s.customerName}</div>
                                <div class="text-muted small" style="font-size: 0.75rem;">
                                    <i class="fas fa-phone-alt me-1 text-secondary"></i>${s.customerPhone}
                                </div>
                            </td>

                            <%-- Phí ship (Dữ liệu Snapshot) --%>
                            <td>
                                <div class="fw-bold text-dark">
                                    <fmt:formatNumber value="${s.cost}" groupingUsed="true" /> ₫
                                </div>
                                <div class="text-muted" style="font-size: 0.7rem;">${s.method}</div>
                            </td>

                            <%-- Trạng thái --%>
                            <td>
                                <c:choose>
                                    <c:when test="${s.status == 'DELIVERED'}">
                                        <span class="badge bg-success-subtle text-success border border-success px-2 py-1">
                                            <i class="fas fa-check-circle me-1"></i>Thành công
                                        </span>
                                    </c:when>
                                    <c:when test="${s.status == 'SHIPPING'}">
                                        <span class="badge bg-primary-subtle text-primary border border-primary px-2 py-1">
                                            <i class="fas fa-shipping-fast me-1"></i>Đang giao
                                        </span>
                                    </c:when>
                                    <c:when test="${s.status == 'PENDING'}">
                                        <span class="badge bg-warning-subtle text-warning border border-warning px-2 py-1 text-uppercase" style="font-size: 0.65rem;">
                                            Chờ shipper
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-light text-dark border px-2 py-1 small">${s.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            
                            <%-- ID Shipper --%>
                            <td>
                                <c:choose>
                                    <c:when test="${not empty s.shipperId}">
                                        <span class="badge bg-dark rounded-pill px-3" style="font-size: 0.75rem;">
                                            ID: #${s.shipperId}
                                        </span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="text-muted small italic">Chưa có</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>

                            <%-- Nút xem chi tiết --%>
                            <td class="text-center pe-4">
                                <a href="/admin/orders/${s.orderId}" class="btn btn-sm btn-outline-info rounded-pill px-3 shadow-sm">
                                    <i class="fas fa-eye me-1"></i>Xem
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <%-- Nếu danh sách trống --%>
                    <c:if test="${empty shippings}">
                        <tr>
                            <td colspan="6" class="text-center py-5">
                                <div class="opacity-50">
                                    <i class="fas fa-inbox fa-3x mb-3 text-muted"></i>
                                    <p class="mb-0">Hiện chưa có dữ liệu vận chuyển nào.</p>
                                </div>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />