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
    <section class="card detail-header">
      <div>
        <a class="back-link" href="${pageContext.request.contextPath}/order">Back to orders</a>
        <p class="eyebrow">Order detail</p>
        <h1>Order #${order.id}</h1>
      </div>
      <span class="status-chip ${order.statusCssClass}">${order.status}</span>
    </section>

    <section class="detail-grid">
      <article class="card detail-card">
        <h2>Summary</h2>
        <dl class="detail-list">
          <div><dt>Created at</dt><dd>${order.createdAtDisplay}</dd></div>
          <div><dt>Items</dt><dd>${order.itemCount}</dd></div>
          <div><dt>Total</dt><dd>${order.totalAmountDisplay}</dd></div>
          <div><dt>Customer</dt><dd>${order.customerName}</dd></div>
          <div><dt>Shipping</dt><dd>${order.shippingMethod}</dd></div>
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
  </main>
</body>
</html>

