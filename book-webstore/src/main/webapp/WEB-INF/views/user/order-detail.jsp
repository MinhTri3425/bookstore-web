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
            <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
        </head>

        <body>
            <%@ include file="/WEB-INF/views/fragments/header.jsp" %>

                <div class="container py-4">
                    <div class="row g-4">
                        <div class="col-lg-3">
                            <div class="card shadow-sm border-0">
                                <div class="card-header bg-white py-3">
                                    <h5 class="mb-0 fw-bold">Tài khoản</h5>
                                </div>
                                <div class="list-group list-group-flush sidebar-nav">
                                    <a href="${pageContext.request.contextPath}/books"
                                        class="list-group-item list-group-item-action border-0 px-4 py-3">
                                        <i class="fas fa-book me-2"></i> Sách
                                    </a>
                                    <a href="${pageContext.request.contextPath}/cart"
                                        class="list-group-item list-group-item-action border-0 px-4 py-3">
                                        <i class="fas fa-shopping-cart me-2"></i> Giỏ hàng
                                    </a>
                                    <a href="${pageContext.request.contextPath}/my-orders"
                                        class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                                        <i class="fas fa-receipt me-2"></i> Đơn hàng
                                    </a>
                                </div>
                            </div>
                        </div>

                        <div class="col-lg-9">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <div>
                                    <a href="${pageContext.request.contextPath}${backPath}"
                                        class="text-decoration-none small text-muted">Quay lại đơn hàng của tôi</a>
                                    <h2 class="fw-bold mt-1 mb-0">Chi tiết đơn hàng #${order.id}</h2>
                                </div>
                                <span class="badge ${order.statusCssClass}">${order.status}</span>
                            </div>

                            <c:if test="${not empty successMessage}">
                                <div class="alert alert-success">${successMessage}</div>
                            </c:if>

                            <div class="row g-4 mb-4">
                                <div class="col-lg-6">
                                    <div class="card border-0 shadow-sm h-100">
                                        <div class="card-header bg-white py-3">
                                            <h5 class="mb-0">Thông tin đơn hàng</h5>
                                        </div>
                                        <div class="card-body">
                                            <div class="row g-3">
                                                <div class="col-sm-6"><strong>Ngày tạo:</strong>
                                                    <div>${order.createdAtDisplay}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Số món:</strong>
                                                    <div>${order.itemCount}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Tạm tính:</strong>
                                                    <div>${order.subtotalAmountDisplay}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Giảm giá:</strong>
                                                    <div>${order.discountAmountDisplay}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Mã coupon:</strong>
                                                    <div>${order.couponCode}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Tổng tiền:</strong>
                                                    <div>${order.totalAmountDisplay}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Thanh toán:</strong>
                                                    <div>${order.paymentStatusDisplay}</div>
                                                </div>
                                                <div class="col-sm-6"><strong>Giao hàng:</strong>
                                                    <div>${order.shippingStatusDisplay}</div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-lg-6">
                                    <div class="card border-0 shadow-sm h-100">
                                        <div class="card-header bg-white py-3">
                                            <h5 class="mb-0">Sản phẩm</h5>
                                        </div>
                                        <div class="card-body">
                                            <c:choose>
                                                <c:when test="${not empty order.items}">
                                                    <div class="list-group list-group-flush">
                                                        <c:forEach var="item" items="${order.items}">
                                                            <div class="list-group-item px-0">
                                                                <div class="fw-semibold">${item.bookTitle}</div>
                                                                <div class="small text-muted">Item ID: ${item.id} - Số
                                                                    lượng: ${item.quantity}</div>
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

                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white py-3">
                                    <h5 class="mb-0">Thao tác</h5>
                                </div>
                                <div class="card-body">
                                    <c:choose>
                                        <c:when test="${order.canCancel}">
                                            <form method="post"
                                                action="${pageContext.request.contextPath}/my-orders/${order.id}/cancel">
                                                <input type="hidden" name="${_csrf.parameterName}"
                                                    value="${_csrf.token}" />
                                                <button class="btn btn-danger" type="submit">
                                                    <i class="fas fa-times me-2"></i>Hủy đơn hàng
                                                </button>
                                            </form>
                                            <div class="form-text mt-2">Bạn chỉ có thể hủy đơn khi đơn còn ở trạng thái
                                                PENDING.</div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="text-muted">Đơn hàng này hiện không thể hủy nữa.</div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
        </body>

        </html>