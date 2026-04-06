<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${pageTitle}</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
  <main class="order-page detail-page">
    <c:url var="backUrl" value="${backPath}">
      <c:if test="${viewMode == 'customer' and not empty selectedCustomerId}">
        <c:param name="customerId" value="${selectedCustomerId}" />
      </c:if>
    </c:url>

    <section class="card detail-header">
      <div>
        <a class="back-link" href="${pageContext.request.contextPath}${backUrl}">Back to orders</a>
        <p class="eyebrow">${viewMode == 'admin' ? 'Admin order detail' : 'Customer order detail'}</p>
        <h1>Order #${order.id}</h1>
      </div>
      <span class="status-chip ${order.statusCssClass}">${order.status}</span>
    </section>

    <c:if test="${not empty successMessage}">
      <section class="flash-message flash-success card">${successMessage}</section>
    </c:if>

    <section class="detail-grid detail-grid-wide">
      <article class="card detail-card">
        <h2>Summary</h2>
        <dl class="detail-list">
          <div><dt>Created at</dt><dd>${order.createdAtDisplay}</dd></div>
          <div><dt>Items</dt><dd>${order.itemCount}</dd></div>
          <div><dt>Total</dt><dd>${order.totalAmountDisplay}</dd></div>
          <div><dt>Customer</dt><dd>${order.customerName}</dd></div>
          <div><dt>Payment status</dt><dd>${order.paymentStatusDisplay}</dd></div>
          <div><dt>Shipping method</dt><dd>${order.shippingMethod}</dd></div>
          <div><dt>Shipping status</dt><dd>${order.shippingStatusDisplay}</dd></div>
          <div><dt>Shipper</dt><dd>${order.shipperName}</dd></div>
        </dl>
      </article>

      <article class="card detail-card">
        <h2>Items</h2>
        <c:choose>
          <c:when test="${not empty order.items}">
            <div class="detail-items">
              <c:forEach var="item" items="${order.items}">
                <div class="detail-item-row">
                  <div>
                    <strong>${item.bookTitle}</strong>
                    <p>Item ID: ${item.id}</p>
                  </div>
                  <span>Qty ${item.quantity}</span>
                </div>
              </c:forEach>
            </div>
          </c:when>
          <c:otherwise>
            <div class="empty-state compact">
              <h2>No items</h2>
              <p>This order does not contain any order items.</p>
            </div>
          </c:otherwise>
        </c:choose>
      </article>
    </section>

    <c:if test="${viewMode == 'admin'}">
      <section class="action-grid">
        <article class="card detail-card action-card">
          <h2>Update Order</h2>
          <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/status">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div class="field-group">
              <label for="orderStatus">Order status</label>
              <select id="orderStatus" name="status">
                <c:forEach var="statusOption" items="${orderStatusOptions}">
                  <option value="${statusOption}" ${order.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                </c:forEach>
              </select>
            </div>
            <button class="primary-btn" type="submit">Save order status</button>
          </form>
          <p class="helper-copy">Allowed flow: PENDING -> CONFIRMED -> COMPLETED or cancel before completion.</p>
        </article>

        <article class="card detail-card action-card">
          <h2>Update Payment</h2>
          <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/payment">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div class="field-group">
              <label for="paymentStatus">Payment status</label>
              <select id="paymentStatus" name="status">
                <c:forEach var="statusOption" items="${paymentStatusOptions}">
                  <option value="${statusOption}" ${order.payment != null and order.payment.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                </c:forEach>
              </select>
            </div>
            <button class="primary-btn" type="submit">Save payment status</button>
          </form>
        </article>

        <article class="card detail-card action-card">
          <h2>Update Shipping</h2>
          <form class="inline-form" method="post" action="${pageContext.request.contextPath}/admin/orders/${order.id}/shipping">
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
            <div class="field-group">
              <label for="shippingStatus">Shipping status</label>
              <select id="shippingStatus" name="status">
                <c:forEach var="statusOption" items="${shippingStatusOptions}">
                  <option value="${statusOption}" ${order.shipping != null and order.shipping.status == statusOption ? 'selected' : ''}>${statusOption}</option>
                </c:forEach>
              </select>
            </div>
            <div class="field-group">
              <label for="shipperId">Shipper</label>
              <select id="shipperId" name="shipperId">
                <option value="">Keep current shipper</option>
                <c:forEach var="shipper" items="${shipperOptions}">
                  <option value="${shipper.id}" ${order.shipping != null and order.shipping.shipperId == shipper.id ? 'selected' : ''}>${shipper.name}</option>
                </c:forEach>
              </select>
            </div>
            <button class="primary-btn" type="submit">Save shipping</button>
          </form>
        </article>
      </section>
    </c:if>

    <c:if test="${viewMode == 'customer'}">
      <section class="action-grid single-action-grid">
        <article class="card detail-card action-card">
          <h2>Customer Action</h2>
          <c:choose>
            <c:when test="${order.canCancel}">
              <form class="inline-form" method="post" action="${pageContext.request.contextPath}/my-orders/${order.id}/cancel">
                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}">
                <input type="hidden" name="customerId" value="${selectedCustomerId}">
                <button class="danger-btn" type="submit">Cancel this order</button>
              </form>
              <p class="helper-copy">Customers can cancel only pending orders.</p>
            </c:when>
            <c:otherwise>
              <p class="helper-copy">This order can no longer be cancelled.</p>
            </c:otherwise>
          </c:choose>
        </article>
      </section>
    </c:if>
  </main>
</body>
</html>
