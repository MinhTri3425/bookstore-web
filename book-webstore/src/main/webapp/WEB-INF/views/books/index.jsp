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
</head>
<body>
    <jsp:include page="../fragments/header.jsp" />

    <section class="hero-section text-white py-5 bg-dark">
        <div class="container">
            <h1 class="display-4 fw-bold mb-2 text-white">
                <i class="fas fa-book-open"></i> Danh sách Sách
            </h1>
            <p class="lead text-white-50">Khám phá thế giới sách với hàng ngàn tác phẩm từ các tác giả nổi tiếng</p>
        </div>
    </section>

    <div class="container-fluid py-5">
        <div class="container">
            <div class="row">
                <div class="col-lg-3 mb-4">
                    <div class="card shadow-sm border-0">
                        <div class="card-header bg-primary text-white">
                            <h5 class="mb-0 text-white">
                                <i class="fas fa-filter"></i> Bộ lọc
                            </h5>
                        </div>
                        <div class="card-body">
                            <form method="get" action="${pageContext.request.contextPath}/books" class="filter-form">
                                <div class="mb-3">
                                    <label for="keyword" class="form-label fw-bold">Tìm kiếm</label>
                                    <input type="text" class="form-control" id="keyword" name="keyword" 
                                        placeholder="Tên sách, tác giả..." 
                                        value="${keyword}">
                                </div>

                                <div class="mb-3">
                                    <label class="form-label fw-bold">Khoảng giá</label>
                                    <div class="row g-2">
                                        <div class="col-6">
                                            <input type="number" class="form-control" name="minPrice" 
                                                placeholder="Tối thiểu" 
                                                value="${minPrice}">
                                        </div>
                                        <div class="col-6">
                                            <input type="number" class="form-control" name="maxPrice" 
                                                placeholder="Tối đa" 
                                                value="${maxPrice}">
                                        </div>
                                    </div>
                                </div>

                                <div class="mb-3">
                                    <label for="categoryId" class="form-label fw-bold">Danh mục</label>
                                    <select class="form-select" id="categoryId" name="categoryId">
                                        <option value="">Tất cả danh mục</option>
                                        <c:forEach var="category" items="${categories}">
                                            <option value="${category.id}" 
                                                ${not empty categoryId and categoryId == category.id ? 'selected' : ''}>
                                                ${category.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="mb-3">
                                    <label for="authorId" class="form-label fw-bold">Tác giả</label>
                                    <select class="form-select" id="authorId" name="authorId">
                                        <option value="">Tất cả tác giả</option>
                                        <c:forEach var="author" items="${authors}">
                                            <option value="${author.id}" 
                                                ${not empty authorId and authorId == author.id ? 'selected' : ''}>
                                                ${author.name}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>

                                <div class="d-grid gap-2">
                                    <button type="submit" class="btn btn-primary">
                                        <i class="fas fa-search"></i> Lọc
                                    </button>
                                    <a href="${pageContext.request.contextPath}/books" class="btn btn-outline-secondary">
                                        <i class="fas fa-redo"></i> Đặt lại
                                    </a>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="col-lg-9">
                    <div class="mb-4">
                        <div class="d-flex justify-content-between align-items-center">
                            <h5 class="mb-0">
                                <span class="text-primary">${fn:length(books)}</span> sách tìm thấy
                            </h5>
                            <div class="sort-options">
                                <select class="form-select form-select-sm" style="width: auto;">
                                    <option>Sắp xếp mặc định</option>
                                    <option>Giá: Thấp đến cao</option>
                                    <option>Giá: Cao đến thấp</option>
                                    <option>A - Z</option>
                                </select>
                            </div>
                        </div>
                    </div>

                    <c:if test="${empty books}">
                        <div class="alert alert-info text-center py-5 border-0 shadow-sm">
                            <i class="fas fa-inbox fa-3x mb-3 text-muted"></i>
                            <h5>Không tìm thấy sách</h5>
                            <p class="text-muted">Vui lòng thử lại với các tiêu chí tìm kiếm khác</p>
                            <a href="${pageContext.request.contextPath}/books" class="btn btn-primary">Xem tất cả sách</a>
                        </div>
                    </c:if>

                    <div class="row g-4">
                        <c:forEach var="book" items="${books}">
                            <div class="col-md-6 col-lg-4">
                                <div class="card book-card h-100 shadow-sm border-0">
                                    <div class="book-image-wrapper position-relative overflow-hidden" style="height: 320px;">
                                        <c:choose>
                                            <c:when test="${not empty book.images}">
                                                <img src="${book.images[0].url}" 
                                                     class="card-img-top w-100 h-100" 
                                                     style="object-fit: cover;" 
                                                     alt="Book Cover">
                                            </c:when>
                                            <c:otherwise>
                                                <img src="${pageContext.request.contextPath}/images/default-book.png" 
                                                     class="card-img-top w-100 h-100" 
                                                     style="object-fit: cover;" 
                                                     alt="Default Cover">
                                            </c:otherwise>
                                        </c:choose>
                                        
                                        <div class="book-overlay d-flex align-items-center justify-content-center position-absolute w-100 h-100 top-0 start-0 opacity-0" 
                                             style="background: rgba(0,0,0,0.5); transition: 0.3s;">
                                            <a href="${pageContext.request.contextPath}/books/${book.id}" class="btn btn-light btn-sm me-2 fw-bold">
                                                <i class="fas fa-eye text-primary"></i> Xem
                                            </a>
                                            <button class="btn btn-success btn-sm fw-bold" ${book.stock == 0 ? 'disabled' : ''}>
                                                <i class="fas fa-shopping-cart"></i>
                                            </button>
                                        </div>
                                    </div>

                                    <div class="card-body d-flex flex-column">
                                        <h6 class="card-title fw-bold">
                                            <a href="${pageContext.request.contextPath}/books/${book.id}" class="text-decoration-none text-dark">
                                                ${book.title}
                                            </a>
                                        </h6>
                                        
                                        <p class="card-text text-muted small mb-2">
                                            <i class="fas fa-user me-1 text-primary"></i>
                                            ${book.authorName}
                                        </p>

                                        <p class="card-text text-secondary small text-truncate-2 mb-3" style="display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; height: 3em;">
                                            ${book.description}
                                        </p>

                                        <div class="mt-auto">
                                            <div class="d-flex justify-content-between align-items-center mb-1">
                                                <span class="h5 mb-0 text-danger fw-bold">
                                                    <fmt:formatNumber value="${book.price}" pattern="#,###"/> ₫
                                                </span>
                                                <c:choose>
                                                    <c:when test="${book.stock > 0}">
                                                        <span class="badge bg-success">Còn hàng</span>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="badge bg-danger">Hết hàng</span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </div>
                                            <small class="text-muted">Kho: ${book.stock}</small>
                                        </div>
                                    </div>

                                    <div class="card-footer bg-white border-0 pt-0 pb-3">
                                        <button class="btn btn-primary btn-sm w-100 shadow-sm" ${book.stock == 0 ? 'disabled' : ''}>
                                            <i class="fas fa-shopping-cart me-1"></i> Thêm vào giỏ
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="../fragments/footer.jsp" />

    <style>
        .book-card:hover .book-overlay { opacity: 1 !important; }
        .text-truncate-2 { height: 3rem; }
    </style>
</body>
</html>