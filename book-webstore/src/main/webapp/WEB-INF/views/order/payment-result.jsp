<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Kết quả thanh toán VNPAY</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/views/fragments/header.jsp" %>

<div class="container py-5" style="max-width: 760px;">
    <div class="card shadow-sm border-0">
        <div class="card-body p-4 p-md-5">
            <div class="text-center mb-4">
                <div class="mb-3">
                    <c:choose>
                        <c:when test="${paymentSuccess}">
                            <i class="fas fa-circle-check text-success" style="font-size: 52px;"></i>
                        </c:when>
                        <c:otherwise>
                            <i class="fas fa-circle-xmark text-danger" style="font-size: 52px;"></i>
                        </c:otherwise>
                    </c:choose>
                </div>
                <h2 class="fw-bold mb-2">${paymentTitle}</h2>
                <p class="text-muted mb-0">${paymentMessage}</p>
            </div>

            <div class="border rounded p-3 mb-3">
                <div class="text-muted small mb-1">Đơn hàng</div>
                <div class="fw-semibold">#${orderId}</div>
            </div>

            <div class="border rounded p-3 mb-4">
                <h5 class="fw-bold mb-3">Sản phẩm trong đơn</h5>

                <c:choose>
                    <c:when test="${not empty paymentOrder and not empty paymentOrder.items}">
                        <c:forEach var="item" items="${paymentOrder.items}">
                            <div class="d-flex justify-content-between align-items-start border-bottom pb-2 mb-2">
                                <div class="pe-2">
                                    <div class="fw-semibold">${item.bookTitle}</div>
                                    <small class="text-muted">Số lượng: ${item.quantity}</small>
                                </div>
                                <div class="text-end text-danger fw-semibold">
                                    <c:choose>
                                        <c:when test="${item.book != null and item.book.price != null}">
                                            <fmt:formatNumber value="${item.book.price * item.quantity}" pattern="#,###"/> VND
                                        </c:when>
                                        <c:otherwise>-</c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>

                        <div class="d-flex justify-content-between pt-2">
                            <span class="fw-bold">Tổng tiền</span>
                            <span class="fw-bold text-danger">${paymentOrder.totalAmountDisplay}</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <p class="mb-0 text-muted">Chưa tải được chi tiết sản phẩm của đơn hàng này.</p>
                    </c:otherwise>
                </c:choose>
            </div>

            <div class="d-flex gap-2 justify-content-center flex-wrap">
                <a class="btn btn-primary" href="${pageContext.request.contextPath}/order/history">Xem đơn hàng của tôi</a>
                <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/books">Tiếp tục mua sắm</a>
            </div>
        </div>
    </div>
</div>

<%@ include file="/WEB-INF/views/fragments/footer.jsp" %>
</body>
</html>
