<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

                <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

                <div class="container py-4">
                    <div class="d-flex justify-content-between align-items-center mb-4">
                        <div>
                            <div class="text-muted small text-uppercase fw-bold">Thiết lập khuyến mãi</div>
                            <h2 class="fw-bold text-dark mb-0">
                                <i class="fas fa-ticket-alt me-2 text-primary"></i>
                                ${coupon.id == null ? 'Tạo Coupon mới' : 'Cập nhật Coupon'}
                            </h2>
                        </div>
                        <a href="${pageContext.request.contextPath}/admin/coupons"
                            class="btn btn-outline-secondary px-4 shadow-sm">
                            <i class="fas fa-arrow-left me-2"></i>Quay lại danh sách
                        </a>
                    </div>

                    <c:if test="${not empty errorMessage}">
                        <div class="alert alert-danger border-0 shadow-sm mb-4">
                            <i class="fas fa-exclamation-triangle me-2"></i>${errorMessage}
                        </div>
                    </c:if>

                    <div class="card border-0 shadow-sm">
                        <div class="card-header bg-white py-3">
                            <h5 class="mb-0 fw-bold text-muted small text-uppercase">Thông tin chi tiết Coupon</h5>
                        </div>
                        <div class="card-body p-4">
                            <c:set var="couponFormAction" value="/admin/coupons/create" />
                            <c:if test="${coupon.id != null}">
                                <c:set var="couponFormAction" value="/admin/coupons/update" />
                            </c:if>
                            <form method="post" action="${pageContext.request.contextPath}${couponFormAction}"
                                class="row g-4" novalidate>
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <input type="hidden" name="id" value="${coupon.id}">
                                <c:set var="isEditMode" value="${coupon.id != null}" />

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted">Mã Coupon</label>
                                    <input type="text" class="form-control fw-bold text-primary" name="code"
                                        value="${coupon.code}" placeholder="Ví dụ: GIAMGIA2026" required>
                                    <div class="form-text">Mã định danh duy nhất để khách hàng nhập vào (có thể chỉnh
                                        khi
                                        cần).</div>
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted">Loại giảm giá</label>
                                    <select class="form-select" id="discountType" name="type" required>
                                        <option value="PERCENTAGE" ${coupon.type=='PERCENTAGE' ? 'selected' : '' }>Phần
                                            trăm
                                            (%)</option>
                                        <option value="FIXED" ${coupon.type=='FIXED' ? 'selected' : '' }>Số tiền cố định
                                            (VNĐ)</option>
                                    </select>
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted">Target coupon</label>
                                    <select class="form-select" id="couponTarget" name="target" required>
                                        <option value="PRODUCT" ${coupon.target=='PRODUCT' ? 'selected' : '' }>PRODUCT
                                            (giảm
                                            trên sản phẩm)</option>
                                        <option value="SHIPPING" ${coupon.target=='SHIPPING' ? 'selected' : '' }>
                                            SHIPPING
                                            (giảm phí vận chuyển)</option>
                                    </select>
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted">Giá trị giảm</label>
                                    <div class="input-group">
                                        <input type="number" id="discountValue" step="0.01" min="1" class="form-control"
                                            name="value" value="${coupon.value}" required>
                                        <span class="input-group-text bg-light fw-bold" id="valueUnitText">
                                            ${coupon.type == 'FIXED' ? '₫' : '%'}
                                        </span>
                                    </div>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold small text-muted">Mỗi khách dùng tối đa</label>
                                    <input type="number" min="1" class="form-control" name="maxUsePerUser"
                                        value="${coupon.maxUsePerUser > 0 ? coupon.maxUsePerUser : 1}" required>
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold small text-muted">Tổng lượt dùng tối đa</label>
                                    <input type="number" min="1" class="form-control" name="totalUsageLimit"
                                        value="${coupon.totalUsageLimit}" placeholder="Không giới hạn">
                                </div>

                                <div class="col-md-3">
                                    <label class="form-label fw-bold small text-muted">Đơn hàng tối thiểu (₫)</label>
                                    <input type="number" step="0.01" min="0" class="form-control" name="minOrderValue"
                                        value="${coupon.minOrderValue}">
                                </div>

                                <div class="col-md-3" id="maxDiscountWrap">
                                    <label class="form-label fw-bold small text-muted">Số tiền giảm tối đa (₫)</label>
                                    <input type="number" step="0.01" min="0" class="form-control"
                                        name="maxDiscountValue" value="${coupon.maxDiscountValue}">
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted">Ngày bắt đầu</label>
                                    <input type="datetime-local" class="form-control" name="startAt"
                                        value="${not empty coupon.startAt ? fn:substring(coupon.startAt, 0, 16) : ''}">
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label fw-bold small text-muted">Ngày kết thúc</label>
                                    <input type="datetime-local" class="form-control" name="endAt"
                                        value="${not empty coupon.endAt ? fn:substring(coupon.endAt, 0, 16) : ''}">
                                </div>

                                <div class="col-md-4 d-flex align-items-end px-4">
                                    <div class="form-check form-switch p-3 bg-light rounded w-100 border">
                                        <input class="form-check-input ms-0 me-2" type="checkbox" id="active"
                                            name="active" value="true" ${coupon.active ? 'checked' : '' }>
                                        <label class="form-check-label fw-bold text-success" for="active">Kích hoạt
                                            Coupon
                                            ngay</label>
                                    </div>
                                </div>

                                <div class="col-12" id="applicableBooksWrap">
                                    <label class="form-label fw-bold small text-muted">Sách được áp dụng (Chọn
                                        nhiều)</label>
                                    <div class="border rounded p-3 bg-light-subtle">
                                        <div class="form-check mb-2 border-bottom pb-2">
                                            <input class="form-check-input" type="checkbox"
                                                id="selectAllApplicableBooks">
                                            <label class="form-check-label fw-bold" for="selectAllApplicableBooks">Chọn
                                                tất
                                                cả sách</label>
                                        </div>

                                        <div class="overflow-auto" style="max-height: 260px;">
                                            <c:forEach var="book" items="${books}">
                                                <c:set var="isSelected" value="false" />
                                                <c:forEach var="selectedId" items="${coupon.applicableBookIds}">
                                                    <c:if test="${selectedId == book.id}">
                                                        <c:set var="isSelected" value="true" />
                                                    </c:if>
                                                </c:forEach>
                                                <div class="form-check mb-2">
                                                    <input class="form-check-input applicable-book-checkbox"
                                                        type="checkbox" id="book-${book.id}" name="applicableBookIds"
                                                        value="${book.id}" ${isSelected ? 'checked' : '' }>
                                                    <label class="form-check-label" for="book-${book.id}">${book.title}
                                                        (ID:
                                                        ${book.id})</label>
                                                </div>
                                            </c:forEach>
                                        </div>
                                    </div>
                                    <div class="form-text text-info"><i class="fas fa-info-circle me-1"></i> Để trống
                                        nếu áp
                                        dụng cho toàn bộ cửa hàng.</div>
                                </div>

                                <div class="col-12 d-flex gap-2 pt-3 border-top mt-4">
                                    <button type="submit" class="btn btn-primary px-5 py-2 shadow">
                                        <i class="fas fa-save me-2"></i>Lưu cấu hình
                                    </button>
                                    <a href="${pageContext.request.contextPath}/admin/coupons"
                                        class="btn btn-light border px-4 py-2">Hủy bỏ</a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />

                <script>
                    (function () {
                        const discountType = document.getElementById('discountType');
                        const couponTarget = document.getElementById('couponTarget');
                        const discountValue = document.getElementById('discountValue');
                        const unitText = document.getElementById('valueUnitText');
                        const maxDiscountWrap = document.getElementById('maxDiscountWrap');
                        const applicableBooksWrap = document.getElementById('applicableBooksWrap');
                        const selectAllApplicableBooks = document.getElementById('selectAllApplicableBooks');
                        const applicableBookCheckboxes = Array.from(document.querySelectorAll('.applicable-book-checkbox'));

                        function syncSelectAllState() {
                            if (!selectAllApplicableBooks || applicableBookCheckboxes.length === 0) {
                                return;
                            }

                            const checkedCount = applicableBookCheckboxes.filter(item => item.checked).length;
                            selectAllApplicableBooks.checked = checkedCount > 0 && checkedCount === applicableBookCheckboxes.length;
                            selectAllApplicableBooks.indeterminate = checkedCount > 0 && checkedCount < applicableBookCheckboxes.length;
                        }

                        function applyFormRules() {
                            const isPercentage = discountType.value === 'PERCENTAGE';
                            const isShippingTarget = couponTarget.value === 'SHIPPING';

                            unitText.textContent = isPercentage ? '%' : '₫';
                            discountValue.step = '0.01';

                            if (isPercentage) {
                                discountValue.max = '100';
                            } else {
                                discountValue.removeAttribute('max');
                            }

                            maxDiscountWrap.style.display = isPercentage ? '' : 'none';

                            applicableBooksWrap.style.display = isShippingTarget ? 'none' : '';
                            if (isShippingTarget) {
                                applicableBookCheckboxes.forEach(item => {
                                    item.checked = false;
                                });
                                if (selectAllApplicableBooks) {
                                    selectAllApplicableBooks.checked = false;
                                    selectAllApplicableBooks.indeterminate = false;
                                }
                            }

                            syncSelectAllState();
                        }

                        discountType.addEventListener('change', applyFormRules);
                        couponTarget.addEventListener('change', applyFormRules);

                        if (selectAllApplicableBooks) {
                            selectAllApplicableBooks.addEventListener('change', function () {
                                const checked = this.checked;
                                applicableBookCheckboxes.forEach(item => {
                                    item.checked = checked;
                                });
                                syncSelectAllState();
                            });
                        }

                        applicableBookCheckboxes.forEach(item => {
                            item.addEventListener('change', syncSelectAllState);
                        });

                        applyFormRules();
                    })();
                </script>