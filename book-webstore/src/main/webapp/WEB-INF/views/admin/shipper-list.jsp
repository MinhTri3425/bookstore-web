<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

<div class="container py-5">
    <div class="row mb-4 align-items-end">
        <div class="col-md-6">
            <h2 class="fw-bold mb-1"><i class="fas fa-user-shield text-primary me-2"></i>Quản lý Shipper</h2>
            <p class="text-muted small">Cấp quyền hoặc thu hồi quyền giao hàng của người dùng.</p>
        </div>
        <div class="col-md-6">
            <div class="card border-0 shadow-sm">
                <div class="card-body p-3">
                    <form action="${pageContext.request.contextPath}/admin/shippers/promote" method="post" class="d-flex gap-2">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="email" name="email" class="form-control" placeholder="Nhập Email khách hàng để nâng cấp..." required>
                        <button type="submit" class="btn btn-primary text-nowrap">
                            <i class="fas fa-plus"></i> Thêm Shipper
                        </button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <c:if test="${not empty success}">
        <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
            <i class="fas fa-check-circle me-2"></i> ${success}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
            <i class="fas fa-exclamation-triangle me-2"></i> ${error}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        </div>
    </c:if>

    <div class="card border-0 shadow-sm">
        <div class="card-header bg-white py-3">
            <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách shipper đang hoạt động</h5>
        </div>
        <div class="table-responsive">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-light">
                    <tr>
                        <th class="px-4">ID</th>
                        <th>Thông tin tài khoản</th>
                        <th>Liên hệ</th>
                        <th class="text-center">Trạng thái</th>
                        <th class="text-center">Thao tác</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="s" items="${shippers}">
                        <tr>
                            <td class="px-4 text-muted fw-bold">#${s.id}</td>
                            <td>
                                <div class="fw-bold text-dark">${s.name}</div>
                                <div class="small text-muted">${s.email}</div>
                            </td>
                            <td>
                                <div class="small"><i class="fas fa-phone-alt me-1 text-muted"></i> ${s.phoneNumber}</div>
                            </td>
                            <td class="text-center">
                                <span class="badge bg-success-subtle text-success rounded-pill px-3">Active</span>
                            </td>
                            <td class="text-center">
                                <form action="${pageContext.request.contextPath}/admin/shippers/${s.id}/revoke" method="post" 
                                      onsubmit="return confirm('Thu hồi quyền shipper của người này? Họ sẽ không thể nhận đơn mới.')">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                    <button type="submit" class="btn btn-sm btn-outline-danger shadow-sm">
                                        <i class="fas fa-user-minus"></i> Thu hồi quyền
                                    </button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty shippers}">
                        <tr>
                            <td colspan="5" class="text-center py-5">
                                <i class="fas fa-user-slash fa-3x text-muted opacity-25 mb-3"></i>
                                <p class="text-muted">Chưa có shipper nào được cấp quyền.</p>
                            </td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<jsp:include page="/WEB-INF/views/fragments/footer.jsp" />