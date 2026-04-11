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
                    <a href="${pageContext.request.contextPath}${backPath}" class="text-decoration-none small text-muted">Quay lại danh sách đơn hàng</a>
                    <h2 class="fw-bold text-dark mb-0 mt-1"><i class="fas fa-file-invoice me-2 text-primary"></i>Đơn hàng #${order.id}</h2>
                </div>
                <span class="badge ${order.statusCssClass}">${order.status}</span>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success">${successMessage}</div>
            </c:if>

            <div class="row g-4 mb-4">
                <div class="col-lg-6">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white py-3"><h5 class="mb-0">Tổng quan</h5></div>
                        <div class="card-body">
                            <div class="row g-3">
                                <div class="col-sm-6"><strong>Ngày tạo:</strong><div>${order.createdAtDisplay}</div></div>
                                <div class="col-sm-6"><strong>Khách hàng:</strong><div>${order.customerName}</div></div>
                                <div class="col-sm-6"><strong>Số món:</strong><div>${order.itemCount}</div></div>
                                <div class="col-sm-6"><strong>Tổng tiền:</strong><div>${order.totalAmountDisplay}</div></div>
                                <div class="col-sm-6"><strong>Thanh toán:</strong><div>${order.paymentStatusDisplay}</div></div>
                                <div class="col-sm-6"><strong>Giao hàng:</strong><div>${order.shippingStatusDisplay}</div></div>
                                <div class="col-sm-6"><strong>Phương thức ship:</strong><div>${order.shippingMethod}</div></div>
                                <div class="col-sm-6"><strong>Shipper:</strong><div>${order.shipperName}</div></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white py-3"><h5 class="mb-0">Sản phẩm</h5></div>
                        <div class="card-body">
                            <c:choose>
                                <c:when test="${not empty order.items}">
                                    <div class="list-group list-group-flush">
                                        <c:forEach var="item" items="${order.items}">
                                            <div class="list-group-item px-0">
                                                <div class="fw-semibold">${item.bookTitle}</div>
                                                <div class="small text-muted">Item ID: ${item.id} - Số lượng: ${item.quantity}</div>
                                            </div>
                                        </c:forEach>
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <div class="text-muted">Đơn hàng này chưa có sản phẩm.</div>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                </div>
            </div>

            <div class="row g-4">
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white py-3"><h5 class="mb-0">Cập nhật đơn hàng</h5></div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/status">
                                <div class="mb-3">
                                    <label for="orderStatus" class="form-label">Trạng thái đơn</label>
                                    <select id="orderStatus" name="status" class="form-select">
                                        <c:forEach var="statusOption" items="${orderStatusOptions}">
                                            <option value="${statusOption}" ${order.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <button class="btn btn-primary w-100" type="submit">Lưu trạng thái đơn</button>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white py-3"><h5 class="mb-0">Cập nhật thanh toán</h5></div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/payment">
                                <div class="mb-3">
                                    <label for="paymentStatus" class="form-label">Trạng thái thanh toán</label>
                                    <select id="paymentStatus" name="status" class="form-select">
                                        <c:forEach var="statusOption" items="${paymentStatusOptions}">
                                            <option value="${statusOption}" ${order.payment != null and order.payment.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <button class="btn btn-primary w-100" type="submit">Lưu thanh toán</button>
                            </form>
                        </div>
                    </div>
                </div>
                <div class="col-lg-4">
                    <div class="card border-0 shadow-sm h-100">
                        <div class="card-header bg-white py-3"><h5 class="mb-0">Cập nhật giao hàng</h5></div>
                        <div class="card-body">
                            <form method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/shipping">
                                <div class="mb-3">
                                    <label for="shippingStatus" class="form-label">Trạng thái giao hàng</label>
                                    <select id="shippingStatus" name="status" class="form-select">
                                        <c:forEach var="statusOption" items="${shippingStatusOptions}">
                                            <option value="${statusOption}" ${order.shipping != null and order.shipping.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <div class="mb-3">
                                    <label for="shipperId" class="form-label">Shipper</label>
                                    <select id="shipperId" name="shipperId" class="form-select">
                                        <option value="">Giữ shipper hiện tại</option>
                                        <c:forEach var="shipper" items="${shipperOptions}">
                                            <option value="${shipper.id}" ${order.shipping != null and order.shipping.shipperId == shipper.id ? 'selected' : ''}>${shipper.name}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                                <button class="btn btn-primary w-100" type="submit">Lưu giao hàng</button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
