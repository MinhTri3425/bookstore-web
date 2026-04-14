<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

        <div class="container py-4">
            <div class="mb-4">
                <div class="text-muted small text-uppercase fw-bold">${pageEyebrow}</div>
                <h2 class="fw-bold text-dark mb-0">
                    <i class="fas fa-receipt me-2 text-primary"></i>${pageHeading}
                </h2>
            </div>

            <c:if test="${not empty successMessage}">
                <div class="alert alert-success border-0 shadow-sm mb-4">
                    <i class="fas fa-check-circle me-2"></i>${successMessage}
                </div>
            </c:if>

            <div class="card border-0 shadow-sm mb-4">
                <div class="card-body">
                    <form class="row g-3" method="get" action="${pageContext.request.contextPath}${listPath}">
                        <div class="col-md-5">
                            <label for="orderId" class="form-label fw-bold small text-muted">Mã đơn hàng</label>
                            <input id="orderId" name="orderId" type="text" class="form-control" value="${searchOrderId}"
                                placeholder="VD: 123...">
                        </div>
                        <div class="col-md-4">
                            <label for="status" class="form-label fw-bold small text-muted">Trạng thái</label>
                            <select id="status" name="status" class="form-select">
                                <option value="">Tất cả trạng thái</option>
                                <c:forEach var="statusOption" items="${statusOptions}">
                                    <option value="${statusOption}" ${selectedStatus==statusOption.toString()
                                        ? 'selected' : '' }>${statusOption}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="col-md-3 d-flex align-items-end gap-2">
                            <button class="btn btn-primary w-100" type="submit">
                                <i class="fas fa-filter me-1"></i> Lọc
                            </button>
                            <a class="btn btn-light border w-100"
                                href="${pageContext.request.contextPath}${listPath}">Reset</a>
                        </div>
                    </form>
                </div>
            </div>

            <div class="card border-0 shadow-sm text-dark">
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
                                <th>Khách hàng</th>
                                <th>Tổng tiền</th>
                                <th class="text-center">Thanh toán</th>
                                <th class="text-center">Giao hàng</th>
                                <th class="text-center">Hành động</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:choose>
                                <c:when test="${not empty orders}">
                                    <c:forEach var="order" items="${orders}">
                                        <tr>
                                            <td class="px-4 fw-bold">#${order.id}</td>
                                            <td>
                                                <span class="badge rounded-pill px-3 ${order.statusCssClass}">
                                                    ${order.status}
                                                </span>
                                            </td>
                                            <td class="small text-muted">${order.createdAtDisplay}</td>
                                            <td>${order.customerName}</td>
                                            <td class="fw-bold text-danger">${order.totalAmountDisplay}</td>
                                            <td class="text-center small">${order.paymentStatusDisplay}</td>
                                            <td class="text-center small">${order.shippingStatusDisplay}</td>
                                            <td class="text-center">
                                                <a class="btn btn-sm btn-outline-primary"
                                                    href="${pageContext.request.contextPath}/admin/orders/${order.id}">
                                                    <i class="fas fa-eye"></i> Chi tiết
                                                </a>
                                            </td>
                                        </tr>
                                    </c:forEach>
                                </c:when>
                                <c:otherwise>
                                    <tr>
                                        <td colspan="8" class="text-center py-5 text-muted">
                                            <i class="fas fa-inbox fa-3x mb-3 opacity-25"></i>
                                            <p>Không tìm thấy đơn hàng nào phù hợp.</p>
                                        </td>
                                    </tr>
                                </c:otherwise>
                            </c:choose>
                        </tbody>
                    </table>
                </div>

                <c:if test="${totalPages > 1}">
                    <div class="card-footer bg-white py-3 d-flex justify-content-between align-items-center">
                        <div class="text-muted small">
                            <c:set var="fromIndex" value="${totalElements == 0 ? 0 : (currentPage * pageSize) + 1}" />
                            <c:set var="toIndex"
                                value="${totalElements == 0 ? 0 : ((currentPage + 1) * pageSize < totalElements ? (currentPage + 1) * pageSize : totalElements)}" />
                            Hiển thị <strong>${fromIndex}-${toIndex}</strong> / ${totalElements} đơn hàng
                        </div>
                        <nav>
                            <ul class="pagination pagination-sm mb-0">
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
                                        <a class="page-link"
                                            href="${pageContext.request.contextPath}${pageUrl}">${pageIndex + 1}</a>
                                    </li>
                                </c:forEach>
                            </ul>
                        </nav>
                    </div>
                </c:if>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />