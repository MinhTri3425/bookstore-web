<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
        <%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
            <!DOCTYPE html>
            <html lang="vi">

            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Thanh toán</title>
                <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
                <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
                <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
            </head>

            <body>
                <%@ include file="/WEB-INF/views/fragments/header.jsp" %>

                    <div class="container py-5">
                        <div class="d-flex justify-content-between align-items-center mb-4">
                            <div>
                                <h2 class="fw-bold mb-1">Thông tin đặt hàng</h2>
                                <p class="text-muted mb-0">Điền thông tin giao hàng và chọn phương thức thanh toán</p>
                            </div>
                            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary">Quay lại
                                giỏ hàng</a>
                        </div>

                        <c:if test="${not empty message}">
                            <div class="alert alert-info alert-dismissible fade show" role="alert">
                                ${message}
                                <button type="button" class="btn-close" data-bs-dismiss="alert"
                                    aria-label="Close"></button>
                            </div>
                        </c:if>

                        <form method="post" action="${pageContext.request.contextPath}/checkout/place"
                            id="placeOrderForm">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <c:forEach var="bookId" items="${selectedBookIds}">
                                <input type="hidden" name="selectedBookIds" value="${bookId}" />
                            </c:forEach>

                            <div class="row g-4">
                                <div class="col-lg-7">
                                    <div class="card shadow-sm border-0">
                                        <div class="card-body p-4">
                                            <h5 class="fw-bold mb-3">Địa chỉ giao hàng</h5>

                                            <div class="mb-3">
                                                <div class="d-flex justify-content-between align-items-center mb-2">
                                                    <label for="selectedAddressId" class="form-label mb-0">Chọn địa chỉ
                                                        đã lưu</label>
                                                    <button type="button" class="btn btn-outline-primary btn-sm"
                                                        data-bs-toggle="modal" data-bs-target="#newAddressModal">
                                                        <i class="fas fa-plus"></i> Thêm địa chỉ mới
                                                    </button>
                                                </div>
                                                <select class="form-select" id="selectedAddressId"
                                                    name="selectedAddressId" required>
                                                    <option value="">-- Vui lòng chọn địa chỉ đã lưu --</option>
                                                    <c:forEach var="addr" items="${userAddresses}">
                                                        <option value="${addr.id}" data-street="${addr.street}"
                                                            data-ward="${addr.ward}" data-district="${addr.district}"
                                                            data-city="${addr.city}" ${selectedAddressId !=null and
                                                            selectedAddressId==addr.id ? 'selected' : '' }>
                                                            ${addr.street}, ${addr.ward}, ${addr.district}, ${addr.city}
                                                        </option>
                                                    </c:forEach>
                                                </select>
                                                <div class="form-text">Bạn chỉ có thể đặt hàng bằng địa chỉ đã lưu.
                                                </div>
                                            </div>

                                            <div class="row g-3">
                                                <div class="col-12">
                                                    <h6 class="mb-1">Thông tin địa chỉ đã chọn</h6>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="street" class="form-label">Số nhà, tên đường</label>
                                                    <input type="text" class="form-control bg-light" id="street"
                                                        placeholder="Chọn địa chỉ ở trên" readonly>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="ward" class="form-label">Phường/Xã</label>
                                                    <input type="text" class="form-control bg-light" id="ward"
                                                        placeholder="Chọn địa chỉ ở trên" readonly>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="district" class="form-label">Quận/Huyện</label>
                                                    <input type="text" class="form-control bg-light" id="district"
                                                        placeholder="Chọn địa chỉ ở trên" readonly>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="city" class="form-label">Tỉnh/Thành phố</label>
                                                    <input type="text" class="form-control bg-light" id="city"
                                                        placeholder="Chọn địa chỉ ở trên" readonly>
                                                </div>
                                            </div>

                                            <hr class="my-4">

                                            <h5 class="fw-bold mb-3">Thông tin nhận hàng</h5>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="receiverName" class="form-label">Tên người nhận</label>
                                                    <input type="text" class="form-control" id="receiverName"
                                                        name="receiverName" value="${receiverName}"
                                                        placeholder="Nhập tên người nhận" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="phoneNumber" class="form-label">Số điện thoại</label>
                                                    <input type="text" class="form-control" id="phoneNumber"
                                                        name="phoneNumber" value="${phoneNumber}"
                                                        placeholder="Ví dụ: 0912345678" required>
                                                </div>
                                                <div class="col-12">
                                                    <label for="note" class="form-label">Ghi chú</label>
                                                    <textarea class="form-control" id="note" name="note" rows="3"
                                                        placeholder="Ví dụ: Giao giờ hành chính..."></textarea>
                                                </div>
                                            </div>

                                            <hr class="my-4">

                                            <h5 class="fw-bold mb-3">Phương thức thanh toán</h5>
                                            <select class="form-select" name="paymentMethod" id="paymentMethod"
                                                required>
                                                <c:forEach var="method" items="${paymentMethods}">
                                                    <option value="${method}">${method}</option>
                                                </c:forEach>
                                            </select>

                                            <hr class="my-4">

                                            <h5 class="fw-bold mb-3">Vận chuyển và ưu đãi</h5>
                                            <div class="row g-3">
                                                <div class="col-12">
                                                    <label for="shippingMethod" class="form-label">Phương thức giao
                                                        hàng</label>
                                                    <select class="form-select" id="shippingMethod"
                                                        name="shippingMethod">
                                                        <option value="STANDARD" ${selectedShippingMethod=='STANDARD'
                                                            ? 'selected' : '' }>Giao tiêu chuẩn (2-4 ngày)</option>
                                                        <option value="FAST" ${selectedShippingMethod=='FAST'
                                                            ? 'selected' : '' }>Giao nhanh (1-2 ngày)</option>
                                                        <option value="ECONOMY" ${selectedShippingMethod=='ECONOMY'
                                                            ? 'selected' : '' }>Giao tiết kiệm (3-6 ngày)</option>
                                                    </select>
                                                </div>

                                                <div class="col-12">
                                                    <label class="form-label" for="productCouponCode">Coupon sản
                                                        phẩm</label>
                                                    <select class="form-select" id="productCouponCode"
                                                        name="productCouponCode">
                                                        <option value="">-- Không áp dụng coupon sản phẩm --</option>
                                                        <c:forEach var="coupon" items="${productCoupons}">
                                                            <option value="${coupon.code}"
                                                                ${selectedProductCouponCode==coupon.code ? 'selected'
                                                                : '' } ${coupon.eligible ? '' : 'disabled' }>
                                                                ${coupon.code} - ${coupon.valueDisplay}${coupon.eligible
                                                                ? '' : ' (Không khả dụng)'}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                    <div class="small text-muted mt-1" id="selectedProductCouponText">
                                                        <c:choose>
                                                            <c:when test="${not empty selectedProductCouponCode}">
                                                                Đã chọn: <span
                                                                    class="fw-bold text-success">${selectedProductCouponCode}</span>
                                                            </c:when>
                                                            <c:otherwise>Chưa chọn coupon sản phẩm.</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>

                                                <div class="col-12">
                                                    <label class="form-label" for="shippingCouponCode">Coupon vận
                                                        chuyển</label>
                                                    <select class="form-select" id="shippingCouponCode"
                                                        name="shippingCouponCode">
                                                        <option value="">-- Không áp dụng coupon vận chuyển --</option>
                                                        <c:forEach var="coupon" items="${shippingCoupons}">
                                                            <option value="${coupon.code}"
                                                                ${selectedShippingCouponCode==coupon.code ? 'selected'
                                                                : '' } ${coupon.eligible ? '' : 'disabled' }>
                                                                ${coupon.code} - ${coupon.valueDisplay}${coupon.eligible
                                                                ? '' : ' (Không khả dụng)'}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                    <div class="small text-muted mt-1" id="selectedShippingCouponText">
                                                        <c:choose>
                                                            <c:when test="${not empty selectedShippingCouponCode}">
                                                                Đã chọn: <span
                                                                    class="fw-bold text-success">${selectedShippingCouponCode}</span>
                                                            </c:when>
                                                            <c:otherwise>Chưa chọn coupon vận chuyển.</c:otherwise>
                                                        </c:choose>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>

                                <div class="col-lg-5">
                                    <div class="card shadow-sm border-0 sticky-top" style="top: 2rem;">
                                        <div class="card-body p-4">
                                            <h5 class="fw-bold mb-3">Mặt hàng đã chọn</h5>
                                            <c:forEach var="item" items="${selectedItems}">
                                                <div
                                                    class="d-flex justify-content-between align-items-start border-bottom pb-2 mb-2">
                                                    <div class="pe-2">
                                                        <div class="fw-semibold">${item.book.title}</div>
                                                        <small class="text-muted">SL: ${item.quantity}</small>
                                                    </div>
                                                    <div class="text-end text-danger fw-semibold">
                                                        <fmt:formatNumber value="${item.book.price * item.quantity}"
                                                            pattern="#,###" /> VND
                                                    </div>
                                                </div>
                                            </c:forEach>

                                            <div class="mt-3">
                                                <div class="d-flex justify-content-between mb-1">
                                                    <span>Tạm tính:</span>
                                                    <span id="displaySubtotal">
                                                        <fmt:formatNumber value="${selectedTotal}" pattern="#,###" />
                                                        VND
                                                    </span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-1">
                                                    <span>Phí vận chuyển:</span>
                                                    <span id="displayShipping">0 VND</span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-1 text-success">
                                                    <span>Giảm sản phẩm:</span>
                                                    <span id="displayProductDiscount">0 VND</span>
                                                </div>
                                                <div class="d-flex justify-content-between mb-1 text-success">
                                                    <span>Giảm vận chuyển:</span>
                                                    <span id="displayShippingDiscount">0 VND</span>
                                                </div>
                                                <div class="d-flex justify-content-between mt-2 pt-2 border-top">
                                                    <span class="fw-bold">Tổng thanh toán</span>
                                                    <span class="fw-bold text-danger" id="displayFinalTotal">
                                                        <fmt:formatNumber value="${selectedTotal}" pattern="#,###" />
                                                        VND
                                                    </span>
                                                </div>
                                            </div>

                                            <button type="submit" class="btn btn-success w-100 mt-4 py-2 fw-bold">Xác
                                                nhận đặt hàng</button>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </form>

                        <div class="modal fade" id="newAddressModal" tabindex="-1"
                            aria-labelledby="newAddressModalLabel" aria-hidden="true">
                            <div class="modal-dialog modal-lg modal-dialog-centered">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h5 class="modal-title" id="newAddressModalLabel">Thêm địa chỉ mới</h5>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <form method="post" action="${pageContext.request.contextPath}/checkout/address/add"
                                        id="addAddressForm">
                                        <div class="modal-body">
                                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                            <c:forEach var="bookId" items="${selectedBookIds}">
                                                <input type="hidden" name="selectedBookIds" value="${bookId}" />
                                            </c:forEach>
                                            <div class="row g-3">
                                                <div class="col-md-6">
                                                    <label for="newStreet" class="form-label">Số nhà, tên đường</label>
                                                    <input type="text" class="form-control" id="newStreet" name="street"
                                                        required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="newWard" class="form-label">Phường/Xã</label>
                                                    <input type="text" class="form-control" id="newWard" name="ward"
                                                        required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="newDistrict" class="form-label">Quận/Huyện</label>
                                                    <input type="text" class="form-control" id="newDistrict"
                                                        name="district" required>
                                                </div>
                                                <div class="col-md-6">
                                                    <label for="newCity" class="form-label">Tỉnh/Thành phố</label>
                                                    <input type="text" class="form-control" id="newCity" name="city"
                                                        required>
                                                </div>
                                            </div>
                                        </div>
                                        <div class="modal-footer">
                                            <button type="button" class="btn btn-outline-secondary"
                                                data-bs-dismiss="modal">Đóng</button>
                                            <button type="submit" class="btn btn-primary">Lưu địa chỉ</button>
                                        </div>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>

                    <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>

                        <script>
                            (function () {
                                const form = document.getElementById('placeOrderForm');
                                const shippingSelect = document.getElementById('shippingMethod');
                                const productCouponInput = document.getElementById('productCouponCode');
                                const shippingCouponInput = document.getElementById('shippingCouponCode');
                                const selectedProductCouponText = document.getElementById('selectedProductCouponText');
                                const selectedShippingCouponText = document.getElementById('selectedShippingCouponText');

                                const displayShipping = document.getElementById('displayShipping');
                                const displayProductDiscount = document.getElementById('displayProductDiscount');
                                const displayShippingDiscount = document.getElementById('displayShippingDiscount');
                                const displayFinalTotal = document.getElementById('displayFinalTotal');

                                // Hàm định dạng tiền tệ
                                function formatCurrency(value) {
                                    return new Intl.NumberFormat('vi-VN').format(value) + ' VND';
                                }

                                // Hàm AJAX cập nhật giá từ Strategy Backend
                                function updateOrderPreview() {
                                    const method = shippingSelect.value;
                                    const productCoupon = productCouponInput.value.trim();
                                    const shippingCoupon = shippingCouponInput.value.trim();
                                    const bookIds = Array.from(document.querySelectorAll('input[name="selectedBookIds"]')).map(i => i.value);

                                    const url = `${pageContext.request.contextPath}/api/checkout/preview?shippingMethod=` + method +
                                        `&productCouponCode=` + encodeURIComponent(productCoupon) +
                                        `&shippingCouponCode=` + encodeURIComponent(shippingCoupon) +
                                        `&bookIds=` + bookIds.join(',');

                                    fetch(url)
                                        .then(response => response.json())
                                        .then(data => {
                                            displayShipping.textContent = formatCurrency(data.shippingFee);
                                            displayProductDiscount.textContent = '-' + formatCurrency(data.productDiscount);
                                            displayShippingDiscount.textContent = '-' + formatCurrency(data.shippingDiscount);
                                            displayFinalTotal.textContent = formatCurrency(data.finalTotal);

                                            refreshCouponSelectOptions(productCouponInput, data.productCoupons, selectedProductCouponText, 'sản phẩm');
                                            refreshCouponSelectOptions(shippingCouponInput, data.shippingCoupons, selectedShippingCouponText, 'vận chuyển');
                                        })
                                        .catch(error => console.error('Error:', error));
                                }

                                function refreshCouponSelectOptions(selectElement, coupons, selectedTextElement, label) {
                                    const current = (selectElement.value || '').trim().toUpperCase();
                                    const availableCoupons = (coupons || []).filter(c => c && c.eligible);
                                    const optionsHtml = ['<option value="">-- Không áp dụng coupon ' + label + ' --</option>'];

                                    availableCoupons.forEach(coupon => {
                                        const code = String(coupon.code || '').trim();
                                        if (!code) {
                                            return;
                                        }
                                        const valueDisplay = String(coupon.valueDisplay || '');
                                        const isSelected = current && code.toUpperCase() === current;
                                        optionsHtml.push('<option value="' + code + '"' + (isSelected ? ' selected' : '') + '>' +
                                            code + ' - ' + valueDisplay +
                                            '</option>');
                                    });

                                    selectElement.innerHTML = optionsHtml.join('');

                                    const selectedCode = (selectElement.value || '').trim().toUpperCase();
                                    if (!selectedCode) {
                                        selectedTextElement.textContent = 'Chưa chọn coupon ' + label + '.';
                                        return;
                                    }
                                    selectedTextElement.innerHTML = 'Đã chọn: <span class="fw-bold text-success">' + selectedCode + '</span>';
                                }

                                // Lắng nghe sự kiện thay đổi
                                shippingSelect.addEventListener('change', updateOrderPreview);

                                productCouponInput.addEventListener('change', updateOrderPreview);
                                shippingCouponInput.addEventListener('change', updateOrderPreview);

                                // Logic hiển thị địa chỉ (Giữ nguyên của bạn)
                                const selectedAddress = document.getElementById('selectedAddressId');
                                const streetInput = document.getElementById('street');
                                const wardInput = document.getElementById('ward');
                                const districtInput = document.getElementById('district');
                                const cityInput = document.getElementById('city');

                                function onAddressChange() {
                                    const option = selectedAddress.options[selectedAddress.selectedIndex];
                                    if (!selectedAddress.value) {
                                        streetInput.value = wardInput.value = districtInput.value = cityInput.value = '';
                                        return;
                                    }
                                    streetInput.value = option.getAttribute('data-street');
                                    wardInput.value = option.getAttribute('data-ward');
                                    districtInput.value = option.getAttribute('data-district');
                                    cityInput.value = option.getAttribute('data-city');
                                }

                                selectedAddress.addEventListener('change', onAddressChange);

                                // Khởi chạy lần đầu để lấy phí ship mặc định
                                onAddressChange();
                                updateOrderPreview();

                                // Validation trước khi submit
                                form.addEventListener('submit', function (event) {
                                    if (!selectedAddress.value.trim()) {
                                        event.preventDefault();
                                        alert('Vui lòng chọn địa chỉ giao hàng.');
                                    }
                                });
                            })();
                        </script>
            </body>

            </html>