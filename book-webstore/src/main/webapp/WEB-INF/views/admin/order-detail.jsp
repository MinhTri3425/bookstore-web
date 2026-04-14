<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

            <div class="container py-4 text-dark">
                <%-- Thanh điều hướng và tiêu đề --%>
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <a href="${pageContext.request.contextPath}/admin/orders"
                                class="text-decoration-none small text-muted">
                                <i class="fas fa-arrow-left me-1"></i> Quay lại danh sách
                            </a>
                            <h2 class="fw-bold mb-0 mt-1">Đơn hàng #${order.id}</h2>
                        </div>
                        <div class="text-end">
                            <span class="badge rounded-pill px-3 py-2 fs-6 ${order.statusCssClass}">
                                ${order.status}
                            </span>
                            <div class="small text-muted mt-1">Ngày tạo: ${order.createdAtDisplay}</div>
                        </div>
                    </div>

                    <%-- Hiển thị thông báo lỗi/thành công --%>
                        <c:if test="${not empty successMessage}">
                            <div class="alert alert-success alert-dismissible fade show mb-4 border-0 shadow-sm"
                                role="alert">
                                <i class="fas fa-check-circle me-2"></i> ${successMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                            </div>
                        </c:if>
                        <c:if test="${not empty errorMessage}">
                            <div class="alert alert-danger alert-dismissible fade show mb-4 border-0 shadow-sm"
                                role="alert">
                                <i class="fas fa-exclamation-triangle me-2"></i> ${errorMessage}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                            </div>
                        </c:if>

                        <div class="row g-4">
                            <%-- CỘT TRÁI: THÔNG TIN CHI TIẾT --%>
                                <div class="col-lg-8">
                                    <%-- 1. Thông tin người nhận --%>
                                        <div class="card border-0 shadow-sm mb-4">
                                            <div class="card-header bg-white py-3">
                                                <h5 class="mb-0 fw-bold"><i
                                                        class="fas fa-user-circle me-2 text-primary"></i>Thông tin người
                                                    nhận</h5>
                                            </div>
                                            <div class="card-body">
                                                <div class="row g-3">
                                                    <div class="col-md-6">
                                                        <label class="text-muted small d-block">Khách hàng đặt</label>
                                                        <span class="fw-bold">${order.customerName}</span>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label class="text-muted small d-block">Người nhận hàng</label>
                                                        <span class="fw-bold text-primary">${order.receiverName}</span>
                                                    </div>
                                                    <div class="col-md-6">
                                                        <label class="text-muted small d-block">Số điện thoại</label>
                                                        <span class="fw-bold">${order.phoneNumber}</span>
                                                    </div>
                                                    <div class="col-12">
                                                        <label class="text-muted small d-block">Địa chỉ giao
                                                            hàng</label>
                                                        <span class="fw-bold">${order.address}</span>
                                                    </div>
                                                    <c:if test="${not empty order.note}">
                                                        <div class="col-12">
                                                            <label class="text-muted small d-block">Ghi chú từ
                                                                khách</label>
                                                            <div
                                                                class="p-2 bg-light rounded italic border-start border-primary border-3">
                                                                "${order.note}"</div>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </div>

                                        <%-- 2. Danh sách sản phẩm --%>
                                            <div class="card border-0 shadow-sm mb-4">
                                                <div class="card-header bg-white py-3">
                                                    <h5 class="mb-0 fw-bold"><i
                                                            class="fas fa-shopping-basket me-2 text-success"></i>Sản
                                                        phẩm đơn hàng</h5>
                                                </div>
                                                <div class="table-responsive">
                                                    <table class="table align-middle mb-0">
                                                        <thead class="table-light">
                                                            <tr>
                                                                <th class="ps-4">Tên sách</th>
                                                                <th class="text-center">Số lượng</th>
                                                                <th class="text-end pe-4">Đơn giá</th>
                                                            </tr>
                                                        </thead>
                                                        <tbody>
                                                            <c:forEach var="item" items="${order.items}">
                                                                <tr>
                                                                    <td class="ps-4 fw-bold">${item.bookTitle}</td>
                                                                    <td class="text-center">${item.quantity}</td>
                                                                    <td class="text-end pe-4">
                                                                        <fmt:formatNumber value="${item.book.price}"
                                                                            pattern="#,###" /> ₫
                                                                    </td>
                                                                </tr>
                                                            </c:forEach>
                                                        </tbody>
                                                    </table>
                                                </div>
                                                <div class="card-footer bg-white py-3 text-dark">
                                                    <div class="d-flex justify-content-between mb-1">
                                                        <span class="text-muted">Tạm tính:</span>
                                                        <span>${order.subtotalAmountDisplay}</span>
                                                    </div>
                                                    <c:if test="${not empty order.productCouponCode}">
                                                        <div class="d-flex justify-content-between mb-1 text-success">
                                                            <span>Coupon sản phẩm (${order.productCouponCode}):</span>
                                                            <span>-${order.productDiscountAmountDisplay}</span>
                                                        </div>
                                                    </c:if>
                                                    <c:if test="${not empty order.shippingCouponCode}">
                                                        <div class="d-flex justify-content-between mb-1 text-success">
                                                            <span>Coupon vận chuyển
                                                                (${order.shippingCouponCode}):</span>
                                                            <span>-${order.shippingDiscountAmountDisplay}</span>
                                                        </div>
                                                    </c:if>
                                                    <div
                                                        class="d-flex justify-content-between mt-2 pt-2 border-top fw-bold fs-5">
                                                        <span>Tổng thanh toán:</span>
                                                        <span class="text-danger">${order.totalAmountDisplay}</span>
                                                    </div>
                                                </div>
                                            </div>
                                </div>

                                <%-- CỘT PHẢI: TRẠNG THÁI & THAO TÁC --%>
                                    <div class="col-lg-4">
                                        <%-- 3. Trạng thái vận chuyển & Shipper --%>
                                            <div class="card border-0 shadow-sm mb-4">
                                                <div class="card-header bg-white py-3">
                                                    <h6 class="mb-0 fw-bold"><i
                                                            class="fas fa-truck me-2 text-info"></i>Vận chuyển</h6>
                                                </div>
                                                <div class="card-body">
                                                    <div class="mb-3">
                                                        <label class="small text-muted d-block">Phương thức</label>
                                                        <span
                                                            class="fw-bold text-uppercase">${order.shippingMethod}</span>
                                                    </div>
                                                    <div class="mb-3">
                                                        <label class="small text-muted d-block">Trạng thái vận
                                                            đơn</label>
                                                        <span class="badge bg-info text-white p-2 mt-1">
                                                            <i class="fas fa-box me-1"></i>
                                                            ${order.shippingStatusDisplay}
                                                        </span>
                                                    </div>

                                                    <%-- Thông tin Shipper Snapshot --%>
                                                        <div class="p-3 bg-light rounded border">
                                                            <label
                                                                class="small text-muted fw-bold d-block mb-2 text-dark">Shipper
                                                                phụ trách</label>
                                                            <c:choose>
                                                                <c:when
                                                                    test="${not empty order.shipperName and order.shipperName != 'N/A'}">
                                                                    <div class="d-flex align-items-center">
                                                                        <div
                                                                            class="bg-primary text-white rounded-circle p-2 me-2">
                                                                            <i class="fas fa-user-check"></i>
                                                                        </div>
                                                                        <span
                                                                            class="fw-bold text-dark">${order.shipperName}</span>
                                                                    </div>
                                                                </c:when>
                                                                <c:otherwise>
                                                                    <span class="text-warning italic small">
                                                                        <i class="fas fa-hourglass-half me-1"></i> Đang
                                                                        chờ điều phối...
                                                                    </span>
                                                                </c:otherwise>
                                                            </c:choose>
                                                        </div>

                                                        <%-- Nút tìm lại Shipper nếu bị kẹt --%>
                                                            <c:if
                                                                test="${order.status == 'CONFIRMED' and (empty order.shipperName or order.shipperName == 'N/A')}">
                                                                <form
                                                                    action="${pageContext.request.contextPath}/admin/shipping/${order.id}/re-dispatch"
                                                                    method="post" class="mt-3">
                                                                    <input type="hidden" name="${_csrf.parameterName}"
                                                                        value="${_csrf.token}" />
                                                                    <button type="submit"
                                                                        class="btn btn-outline-warning btn-sm w-100 fw-bold">
                                                                        <i class="fas fa-redo me-1"></i> Thử tìm lại
                                                                        Shipper
                                                                    </button>
                                                                </form>
                                                            </c:if>
                                                </div>
                                            </div>

                                            <%-- 4. Thao tác phê duyệt đơn --%>
                                                <div class="card border-0 shadow-sm text-dark">
                                                    <div class="card-body">
                                                        <h6 class="fw-bold mb-3 border-bottom pb-2">Hành động Admin</h6>

                                                        <c:choose>
                                                            <%-- CHỈ HIỂN THỊ NÚT XÁC NHẬN KHI ĐƠN LÀ PENDING --%>
                                                                <c:when test="${order.status == 'PENDING'}">
                                                                    <form method="post"
                                                                        action="${pageContext.request.contextPath}/admin/orders/${order.id}/status">
                                                                        <input type="hidden"
                                                                            name="${_csrf.parameterName}"
                                                                            value="${_csrf.token}" />
                                                                        <input type="hidden" name="status"
                                                                            value="CONFIRMED" />

                                                                        <p class="small text-muted mb-3 italic">
                                                                            Xác nhận đơn hàng để hệ thống tự động trừ
                                                                            kho và gán Shipper giao hàng.
                                                                        </p>

                                                                        <button
                                                                            class="btn btn-primary w-100 fw-bold py-2 mb-2"
                                                                            type="submit">
                                                                            <i class="fas fa-check-circle me-1"></i> XÁC
                                                                            NHẬN ĐƠN HÀNG
                                                                        </button>
                                                                    </form>

                                                                    <form method="post"
                                                                        action="${pageContext.request.contextPath}/admin/orders/${order.id}/status">
                                                                        <input type="hidden"
                                                                            name="${_csrf.parameterName}"
                                                                            value="${_csrf.token}" />
                                                                        <input type="hidden" name="status"
                                                                            value="CANCELLED" />
                                                                        <button
                                                                            class="btn btn-outline-danger w-100 btn-sm"
                                                                            type="submit"
                                                                            onclick="return confirm('Bạn có chắc chắn muốn hủy đơn hàng này?')">
                                                                            Hủy đơn
                                                                        </button>
                                                                    </form>
                                                                </c:when>

                                                                <%-- TRẠNG THÁI ĐÃ XỬ LÝ --%>
                                                                    <c:when test="${order.status == 'CONFIRMED'}">
                                                                        <div
                                                                            class="alert alert-success border-0 bg-success bg-opacity-10 text-success small mb-0">
                                                                            <i class="fas fa-info-circle me-1"></i> Đơn
                                                                            hàng đang trong quá trình vận chuyển. Không
                                                                            thể thay đổi.
                                                                        </div>
                                                                    </c:when>

                                                                    <c:otherwise>
                                                                        <div
                                                                            class="alert alert-secondary border-0 small mb-0 text-center">
                                                                            Đơn hàng đã đóng kết thúc.
                                                                        </div>
                                                                    </c:otherwise>
                                                        </c:choose>

                                                        <hr>
                                                        <div class="d-flex justify-content-between align-items-center">
                                                            <span class="small text-muted italic">Thanh toán:</span>
                                                            <span
                                                                class="badge ${order.paymentStatusDisplay == 'PAID' ? 'bg-success' : 'bg-warning text-dark'}">
                                                                ${order.paymentStatusDisplay}
                                                            </span>
                                                        </div>
                                                    </div>
                                                </div>
                                    </div>
                        </div>
            </div>

            <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />