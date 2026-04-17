<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${pageTitle}</title>
  <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <c:choose>
      <c:when test="${viewMode == 'admin'}">
          <jsp:include page="/WEB-INF/views/fragments/admin-header.jsp" />
      </c:when>
      <c:otherwise>
          <jsp:include page="/WEB-INF/views/fragments/header.jsp" />
      </c:otherwise>
  </c:choose>

  <main class="order-page detail-page container mt-4">
    <section class="mb-3">
        <a class="btn btn-sm btn-outline-secondary" href="${pageContext.request.contextPath}${backUrl}">
            <i class="fas fa-arrow-left"></i> Quay lại danh sách
        </a>
    </section>

    <section class="detail-grid detail-grid-wide row">
      <article class="card detail-card col-md-6 shadow-sm border-0 mb-4">
        <div class="card-body">
            <h2 class="card-title h5 fw-bold mb-3 text-uppercase text-muted">Order Summary</h2>
            <dl class="detail-list">
              <div class="d-flex justify-content-between mb-2"><dt>Created at</dt><dd>${order.createdAtDisplay}</dd></div>
              <div class="d-flex justify-content-between mb-2"><dt>Customer</dt><dd><strong>${order.customerName}</strong></dd></div>
              <div class="d-flex justify-content-between mb-2 text-primary fw-bold"><dt>Total Amount</dt><dd>${order.totalAmountDisplay}</dd></div>
              <hr>
              <div class="d-flex justify-content-between mb-2"><dt>Payment</dt><dd>${order.paymentStatusDisplay}</dd></div>
              <div class="d-flex justify-content-between mb-2"><dt>Shipping Status</dt><dd><span class="status-chip">${order.shippingStatusDisplay}</span></dd></div>
              <div class="d-flex justify-content-between mb-2">
                  <dt>Current Shipper</dt>
                  <dd>
                      <c:choose>
                          <c:when test="${not empty order.shipperName}">
                              <a href="${pageContext.request.contextPath}/admin/shipping?orderId=${order.id}" class="text-success fw-bold text-decoration-none">
                                  <i class="fas fa-truck"></i> ${order.shipperName}
                              </a>
                          </c:when>
                          <c:otherwise><span class="text-muted italic">No shipper assigned yet</span></c:otherwise>
                      </c:choose>
                  </dd>
              </div>
            </dl>
        </div>
      </article>
      
      </section>

    <c:if test="${viewMode == 'admin'}">
      <section class="action-grid row g-3">
        <div class="col-md-4">
            <article class="card detail-card action-card shadow-sm border-0 h-100">
              <div class="card-body">
                  <h2 class="h6 fw-bold">Update Order Status</h2>
                  <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/status">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <div class="mb-3">
                      <select class="form-select form-select-sm" name="status">
                        <c:forEach var="statusOption" items="${orderStatusOptions}">
                          <option value="${statusOption}" ${order.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                        </c:forEach>
                      </select>
                    </div>
                    <button class="btn btn-primary btn-sm w-100" type="submit">Update Order</button>
                  </form>
                  <p class="small text-muted mt-2 italic">
                      <i class="fas fa-info-circle"></i> <b>CONFIRMED</b> will push to Shipper Market.
                  </p>
              </div>
            </article>
        </div>

        <div class="col-md-4">
            <article class="card detail-card action-card shadow-sm border-0 h-100">
              <div class="card-body">
                  <h2 class="h6 fw-bold">Update Payment</h2>
                  <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/payment">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <div class="mb-3">
                      <select class="form-select form-select-sm" name="status">
                        <c:forEach var="statusOption" items="${paymentStatusOptions}">
                          <option value="${statusOption}" ${order.payment != null and order.payment.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                        </c:forEach>
                      </select>
                    </div>
                    <button class="btn btn-primary btn-sm w-100" type="submit">Update Payment</button>
                  </form>
              </div>
            </article>
        </div>

        <div class="col-md-4">
            <article class="card detail-card action-card shadow-sm border-0 h-100">
              <div class="card-body">
                  <h2 class="h6 fw-bold">Manual Shipping Override</h2>
                  <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/shipping">
                    <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                    <div class="mb-3">
                      <select class="form-select form-select-sm" name="shipperId">
                        <option value="">Wait for market...</option>
                        <c:forEach var="shipper" items="${shipperOptions}">
                          <option value="${shipper.id}" ${order.shipping != null and order.shipping.shipperId == shipper.id ? 'selected' : ''}>${shipper.name}</option>
                        </c:forEach>
                      </select>
                    </div>
                    <button class="btn btn-dark btn-sm w-100" type="submit">Assign Manually</button>
                  </form>
                  <p class="small text-danger mt-2 italic">Skips shipper self-pickup.</p>
              </div>
            </article>
        </div>
      </section>
    </c:if>
  </main>
</body>
</html>