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
            <a href="${pageContext.request.contextPath}/cart" class="btn btn-outline-secondary">Quay lại giỏ hàng</a>
        </div>

        <c:if test="${not empty message}">
            <div class="alert alert-info alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/checkout/place" id="placeOrderForm">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
            <c:forEach var="bookId" items="${selectedBookIds}">
                <input type="hidden" name="selectedBookIds" value="${bookId}"/>
            </c:forEach>

            <div class="row g-4">
                <div class="col-lg-7">
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-3">Địa chỉ giao hàng</h5>

                            <div class="mb-3">
                                <div class="d-flex justify-content-between align-items-center mb-2">
                                    <label for="selectedAddressId" class="form-label mb-0">Chọn địa chỉ đã lưu</label>
                                    <button type="button" class="btn btn-outline-primary btn-sm" data-bs-toggle="modal" data-bs-target="#newAddressModal">
                                        <i class="fas fa-plus"></i> Thêm địa chỉ mới
                                    </button>
                                </div>
                                <select class="form-select" id="selectedAddressId" name="selectedAddressId" required>
                                    <option value="">-- Vui lòng chọn địa chỉ đã lưu --</option>
                                    <c:forEach var="addr" items="${userAddresses}">
                                        <option value="${addr.id}"
                                                data-street="${addr.street}"
                                                data-ward="${addr.ward}"
                                                data-district="${addr.district}"
                                                data-city="${addr.city}"
                                                ${selectedAddressId != null and selectedAddressId == addr.id ? 'selected' : ''}>
                                            ${addr.street}, ${addr.ward}, ${addr.district}, ${addr.city}
                                        </option>
                                    </c:forEach>
                                </select>
                                <div class="form-text">Bạn chỉ có thể đặt hàng bằng địa chỉ đã lưu.</div>
                            </div>

                            <div class="row g-3">
                                <div class="col-12">
                                    <h6 class="mb-1">Thông tin địa chỉ đã chọn</h6>
                                </div>
                                <div class="col-md-6">
                                    <label for="street" class="form-label">Số nhà, tên đường</label>
                                    <input type="text" class="form-control bg-light" id="street" placeholder="Chọn địa chỉ ở trên" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label for="ward" class="form-label">Phường/Xã</label>
                                    <input type="text" class="form-control bg-light" id="ward" placeholder="Chọn địa chỉ ở trên" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label for="district" class="form-label">Quận/Huyện</label>
                                    <input type="text" class="form-control bg-light" id="district" placeholder="Chọn địa chỉ ở trên" readonly>
                                </div>
                                <div class="col-md-6">
                                    <label for="city" class="form-label">Tỉnh/Thành phố</label>
                                    <input type="text" class="form-control bg-light" id="city" placeholder="Chọn địa chỉ ở trên" readonly>
                                </div>
                            </div>

                            <hr class="my-4">

                            <h5 class="fw-bold mb-3">Thông tin nhận hàng</h5>
                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="receiverName" class="form-label">Tên người nhận</label>
                                    <input type="text" class="form-control" id="receiverName" name="receiverName" value="${receiverName}" placeholder="Nhập tên người nhận" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="phoneNumber" class="form-label">Số điện thoại</label>
                                    <input type="text" class="form-control" id="phoneNumber" name="phoneNumber" value="${phoneNumber}" placeholder="Ví dụ: 0912345678 hoặc +84912345678" pattern="^(0|\+84)(3|5|7|8|9)[0-9]{8}$" title="Số điện thoại VN hợp lệ: 0912345678 hoặc +84912345678" required>
                                </div>
                                <div class="col-12">
                                    <label for="note" class="form-label">Ghi chú</label>
                                    <textarea class="form-control" id="note" name="note" rows="3" placeholder="Ví dụ: Giao giờ hành chính..."></textarea>
                                </div>
                            </div>

                            <hr class="my-4">

                            <h5 class="fw-bold mb-3">Phương thức thanh toán</h5>
                            <select class="form-select" name="paymentMethod" id="paymentMethod" required>
                                <c:forEach var="method" items="${paymentMethods}">
                                    <option value="${method}">${method}</option>
                                </c:forEach>
                            </select>

                            <hr class="my-4">

                            <h5 class="fw-bold mb-3">Vận chuyển và ưu đãi</h5>
                            <div class="row g-3">
                                <div class="col-12">
                                    <label for="shippingMethod" class="form-label">Phương thức giao hàng</label>
                                    <select class="form-select" id="shippingMethod" name="shippingMethod">
                                        <option value="STANDARD" selected>Giao tiêu chuẩn (2-4 ngày)</option>
                                        <option value="FAST">Giao nhanh (1-2 ngày)</option>
                                        <option value="ECONOMY">Giao tiết kiệm (3-6 ngày)</option>
                                    </select>
                                    <div class="form-text">UI tạm thời, chưa tích hợp backend tính phí ship.</div>
                                </div>

                                <div class="col-12">
                                    <label for="couponCode" class="form-label">Mã giảm giá</label>
                                    <div class="input-group">
                                        <input type="text" class="form-control" id="couponCode" name="couponCode" placeholder="Nhập mã giảm giá">
                                        <button type="button" class="btn btn-outline-secondary" id="applyCouponBtn">Áp dụng</button>
                                    </div>
                                    <small id="couponHint" class="text-muted">Chưa áp dụng mã giảm giá.</small>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="col-lg-5">
                    <div class="card shadow-sm border-0">
                        <div class="card-body p-4">
                            <h5 class="fw-bold mb-3">Mặt hàng đã chọn</h5>
                            <c:forEach var="item" items="${selectedItems}">
                                <div class="d-flex justify-content-between align-items-start border-bottom pb-2 mb-2">
                                    <div class="pe-2">
                                        <div class="fw-semibold">${item.book.title}</div>
                                        <small class="text-muted">SL: ${item.quantity}</small>
                                    </div>
                                    <div class="text-end text-danger fw-semibold">
                                        <fmt:formatNumber value="${item.book.price * item.quantity}" pattern="#,###"/> VND
                                    </div>
                                </div>
                            </c:forEach>

                            <div class="d-flex justify-content-between mt-3 pt-2 border-top">
                                <span class="fw-bold">Tổng thanh toán</span>
                                <span class="fw-bold text-danger"><fmt:formatNumber value="${selectedTotal}" pattern="#,###"/> VND</span>
                            </div>

                            <button type="submit" class="btn btn-success w-100 mt-4">Xác nhận đặt hàng</button>
                        </div>
                    </div>
                </div>
            </div>
        </form>

        <div class="modal fade" id="newAddressModal" tabindex="-1" aria-labelledby="newAddressModalLabel" aria-hidden="true">
            <div class="modal-dialog modal-lg modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title" id="newAddressModalLabel">Thêm địa chỉ mới</h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/checkout/address/add" id="addAddressForm">
                        <div class="modal-body">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <c:forEach var="bookId" items="${selectedBookIds}">
                                <input type="hidden" name="selectedBookIds" value="${bookId}"/>
                            </c:forEach>

                            <div class="row g-3">
                                <div class="col-md-6">
                                    <label for="newStreet" class="form-label">Số nhà, tên đường</label>
                                    <input type="text" class="form-control" id="newStreet" name="street" placeholder="Ví dụ: 12 Nguyễn Trãi" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="newWard" class="form-label">Phường/Xã</label>
                                    <input type="text" class="form-control" id="newWard" name="ward" placeholder="Ví dụ: Phường 7" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="newDistrict" class="form-label">Quận/Huyện</label>
                                    <input type="text" class="form-control" id="newDistrict" name="district" placeholder="Ví dụ: Quận 5" required>
                                </div>
                                <div class="col-md-6">
                                    <label for="newCity" class="form-label">Tỉnh/Thành phố</label>
                                    <input type="text" class="form-control" id="newCity" name="city" placeholder="Ví dụ: TP. Hồ Chí Minh" required>
                                </div>
                            </div>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Đóng</button>
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
            const selectedAddress = document.getElementById('selectedAddressId');
            const streetInput = document.getElementById('street');
            const wardInput = document.getElementById('ward');
            const districtInput = document.getElementById('district');
            const cityInput = document.getElementById('city');
            const applyCouponBtn = document.getElementById('applyCouponBtn');
            const couponCodeInput = document.getElementById('couponCode');
            const couponHint = document.getElementById('couponHint');

            if (!form) {
                return;
            }

            function setAddressInputs(street, ward, district, city) {
                streetInput.value = street || '';
                wardInput.value = ward || '';
                districtInput.value = district || '';
                cityInput.value = city || '';
            }

            function onAddressChange() {
                const option = selectedAddress.options[selectedAddress.selectedIndex];
                const hasSelectedAddress = selectedAddress.value && selectedAddress.value.trim().length > 0;

                if (!hasSelectedAddress) {
                    setAddressInputs('', '', '', '');
                    return;
                }

                setAddressInputs(
                    option.getAttribute('data-street'),
                    option.getAttribute('data-ward'),
                    option.getAttribute('data-district'),
                    option.getAttribute('data-city')
                );
            }

            selectedAddress.addEventListener('change', onAddressChange);
            onAddressChange();

            form.addEventListener('submit', function (event) {
                const selectedAddressId = selectedAddress.value.trim();
                const phoneInput = document.getElementById('phoneNumber');
                const phoneValue = (phoneInput.value || '').replace(/\s+/g, '');
                const vnPhoneRegex = /^(0|\+84)(3|5|7|8|9)\d{8}$/;

                if (selectedAddressId.length === 0) {
                    event.preventDefault();
                    alert('Vui lòng chọn địa chỉ đã lưu trước khi đặt hàng.');
                    return;
                }

                if (!vnPhoneRegex.test(phoneValue)) {
                    event.preventDefault();
                    alert('Số điện thoại không đúng định dạng Việt Nam. Ví dụ: 0912345678 hoặc +84912345678');
                    phoneInput.focus();
                }
            });

            if (applyCouponBtn && couponCodeInput && couponHint) {
                applyCouponBtn.addEventListener('click', function () {
                    const code = (couponCodeInput.value || '').trim();
                    if (!code) {
                        couponHint.textContent = 'Vui lòng nhập mã giảm giá.';
                        return;
                    }
                    couponHint.textContent = 'Da nhan ma "' + code + '" (chua tich hop backend).';
                });
            }
        })();
    </script>
</body>
</html>
