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
  <main class="order-page">
    <section class="page-header card">
      <div>
        <p class="eyebrow">Book Webstore</p>
        <h1>Order List</h1>
      </div>
    </section>

    <section class="card filter-card">
      <form class="filter-form" method="get" action="${pageContext.request.contextPath}/order">
        <div class="field-group">
          <label for="orderId">Order ID</label>
          <input id="orderId" name="orderId" type="text" value="${searchOrderId}" placeholder="Search exact order id">
        </div>

        <div class="field-group">
          <label for="status">Status</label>
          <select id="status" name="status">
            <option value="">All status</option>
            <c:forEach var="statusOption" items="${statusOptions}">
              <option value="${statusOption}" ${selectedStatus == statusOption.toString() ? 'selected' : ''}>${statusOption}</option>
            </c:forEach>
          </select>
        </div>

        <div class="action-group">
          <button class="primary-btn" type="submit">Apply</button>
          <a class="secondary-btn" href="${pageContext.request.contextPath}/order">Reset</a>
        </div>
      </form>
    </section>

    <section class="card table-card">
      <div class="table-scroll">
        <table class="order-table">
          <thead>
            <tr>
              <th>Order ID</th>
              <th>Status</th>
              <th>Created At</th>
              <th>Items</th>
              <th>Total</th>
              <th>Customer</th>
              <th>Shipping</th>
              <th>Action</th>
            </tr>
          </thead>
          <tbody>
            <c:choose>
              <c:when test="${not empty orders}">
                <c:forEach var="order" items="${orders}">
                  <tr>
                    <td>#${order.id}</td>
                    <td>
                      <span class="status-chip ${order.statusCssClass}">${order.status}</span>
                    </td>
                    <td>${order.createdAtDisplay}</td>
                    <td>${order.itemCount}</td>
                    <td>${order.totalAmountDisplay}</td>
                    <td>${order.customerName}</td>
                    <td>${order.shippingMethod}</td>
                    <td>
                      <a class="icon-action" href="${pageContext.request.contextPath}/order/${order.id}" aria-label="View order ${order.id}">
                        <img src="${pageContext.request.contextPath}/images/pencil-alt0.svg" alt="">
                        <span>Details</span>
                      </a>
                    </td>
                  </tr>
                </c:forEach>
              </c:when>
              <c:otherwise>
                <tr>
                  <td colspan="8">
                    <div class="empty-state">
                      <h2>No orders found</h2>
                      <p>Try changing the search input or clearing the status filter.</p>
                    </div>
                  </td>
                </tr>
              </c:otherwise>
            </c:choose>
          </tbody>
        </table>
      </div>

      <div class="table-footer">
        <c:set var="fromIndex" value="${totalElements == 0 ? 0 : (currentPage * pageSize) + 1}" />
        <c:set var="toIndex" value="${totalElements == 0 ? 0 : ((currentPage + 1) * pageSize < totalElements ? (currentPage + 1) * pageSize : totalElements)}" />
        <p class="results-copy">Showing ${fromIndex} to ${toIndex} of ${totalElements} results</p>

        <c:if test="${totalPages > 1}">
          <nav class="pagination" aria-label="Order pages">
            <c:if test="${currentPage > 0}">
              <c:url var="prevUrl" value="/order">
                <c:param name="page" value="${currentPage - 1}" />
                <c:param name="size" value="${pageSize}" />
                <c:if test="${not empty selectedStatus}">
                  <c:param name="status" value="${selectedStatus}" />
                </c:if>
                <c:if test="${not empty searchOrderId}">
                  <c:param name="orderId" value="${searchOrderId}" />
                </c:if>
              </c:url>
              <a class="page-link" href="${pageContext.request.contextPath}${prevUrl}">Prev</a>
            </c:if>

            <c:forEach var="pageIndex" begin="0" end="${totalPages - 1}">
              <c:url var="pageUrl" value="/order">
                <c:param name="page" value="${pageIndex}" />
                <c:param name="size" value="${pageSize}" />
                <c:if test="${not empty selectedStatus}">
                  <c:param name="status" value="${selectedStatus}" />
                </c:if>
                <c:if test="${not empty searchOrderId}">
                  <c:param name="orderId" value="${searchOrderId}" />
                </c:if>
              </c:url>
              <a class="page-link ${pageIndex == currentPage ? 'is-active' : ''}" href="${pageContext.request.contextPath}${pageUrl}">${pageIndex + 1}</a>
            </c:forEach>

            <c:if test="${currentPage + 1 < totalPages}">
              <c:url var="nextUrl" value="/order">
                <c:param name="page" value="${currentPage + 1}" />
                <c:param name="size" value="${pageSize}" />
                <c:if test="${not empty selectedStatus}">
                  <c:param name="status" value="${selectedStatus}" />
                </c:if>
                <c:if test="${not empty searchOrderId}">
                  <c:param name="orderId" value="${searchOrderId}" />
                </c:if>
              </c:url>
              <a class="page-link" href="${pageContext.request.contextPath}${nextUrl}">Next</a>
            </c:if>
          </nav>
        </c:if>
      </div>
    </section>
  </main>
</body>
</html>
