<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
    <%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

        <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />

        <div class="container py-5">
            <div class="row mb-4 align-items-end">
                <div class="col-md-6">
                    <h2 class="fw-bold mb-1"><i class="fas fa-user-shield text-primary me-2"></i>Quản lý Shipper</h2>
                    <p class="text-muted small">Cấp quyền hoặc thu hồi quyền giao hàng của người dùng.</p>
                </div>
                <div class="col-md-6">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body p-3">
                            <form action="${pageContext.request.contextPath}/admin/shippers/promote" method="post"
                                class="d-flex gap-2">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                                <input type="email" name="email" class="form-control"
                                    placeholder="Nhập Email khách hàng để nâng cấp..." required>
                                <button type="submit" class="btn btn-primary text-nowrap">
                                    <i class="fas fa-plus"></i> Thêm Shipper
                                </button>
                            </form>
                        </div>
                    </div>
                </div>
            </div>

            <c:if test="${not empty success}">
                <div class="alert alert-success alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                    <i class="fas fa-check-circle me-2"></i> ${success}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>
            <c:if test="${not empty error}">
                <div class="alert alert-danger alert-dismissible fade show border-0 shadow-sm mb-4" role="alert">
                    <i class="fas fa-exclamation-triangle me-2"></i> ${error}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                </div>
            </c:if>

            <div class="card border-0 shadow-sm">
                <div class="card-header bg-white py-3">
                    <h5 class="mb-0 fw-bold text-muted small text-uppercase">Danh sách shipper đang hoạt động</h5>
                </div>
                <div class="table-responsive">
                    <table class="table table-hover align-middle mb-0">
                        <thead class="table-light">
                            <tr>
                                <th class="px-4">ID</th>
                                <th>Thông tin tài khoản</th>
                                <th>Liên hệ</th>
                                <th class="text-center">Trạng thái</th>
                                <th class="text-center">Thao tác</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="s" items="${shippers}">
                                <tr>
                                    <td class="px-4 text-muted fw-bold">#${s.id}</td>
                                    <td>
                                        <div class="fw-bold text-dark">${s.name}</div>
                                        <div class="small text-muted">${s.email}</div>
                                    </td>
                                    <td>
                                        <div class="small"><i class="fas fa-phone-alt me-1 text-muted"></i>
                                            ${s.phoneNumber}</div>
                                    </td>
                                    <td class="text-center">
                                        <span
                                            class="badge bg-success-subtle text-success rounded-pill px-3">Active</span>
                                    </td>
                                    <td class="text-center">
                                        <button type="button"
                                            class="btn btn-sm btn-outline-danger shadow-sm btn-open-revoke-modal"
                                            data-bs-toggle="modal" data-bs-target="#revokeShipperModal"
                                            data-shipper-id="${s.id}" data-shipper-name="${s.name}"
                                            data-shipper-email="${s.email}">
                                            <i class="fas fa-user-minus"></i> Thu hồi quyền
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty shippers}">
                                <tr>
                                    <td colspan="5" class="text-center py-5">
                                        <i class="fas fa-user-slash fa-3x text-muted opacity-25 mb-3"></i>
                                        <p class="text-muted">Chưa có shipper nào được cấp quyền.</p>
                                    </td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <div class="modal fade" id="revokeShipperModal" tabindex="-1" aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content border-0 shadow">
                    <div class="modal-header">
                        <h5 class="modal-title"><i class="fas fa-user-slash text-danger me-2"></i>Xác nhận thu hồi quyền
                        </h5>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <p class="mb-2">Bạn có chắc chắn muốn thu hồi quyền shipper của tài khoản:</p>
                        <div class="fw-bold" id="revokeShipperName"></div>
                        <div class="text-muted small" id="revokeShipperEmail"></div>
                        <div class="small text-danger mt-2">Sau khi thu hồi, tài khoản này sẽ không thể nhận đơn mới.
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-outline-secondary" data-bs-dismiss="modal">Đóng</button>
                        <form method="post" id="revokeShipperForm" class="d-inline">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
                            <button type="submit" class="btn btn-danger">Xác nhận thu hồi</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>

        <jsp:include page="/WEB-INF/views/fragments/footer.jsp" />
        <script>
            (function () {
                const revokeForm = document.getElementById('revokeShipperForm');
                const nameText = document.getElementById('revokeShipperName');
                const emailText = document.getElementById('revokeShipperEmail');

                if (!revokeForm || !nameText || !emailText) {
                    return;
                }

                document.querySelectorAll('.btn-open-revoke-modal').forEach(function (button) {
                    button.addEventListener('click', function () {
                        const shipperId = this.getAttribute('data-shipper-id') || '';
                        const shipperName = this.getAttribute('data-shipper-name') || '';
                        const shipperEmail = this.getAttribute('data-shipper-email') || '';

                        revokeForm.action = '${pageContext.request.contextPath}/admin/shippers/' + shipperId + '/revoke';
                        nameText.textContent = shipperName;
                        emailText.textContent = shipperEmail;
                    });
                });
            })();
        </script>