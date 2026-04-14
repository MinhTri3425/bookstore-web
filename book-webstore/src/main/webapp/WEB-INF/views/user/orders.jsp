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
                                    <div class="text-muted small text-uppercase">${pageEyebrow}</div>
                                    <h2 class="fw-bold mb-0">Đơn hàng của tôi</h2>
                                </div>
                            </div>

                            <c:if test="${not empty successMessage}">
                                <div class="alert alert-success">${successMessage}</div>
                            </c:if>

                            <div class="card border-0 shadow-sm mb-4">
                                <div class="card-body">
                                    <form class="row g-3" method="get"
                                        action="${pageContext.request.contextPath}${listPath}">
                                        <div class="col-md-8">
                                            <label for="status" class="form-label fw-semibold">Lọc theo trạng
                                                thái</label>
                                            <select id="status" name="status" class="form-select">
                                                <option value="">Tất cả trạng thái</option>
                                                <c:forEach var="statusOption" items="${statusOptions}">
                                                    <option value="${statusOption}"
                                                        ${selectedStatus==statusOption.toString() ? 'selected' : '' }>
                                                        ${statusOption}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                        <div class="col-md-4 d-flex align-items-end gap-2">
                                            <button class="btn btn-primary w-100" type="submit">Lọc</button>
                                            <a class="btn btn-light border w-100"
                                                href="${pageContext.request.contextPath}${listPath}">Reset</a>
                                        </div>
                                    </form>
                                </div>
                            </div>

                            <div class="card border-0 shadow-sm">
                                <div class="card-header bg-white py-3">
                                    <h5 class="mb-0 fw-bold">Danh sách đơn hàng</h5>
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
                                                <th>Thanh toán</th>
                                                <th>Giao hàng</th>
                                                <th class="text-center">Thao tác</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:choose>
                                                <c:when test="${not empty orders}">
                                                    <c:forEach var="order" items="${orders}">
                                                        <tr>
                                                            <td class="px-4 fw-semibold">#${order.id}</td>
                                                            <td><span
                                                                    class="badge ${order.statusCssClass}">${order.status}</span>
                                                            </td>
                                                            <td>${order.createdAtDisplay}</td>
                                                            <td>${order.itemCount}</td>
                                                            <td>${order.totalAmountDisplay}</td>
                                                            <td>${order.paymentStatusDisplay}</td>
                                                            <td>${order.shippingStatusDisplay}</td>
                                                            <td class="text-center">
                                                                <div
                                                                    class="d-flex justify-content-center gap-2 flex-wrap">
                                                                    <a class="btn btn-outline-primary btn-sm"
                                                                        href="${pageContext.request.contextPath}/my-orders/${order.id}">
                                                                        <i class="fas fa-eye me-1"></i>Xem
                                                                    </a>
                                                                    <c:if test="${order.canCancel}">
                                                                        <button class="btn btn-outline-danger btn-sm"
                                                                            type="button" data-bs-toggle="modal"
                                                                            data-bs-target="#cancelOrderModal"
                                                                            data-order-id="${order.id}">
                                                                            <i class="fas fa-times me-1"></i>Hủy
                                                                        </button>
                                                                    </c:if>
                                                                    <c:if test="${order.status == 'CANCEL_REQUESTED'}">
                                                                        <span class="badge bg-warning text-dark">Chờ
                                                                            duyệt hủy</span>
                                                                    </c:if>
                                                                </div>
                                                            </td>
                                                        </tr>
                                                    </c:forEach>
                                                </c:when>
                                                <c:otherwise>
                                                    <tr>
                                                        <td colspan="8" class="text-center py-5 text-muted">Bạn chưa có
                                                            đơn hàng nào phù hợp.</td>
                                                    </tr>
                                                </c:otherwise>
                                            </c:choose>
                                        </tbody>
                                    </table>
                                </div>
                                <div
                                    class="card-body border-top d-flex justify-content-between align-items-center flex-wrap gap-3">
                                    <div class="text-muted small">
                                        <c:set var="fromIndex"
                                            value="${totalElements == 0 ? 0 : (currentPage * pageSize) + 1}" />
                                        <c:set var="toIndex"
                                            value="${totalElements == 0 ? 0 : ((currentPage + 1) * pageSize < totalElements ? (currentPage + 1) * pageSize : totalElements)}" />
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
                                                    </c:url>
                                                    <li class="page-item"><a class="page-link"
                                                            href="${pageContext.request.contextPath}${prevUrl}">Trước</a>
                                                    </li>
                                                </c:if>
                                                <c:forEach var="pageIndex" begin="0" end="${totalPages - 1}">
                                                    <c:url var="pageUrl" value="${listPath}">
                                                        <c:param name="page" value="${pageIndex}" />
                                                        <c:param name="size" value="${pageSize}" />
                                                        <c:if test="${not empty selectedStatus}">
                                                            <c:param name="status" value="${selectedStatus}" />
                                                        </c:if>
                                                    </c:url>
                                                    <li class="page-item ${pageIndex == currentPage ? 'active' : ''}">
                                                        <a class="page-link"
                                                            href="${pageContext.request.contextPath}${pageUrl}">${pageIndex
                                                            + 1}</a>
                                                    </li>
                                                </c:forEach>
                                                <c:if test="${currentPage + 1 < totalPages}">
                                                    <c:url var="nextUrl" value="${listPath}">
                                                        <c:param name="page" value="${currentPage + 1}" />
                                                        <c:param name="size" value="${pageSize}" />
                                                        <c:if test="${not empty selectedStatus}">
                                                            <c:param name="status" value="${selectedStatus}" />
                                                        </c:if>
                                                    </c:url>
                                                    <li class="page-item"><a class="page-link"
                                                            href="${pageContext.request.contextPath}${nextUrl}">Sau</a>
                                                    </li>
                                                </c:if>
                                            </ul>
                                        </nav>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="modal fade" id="cancelOrderModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow">
                            <div class="modal-header">
                                <h5 class="modal-title"><i class="fas fa-exclamation-triangle text-danger me-2"></i>Xác
                                    nhận hủy đơn</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                Bạn có chắc chắn muốn gửi yêu cầu hủy đơn hàng
                                <strong id="cancelOrderNumber"></strong>?
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-outline-secondary"
                                    data-bs-dismiss="modal">Đóng</button>
                                <form method="post" id="cancelOrderForm" class="d-inline">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <button class="btn btn-danger" type="submit">Xác nhận gửi yêu cầu</button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>

                <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>
                    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
                    <script>
                        (function () {
                            const cancelForm = document.getElementById('cancelOrderForm');
                            const cancelOrderNumber = document.getElementById('cancelOrderNumber');
                            if (!cancelForm || !cancelOrderNumber) {
                                return;
                            }

                            document.querySelectorAll('[data-order-id]').forEach(function (button) {
                                button.addEventListener('click', function () {
                                    const orderId = this.getAttribute('data-order-id') || '';
                                    cancelOrderNumber.textContent = '#' + orderId;
                                    cancelForm.action = '${pageContext.request.contextPath}/my-orders/' + orderId + '/cancel';
                                });
                            });
                        })();
                    </script>
        </body>

        </html>