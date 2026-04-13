<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

<div class="admin-shipping-container container-fluid mt-4">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h3><i class="fas fa-truck-loading text-primary"></i> Giám sát Vận chuyển</h3>
        <span class="badge bg-secondary">Tổng số: ${shippings.size()} vận đơn</span>
    </div>
    
    <div class="card shadow-sm mb-4">
        <div class="card-body">
            <form action="/admin/shipping" method="get" class="row g-3">
                <div class="col-md-3">
                    <input type="text" name="orderId" placeholder="Mã đơn hàng (#...)" value="${searchOrderId}" class="form-control">
                </div>
                <div class="col-md-3">
                    <select name="status" class="form-select">
                        <option value="">-- Tất cả trạng thái --</option>
                        <c:forEach var="st" items="${statusOptions}">
                            <option value="${st}" ${st == selectedStatus ? 'selected' : ''}>${st}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-2">
                    <button type="submit" class="btn btn-primary w-100"><i class="fas fa-filter"></i> Lọc</button>
                </div>
                <div class="col-md-2">
                    <a href="/admin/shipping" class="btn btn-outline-secondary w-100">Reset</a>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm">
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark">
                    <tr>
                        <th>Mã Đơn</th>
                        <th>Thông tin khách</th>
                        <th>Phí Ship</th>
                        <th>Trạng thái</th>
                        <th>Shipper đảm nhận</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${shippings}">
                        <tr>
                            <td>
                                <a href="${pageContext.request.contextPath}/admin/orders/${s.orderId}" class="fw-bold text-decoration-none">
                                    #${s.orderId} <i class="fas fa-external-link-alt ms-1 small"></i>
                                </a>
                            </td>
                            <td>
                                <div class="small">${s.customerName}</div>
                                <div class="text-muted small">${s.customerPhone}</div>
                            </td>
                            <td>
                                <span class="text-primary fw-bold">
                                    <fmt:formatNumber value="${s.cost}" type="number" /> VND
                                </span>
                            </td>
                            <td>
                                <c:choose>
                                    <c:when test="${s.status == 'DELIVERED'}">
                                        <span class="badge bg-success"><i class="fas fa-check"></i> Hoàn thành</span>
                                    </c:when>
                                    <c:when test="${s.status == 'SHIPPING'}">
                                        <span class="badge bg-primary text-white"><i class="fas fa-shipping-fast"></i> Đang giao</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="badge bg-warning text-dark">${s.status}</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td>
                                <form action="/admin/shipping/${s.orderId}/update" method="post" class="d-flex gap-2">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    
                                    <select name="shipperId" class="form-select form-select-sm" style="min-width: 140px;">
                                        <option value="">-- Chưa gán --</option>
                                        <c:forEach var="shipper" items="${shipperOptions}">
                                            <option value="${shipper.id}" ${shipper.id == s.shipperId ? 'selected' : ''}>
                                                ${shipper.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                    <input type="hidden" name="status" value="${s.status}">
                                    <button type="submit" class="btn btn-sm btn-outline-dark" title="Lưu thay đổi">
                                        <i class="fas fa-save"></i>
                                    </button>
                                </form>
                            </td>
                            <td class="text-center">
                                <a href="/admin/orders/${s.orderId}" class="btn btn-sm btn-info text-white">
                                    <i class="fas fa-eye"></i> Chi tiết
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                    
                    <c:if test="${empty shippings}">
                        <tr>
                            <td colspan="6" class="text-center py-5 text-muted">Không tìm thấy vận đơn nào.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />