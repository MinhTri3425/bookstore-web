<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

            <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

            <div class="container py-4">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <div>
                        <div class="text-muted small text-uppercase fw-bold">Marketing & Khuyến mãi</div>
                        <h2 class="fw-bold text-dark mb-0">
                            <i class="fas fa-ticket-alt me-2 text-primary"></i>Quản lý Coupon
                        </h2>
                    </div>
                    <a href="${pageContext.request.contextPath}/admin/coupons/add"
                        class="btn btn-primary shadow-sm px-4">
                        <i class="fas fa-plus me-2"></i>Tạo coupon mới
                    </a>
                </div>

                <c:if test="${not empty successMessage}">
                    <div class="alert alert-success border-0 shadow-sm mb-4">
                        <i class="fas fa-check-circle me-2"></i>${successMessage}
                    </div>
                </c:if>
                <c:if test="${not empty errorMessage}">
                    <div class="alert alert-danger border-0 shadow-sm mb-4">
                        <i class="fas fa-exclamation-triangle me-2"></i>${errorMessage}
                    </div>
                </c:if>

                <div class="card border-0 shadow-sm text-dark">
                    <div class="card-header bg-white py-3 d-flex justify-content-between align-items-center">
                        <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách coupon hiện có</h5>
                        <div class="d-flex align-items-center">
                            <label for="sortCoupons" class="me-2 small text-muted text-nowrap"><i class="fas fa-sort me-1"></i>Sắp xếp:</label>
                            <select id="sortCoupons" class="form-select form-select-sm" style="width: auto;">
                                <option value="default">Mặc định</option>
                                <option value="value-asc">Giá trị giảm: Thấp đến Cao</option>
                                <option value="value-desc">Giá trị giảm: Cao đến Thấp</option>
                            </select>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th class="px-4">Mã Coupon</th>
                                    <th>Loại</th>
                                    <th>Target</th>
                                    <th id="sort-value-header" style="cursor: pointer; user-select: none;" title="Click để sắp xếp theo giá trị giảm">
                                        Giá trị giảm <i class="fas fa-sort text-muted ms-1" id="sort-value-icon"></i>
                                    </th>
                                    <th>Thời hạn hiệu lực</th>
                                    <th>Tình trạng sử dụng</th>
                                    <th>Trạng thái</th>
                                    <th class="text-center">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:choose>
                                    <c:when test="${not empty coupons}">
                                        <c:forEach var="coupon" items="${coupons}" varStatus="status">
                                            <c:set var="isExpired"
                                                value="${not empty coupon.endAt and coupon.endAt lt currentTime}" />
                                            <tr class="coupon-row" data-value="${coupon.value}" data-index="${status.index}">
                                                <td class="px-4">
                                                    <div class="fw-bold text-primary">${coupon.code}</div>
                                                    <div class="small text-muted">
                                                        <i class="fas fa-info-circle me-1"></i>
                                                        ${empty coupon.applicableBooks ? 'Toàn cửa hàng' : 'Sách chỉ
                                                        định'}
                                                    </div>
                                                </td>
                                                <td>
                                                    <span class="badge bg-light text-dark border">
                                                        ${coupon.type == 'PERCENTAGE' ? 'Phần trăm' : 'Số tiền cố định'}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span
                                                        class="badge ${coupon.target == 'SHIPPING' ? 'bg-info text-dark' : 'bg-primary'}">
                                                        ${coupon.target}
                                                    </span>
                                                </td>
                                                <td class="fw-bold">
                                                    <c:choose>
                                                        <c:when test="${coupon.type == 'PERCENTAGE'}">
                                                            <span class="text-success">-${coupon.value}%</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="text-success">-
                                                                <fmt:formatNumber value="${coupon.value}"
                                                                    pattern="#,###" /> ₫
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="small">
                                                    <div><i class="far fa-calendar-check me-1"></i>${coupon.startAt}
                                                    </div>
                                                    <div><i
                                                            class="far fa-calendar-times me-1 text-danger"></i>${coupon.endAt}
                                                    </div>
                                                </td>
                                                <td class="small">
                                                    <div>Đã dùng: <strong
                                                            class="text-dark">${coupon.currentUsageCount}</strong></div>
                                                    <div>Giới hạn/User: <strong>${coupon.maxUsePerUser}</strong></div>
                                                </td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${isExpired}">
                                                            <span class="badge rounded-pill bg-danger">
                                                                Không còn khả dụng
                                                            </span>
                                                        </c:when>
                                                        <c:when test="${coupon.active}">
                                                            <span class="badge rounded-pill bg-success">
                                                                Đang hoạt động
                                                            </span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge rounded-pill bg-secondary">
                                                                Đang tắt
                                                            </span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td class="text-center">
                                                    <div class="btn-group btn-group-sm shadow-sm">
                                                        <a href="${pageContext.request.contextPath}/admin/coupons/edit?id=${coupon.id}"
                                                            class="btn btn-white border" title="Chỉnh sửa">
                                                            <i class="fas fa-edit text-primary"></i>
                                                        </a>
                                                        <button type="button"
                                                            class="btn btn-white border btn-delete-coupon" title="Xóa"
                                                            data-coupon-id="${coupon.id}"
                                                            data-coupon-code="${coupon.code}" data-bs-toggle="modal"
                                                            data-bs-target="#deleteCouponModal">
                                                            <i class="fas fa-trash text-danger"></i>
                                                        </button>
                                                    </div>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </c:when>
                                    <c:otherwise>
                                        <tr>
                                            <td colspan="8" class="text-center py-5 text-muted">
                                                <i class="fas fa-ticket-alt fa-3x mb-3 opacity-25"></i>
                                                <p>Chưa có chương trình khuyến mãi nào được tạo.</p>
                                            </td>
                                        </tr>
                                    </c:otherwise>
                                </c:choose>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="modal fade" id="deleteCouponModal" tabindex="-1" aria-hidden="true">
                    <div class="modal-dialog modal-dialog-centered">
                        <div class="modal-content border-0 shadow">
                            <div class="modal-header">
                                <h5 class="modal-title"><i class="fas fa-trash-alt text-danger me-2"></i>Xác nhận xóa
                                    coupon</h5>
                                <button type="button" class="btn-close" data-bs-dismiss="modal"
                                    aria-label="Close"></button>
                            </div>
                            <div class="modal-body">
                                <p class="mb-0">Bạn có chắc chắn muốn xóa coupon <strong
                                        id="deleteCouponCode"></strong>?</p>
                                <div class="small text-muted mt-2">Hành động này không thể hoàn tác.</div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-outline-secondary"
                                    data-bs-dismiss="modal">Hủy</button>
                                <form method="post" action="${pageContext.request.contextPath}/admin/coupons/delete"
                                    class="d-inline" id="deleteCouponForm">
                                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                    <input type="hidden" name="id" id="deleteCouponId" />
                                    <button type="submit" class="btn btn-danger">
                                        <i class="fas fa-trash me-1"></i>Xóa coupon
                                    </button>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

            <script>
                (function () {
                    const deleteIdInput = document.getElementById('deleteCouponId');
                    const deleteCodeText = document.getElementById('deleteCouponCode');
                    document.querySelectorAll('.btn-delete-coupon').forEach(btn => {
                        btn.addEventListener('click', function () {
                            const couponId = this.getAttribute('data-coupon-id') || '';
                            const couponCode = this.getAttribute('data-coupon-code') || '';
                            deleteIdInput.value = couponId;
                            deleteCodeText.textContent = couponCode;
                        });
                    });

                    // Chức năng sắp xếp coupon theo giá trị giảm
                    const sortSelect = document.getElementById('sortCoupons');
                    const valueHeader = document.getElementById('sort-value-header');
                    const sortIcon = document.getElementById('sort-value-icon');
                    let currentSortOrder = 'none'; // 'none', 'asc', 'desc'

                    if (sortSelect) {
                        sortSelect.addEventListener('change', function () {
                            const sortBy = this.value;
                            const tbody = document.querySelector('table tbody');
                            if (!tbody) return;
                            const rows = Array.from(tbody.querySelectorAll('.coupon-row'));

                            if (sortBy === 'default') {
                                rows.sort((a, b) => parseInt(a.getAttribute('data-index')) - parseInt(b.getAttribute('data-index')));
                                if (sortIcon) sortIcon.className = 'fas fa-sort text-muted ms-1';
                                currentSortOrder = 'none';
                            } else if (sortBy === 'value-asc') {
                                rows.sort((a, b) => parseFloat(a.getAttribute('data-value')) - parseFloat(b.getAttribute('data-value')));
                                if (sortIcon) sortIcon.className = 'fas fa-sort-up ms-1 text-primary';
                                currentSortOrder = 'asc';
                            } else if (sortBy === 'value-desc') {
                                rows.sort((a, b) => parseFloat(b.getAttribute('data-value')) - parseFloat(a.getAttribute('data-value')));
                                if (sortIcon) sortIcon.className = 'fas fa-sort-down ms-1 text-primary';
                                currentSortOrder = 'desc';
                            }

                            rows.forEach(row => tbody.appendChild(row));
                        });
                    }

                    if (valueHeader && sortSelect) {
                        valueHeader.addEventListener('click', function () {
                            if (currentSortOrder === 'none' || currentSortOrder === 'desc') {
                                sortSelect.value = 'value-asc';
                            } else {
                                sortSelect.value = 'value-desc';
                            }
                            sortSelect.dispatchEvent(new Event('change'));
                        });
                    }
                })();
            </script>