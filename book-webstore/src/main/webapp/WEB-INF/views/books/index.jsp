<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Cửa hàng sách - Danh sách sách</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        .book-card { transition: all 0.3s ease; border: none; }
        .book-card:hover { transform: translateY(-10px); shadow: 0 1rem 3rem rgba(0,0,0,0.175); }
        .book-image-wrapper { background-color: #f8f9fa; }
        .book-image-wrapper img { object-fit: contain; padding: 10px; }
        .book-card:hover .book-overlay { opacity: 1 !important; }
        .text-truncate-2 {
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            height: 3em;
        }
        .filter-card { position: sticky; top: 90px; }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/fragments/header.jsp" %>

    <section class="hero-section text-white py-5 bg-dark">
        <div class="container">
            <h1 class="display-4 fw-bold mb-2 text-white">
                <i class="fas fa-book-open me-2"></i>Danh sách Sách
            </h1>
            <p class="lead text-white-50">Khám phá kho tàng tri thức với hàng ngàn đầu sách hấp dẫn</p>
        </div>
    </section>

    <div class="container py-5">
        <div class="row">
            <div class="col-lg-3 mb-4">
                <div class="card shadow-sm border-0 filter-card">
                    <div class="card-header bg-primary text-white py-3">
                        <h5 class="mb-0 text-white"><i class="fas fa-filter me-2"></i>Bộ lọc</h5>
                    </div>
                    <div class="card-body">
                        <form method="get" action="${pageContext.request.contextPath}/books">
                            <div class="mb-3">
                                <label class="form-label fw-bold small text-uppercase">Tìm kiếm</label>
                                <input type="text" class="form-control" name="keyword" 
                                       placeholder="Tên sách, tác giả..." value="${keyword}">
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold small text-uppercase">Khoảng giá (₫)</label>
                                <div class="row g-2">
                                    <div class="col-6">
                                        <input type="number" class="form-control form-control-sm" name="minPrice" 
                                               placeholder="Từ" value="${minPrice}">
                                    </div>
                                    <div class="col-6">
                                        <input type="number" class="form-control form-control-sm" name="maxPrice" 
                                               placeholder="Đến" value="${maxPrice}">
                                    </div>
                                </div>
                            </div>

                            <div class="mb-3">
                                <label class="form-label fw-bold small text-uppercase">Danh mục</label>
                                <select class="form-select form-select-sm" name="categoryId">
                                    <option value="">Tất cả danh mục</option>
                                    <c:forEach var="cat" items="${categories}">
                                        <option value="${cat.id}" ${categoryId == cat.id ? 'selected' : ''}>${cat.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="mb-4">
                                <label class="form-label fw-bold small text-uppercase">Tác giả</label>
                                <select class="form-select form-select-sm" name="authorId">
                                    <option value="">Tất cả tác giả</option>
                                    <c:forEach var="auth" items="${authors}">
                                        <option value="${auth.id}" ${authorId == auth.id ? 'selected' : ''}>${auth.name}</option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="d-grid gap-2">
                                <button type="submit" class="btn btn-primary">Lọc kết quả</button>
                                <a href="${pageContext.request.contextPath}/books" class="btn btn-light btn-sm text-muted">Xóa bộ lọc</a>
                            </div>
                        </form>
                    </div>
                </div>
            </div>

            <div class="col-lg-9">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <p class="mb-0 text-muted">Tìm thấy <strong>${fn:length(books)}</strong> cuốn sách</p>
                    <div class="d-flex align-items-center">
                        <span class="me-2 small text-muted">Sắp xếp:</span>
                        <select class="form-select form-select-sm" style="width: auto;">
                            <option>Mới nhất</option>
                            <option>Giá tăng dần</option>
                            <option>Giá giảm dần</option>
                        </select>
                    </div>
                </div>

                <c:if test="${empty books}">
                    <div class="text-center py-5 shadow-sm bg-white rounded">
                        <i class="fas fa-search fa-3x text-light mb-3"></i>
                        <p class="text-muted">Không tìm thấy sách phù hợp với bộ lọc.</p>
                    </div>
                </c:if>

                <div class="row g-4">
                    <c:forEach var="book" items="${books}">
                        <div class="col-md-6 col-xl-4">
                            <div class="card h-100 shadow-sm book-card">
                                <div class="book-image-wrapper position-relative overflow-hidden" style="height: 300px;">
                                    <c:choose>
                                        <c:when test="${not empty book.images}">
                                            <img src="${book.images[0].url}" class="w-100 h-100" alt="${book.title}">
                                        </c:when>
                                        <c:otherwise>
                                            <img src="${pageContext.request.contextPath}/images/default-book.png" class="w-100 h-100" alt="No image">
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <div class="book-overlay d-flex align-items-center justify-content-center position-absolute w-100 h-100 top-0 start-0 opacity-0 bg-dark bg-opacity-50 transition-all">
                                        <a href="${pageContext.request.contextPath}/books/${book.id}" class="btn btn-light btn-sm fw-bold me-2">
                                            <i class="fas fa-eye me-1"></i>Chi tiết
                                        </a>
                                    </div>
                                </div>

                                <div class="card-body d-flex flex-column">
                                    <h6 class="fw-bold text-dark text-truncate mb-1">${book.title}</h6>
                                    <p class="small text-primary mb-2">${book.authorName}</p>
                                    <p class="text-muted small text-truncate-2 mb-3">${book.description}</p>
                                    
                                    <div class="mt-auto">
                                        <div class="d-flex justify-content-between align-items-center">
                                            <span class="h5 mb-0 text-danger fw-bold">
                                                <fmt:formatNumber value="${book.price}" pattern="#,###"/>₫
                                            </span>
                                            <c:if test="${book.stock <= 5 and book.stock > 0}">
                                                <span class="badge bg-warning text-dark small">Chỉ còn ${book.stock}</span>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>

                                <div class="card-footer bg-white border-0 pb-3">
                                    <form action="${pageContext.request.contextPath}/cart/add" method="post">
                                        <input type="hidden" name="bookId" value="${book.id}">
                                        <input type="hidden" name="quantity" value="1">
                                        <button type="submit" class="btn btn-primary btn-sm w-100 shadow-sm" ${book.stock == 0 ? 'disabled' : ''}>
                                            <i class="fas fa-cart-plus me-1"></i>
                                            ${book.stock == 0 ? 'Hết hàng' : 'Thêm vào giỏ'}
                                        </button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>