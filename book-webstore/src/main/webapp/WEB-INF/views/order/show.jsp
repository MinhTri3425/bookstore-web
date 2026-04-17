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

  <main class="order-page container mt-4">
    <section class="page-header card border-0 shadow-sm p-4 mb-4">
      <div class="d-flex justify-content-between align-items-center">
        <div>
          <p class="eyebrow text-muted text-uppercase small mb-1">${pageEyebrow}</p>
          <h1 class="h3 fw-bold mb-0">${pageHeading}</h1>
        </div>
        <div class="page-switch-links d-flex gap-2">
          <c:if test="${viewMode == 'admin'}">
            <a class="btn btn-primary shadow-sm" href="${pageContext.request.contextPath}/admin/shipping">
              <i class="fas fa-truck-fast"></i> Shipping Monitor
            </a>
          </c:if>
          <a class="btn btn-outline-secondary" href="${pageContext.request.contextPath}/my-orders">
             <i class="fas fa-user"></i> My Orders
          </a>
        </div>
      </div>
    </section>

    <c:if test="${not empty successMessage}">
      <section class="alert alert-success border-0 shadow-sm mb-4">${successMessage}</section>
    </c:if>

    <section class="card table-card border-0 shadow-sm">
      <div class="table-responsive">
        <table class="table table-hover align-middle mb-0">
          <thead class="table-light">
            <tr>
              <th class="ps-4">Order ID</th>
              <th>Status</th>
              <th>Created At</th>
              <th>Total</th>
              <c:if test="${viewMode == 'admin'}">
                <th>Customer</th>
              </c:if>
              <th>Payment</th>
              <th>Shipping</th>
              <th>Shipper</th> 
              <th class="text-center">Action</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${not empty orders}">
                <c:forEach var="order" items="${orders}">
                  <tr>
                    <td class="ps-4 fw-bold">#${order.id}</td>
                    <td>
                      <span class="status-chip ${order.statusCssClass}">${order.status}</span>
                    </td>
                    <td class="small text-muted">${order.createdAtDisplay}</td>
                    <td class="fw-bold text-primary">${order.totalAmountDisplay}</td>
                    <c:if test="${viewMode == 'admin'}">
                      <td>${order.customerName}</td>
                    </c:if>
                    <td class="small">${order.paymentStatusDisplay}</td>
                    <td>
                        <span class="badge ${not empty order.shipperName ? 'bg-info' : 'bg-secondary'} rounded-pill">
                            ${order.shippingStatusDisplay}
                        </span>
                    </td>
                    <td>
                        <c:choose>
                            <c:when test="${not empty order.shipperName}">
                                <a href="${pageContext.request.contextPath}/admin/shipping?orderId=${order.id}" class="text-success text-decoration-none fw-bold">
                                    <i class="fas fa-truck-pickup me-1"></i> ${order.shipperName}
                                </a>
                            </c:when>
                            <c:otherwise>
                                <span class="text-muted small italic">Waiting...</span>
                            </c:otherwise>
                        </c:choose>
                    </td>
                    <td class="text-center">
                      <c:url var="detailUrl" value="${detailBasePath}/${order.id}">
                        <c:if test="${viewMode == 'customer' and not empty selectedCustomerId}">
                          <c:param name="customerId" value="${selectedCustomerId}" />
                        </c:if>
                      </c:url>
                      <a class="btn btn-sm btn-light border shadow-sm" href="${pageContext.request.contextPath}${detailUrl}">
                        <i class="fas fa-eye text-primary"></i> <span>Details</span>
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                  <tr>
                      <td colspan="9" class="text-center py-5 text-muted">
                          <i class="fas fa-inbox fa-3x mb-3 opacity-25"></i><br>
                          No orders found.
                      </td>
                  </tr>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>
    </section>
  </main>

  <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>