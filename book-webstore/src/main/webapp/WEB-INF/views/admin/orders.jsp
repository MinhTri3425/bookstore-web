<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-cog me-2"></i> Admin Panel
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a></li>
                <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/orders">Đơn hàng</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/authors">Tác giả</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/categories">Danh mục</a></li>
                <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/coupons">Coupon</a></li>
            </ul>
        </div>
    </div>
</nav>

<div class="container-fluid py-4">
    <div class="row">
        <div class="col-md-3">
            <div class="card border-0 shadow-sm overflow-hidden">
                <div class="card-header bg-white py-3 border-0">
                    <h5 class="mb-0 fw-bold">Menu Quản lý</h5>
                </div>
                <div class="list-group list-group-flush sidebar-nav">
                    <a href="${pageContext.request.contextPath}/admin/dashboard" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/books" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-book me-2"></i> Quản lý sách
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/orders" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                        <i class="fas fa-receipt me-2"></i> Đơn hàng
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/authors" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-user me-2"></i> Tác giả
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-tags me-2"></i> Danh mục
                    </a>
                    <a href="${pageContext.request.contextPath}/admin/coupons" class="list-group-item list-group-item-action border-0 px-4 py-3">
                        <i class="fas fa-ticket-alt me-2"></i> Coupon
                    </a>
                </div>
            </div>
        </div>

        <div class="col-md-9">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <div>
                    <div class="text-muted small text-uppercase">${pageEyebrow}</div>
                    <h2 class="fw-bold text-dark mb-0"><i class="fas fa-receipt me-2 text-primary"></i>${pageHeading}</h2>
                </div>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>

            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <form class="row g-3" method="get" action="${pageContext.request.contextPath}${listPath}">
                        <div class="col-md-5">
                            <label for="orderId" class="form-label fw-semibold">Mã đơn hàng</label>
                            <input id="orderId" name="orderId" type="text" class="form-control" value="${searchOrderId}" placeholder="Nhập đúng ID đơn hàng">
                        </div>
                        <div class="col-md-4">
                            <label for="status" class="form-label fw-semibold">Trạng thái</label>
                            <select id="status" name="status" class="form-select">
                                <option value="">Tất cả trạng thái</option>
                                <c:forEach var="statusOption" items="${statusOptions}">
                                    <option value="${statusOption}" ${selectedStatus == statusOption.toString() ? 'selected' : ''}>${statusOption}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 d-flex align-items-end gap-2">
                            <button class="btn btn-primary w-100" type="submit">Lọc</button>
                            <a class="btn btn-light border w-100" href="${pageContext.request.contextPath}${listPath}">Reset</a>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách đơn hàng</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                        <tr>
                            <th class="px-4">Mã đơn</th>
                            <th>Trạng thái</th>
                            <th>Ngày tạo</th>
                            <th>Số món</th>
                            <th>Tổng tiền</th>
                            <th>Khách hàng</th>
                            <th>Thanh toán</th>
                            <th>Giao hàng</th>
                            <th class="text-center">Chi tiết</th>
                        </tr>
                        </thead>
                        <tbody>
                        <c:choose>
                            <c:when test="${not empty orders}">
                                <c:forEach var="order" items="${orders}">
                                    <tr>
                                        <td class="px-4 fw-semibold">#${order.id}</td>
                                        <td><span class="badge ${order.statusCssClass}">${order.status}</span></td>
                                        <td>${order.createdAtDisplay}</td>
                                        <td>${order.itemCount}</td>
                                        <td>${order.totalAmountDisplay}</td>
                                        <td>${order.customerName}</td>
                                        <td>${order.paymentStatusDisplay}</td>
                                        <td>${order.shippingStatusDisplay}</td>
                                        <td class="text-center">
                                            <a class="btn btn-outline-primary btn-sm" href="${pageContext.request.contextPath}${detailBasePath}/${order.id}">
                                                <i class="fas fa-eye me-1"></i>Xem
                                            </a>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </c:when>
                            <c:otherwise>
                                <tr>
                                    <td colspan="9" class="text-center py-5 text-muted">Không tìm thấy đơn hàng phù hợp.</td>
                                </tr>
                            </c:otherwise>
                        </c:choose>
                        </tbody>
                    </table>
                </div>
                <div class="card-body border-top d-flex justify-content-between align-items-center flex-wrap gap-3">
                    <div class="text-muted small">
                        <c:set var="fromIndex" value="${totalElements == 0 ? 0 : (currentPage * pageSize) + 1}" />
                        <c:set var="toIndex" value="${totalElements == 0 ? 0 : ((currentPage + 1) * pageSize < totalElements ? (currentPage + 1) * pageSize : totalElements)}" />
                        Hiển thị ${fromIndex} - ${toIndex} trên tổng ${totalElements} đơn hàng
                    </div>
                    <c:if test="${totalPages > 1}">
                        <nav>
                            <ul class="pagination mb-0">
                                <c:if test="${currentPage > 0}">
                                    <c:url var="prevUrl" value="${listPath}">
                                        <c:param name="page" value="${currentPage - 1}" />
                                        <c:param name="size" value="${pageSize}" />
                                        <c:if test="${not empty selectedStatus}">
                                            <c:param name="status" value="${selectedStatus}" />
                                        </c:if>
                                        <c:if test="${not empty searchOrderId}">
                                            <c:param name="orderId" value="${searchOrderId}" />
                                        </c:if>
                                    </c:url>
                                    <li class="page-item"><a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Trước</a></li>
                                </c:if>
                                <c:forEach var="pageIndex" begin="0" end="${totalPages - 1}">
                                    <c:url var="pageUrl" value="${listPath}">
                                        <c:param name="page" value="${pageIndex}" />
                                        <c:param name="size" value="${pageSize}" />
                                        <c:if test="${not empty selectedStatus}">
                                            <c:param name="status" value="${selectedStatus}" />
                                        </c:if>
                                        <c:if test="${not empty searchOrderId}">
                                            <c:param name="orderId" value="${searchOrderId}" />
                                        </c:if>
                                    </c:url>
                                    <li class="page-item ${pageIndex == currentPage ? 'active' : ''}">
                                        <a class="page-link" href="${pageContext.request.contextPath}${pageUrl}">${pageIndex + 1}</a>
                                    </li>
                                </c:forEach>
                                <c:if test="${currentPage + 1 < totalPages}">
                                    <c:url var="nextUrl" value="${listPath}">
                                        <c:param name="page" value="${currentPage + 1}" />
                                        <c:param name="size" value="${pageSize}" />
                                        <c:if test="${not empty selectedStatus}">
                                            <c:param name="status" value="${selectedStatus}" />
                                        </c:if>
                                        <c:if test="${not empty searchOrderId}">
                                            <c:param name="orderId" value="${searchOrderId}" />
                                        </c:if>
                                    </c:url>
                                    <li class="page-item"><a class="page-link" href="${pageContext.request.contextPath}${nextUrl}">Sau</a></li>
                                </c:if>
                            </ul>
                        </nav>
                    </c:if>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
