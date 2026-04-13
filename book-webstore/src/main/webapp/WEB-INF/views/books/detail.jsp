<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${book.title} - Cửa hàng Sách</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        .cursor-pointer { cursor: pointer; }
        .thumb-container { 
            transition: all 0.2s; 
            border: 2px solid #dee2e6; 
            border-radius: 8px;
            overflow: hidden;
        }
        .thumb-container:hover { border-color: #0d6efd !important; }
        .active-thumb { border-color: #0d6efd !important; box-shadow: 0 0 5px rgba(13, 110, 253, 0.5); }
        
        #bookCarousel { background-color: #fff; }
        .carousel-item img {
            max-height: 500px;
            width: 100%;
            object-fit: contain;
        }
        .carousel-control-prev-icon, .carousel-control-next-icon {
            background-color: rgba(0,0,0,0.3);
            border-radius: 50%;
            background-size: 50%;
            width: 3rem;
            height: 3rem;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/fragments/header.jsp" %>

    <section class="py-5 bg-light">
        <div class="container">
            <div class="row">
                <div class="col-lg-5 mb-4">
                    <div id="bookCarousel" class="carousel slide border rounded shadow-sm" data-bs-ride="false">
                        <div class="carousel-inner p-2">
                            <c:forEach var="image" items="${book.images}" varStatus="iterStat">
                                <div class="carousel-item ${iterStat.first ? 'active' : ''}">
                                    <img src="${image.url}" class="d-block mx-auto" alt="Book Image">
                                </div>
                            </c:forEach>
                            
                            <c:if test="${empty book.images}">
                                <div class="carousel-item active">
                                    <img src="${pageContext.request.contextPath}/images/default-book.png" class="d-block mx-auto" alt="Default Cover">
                                </div>
                            </c:if>
                        </div>

                        <c:if test="${not empty book.images and book.images.size() > 1}">
                            <button class="carousel-control-prev" type="button" data-bs-target="#bookCarousel" data-bs-slide="prev">
                                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                                <span class="visually-hidden">Previous</span>
                            </button>
                            <button class="carousel-control-next" type="button" data-bs-target="#bookCarousel" data-bs-slide="next">
                                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                                <span class="visually-hidden">Next</span>
                            </button>
                        </c:if>
                    </div>

                    <div class="row mt-3 g-2">
                        <c:forEach var="image" items="${book.images}" varStatus="iterStat">
                            <div class="col-3">
                                <div class="thumb-container p-1 cursor-pointer ${iterStat.first ? 'active-thumb' : ''}" 
                                     onclick="goToSlide('${iterStat.index}', this)">
                                    <img src="${image.url}" class="img-fluid rounded" 
                                         style="height: 70px; width: 100%; object-fit: cover;">
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="col-lg-7">
                    <div class="ps-lg-4">
                        <h1 class="fw-bold mb-3">${book.title}</h1>

                        <div class="mb-3">
                            <span class="text-muted"><i class="fas fa-user me-1"></i> Tác giả: </span>
                            <span class="fw-bold">${book.authorName}</span>
                            <span class="mx-2 text-muted">|</span>
                            <span class="text-muted"><i class="fas fa-tag me-1"></i> Danh mục: </span>
                            <span class="fw-bold">${book.categoryName}</span>
                        </div>

                        <div class="price-box p-3 bg-white rounded shadow-sm mb-4">
                            <div class="text-danger h2 fw-bold mb-0">
                                <fmt:formatNumber value="${book.price}" pattern="#,###"/> ₫
                            </div>
                            <small class="text-muted">ISBN: ${book.isbn}</small>
                        </div>

                        <div class="mb-4">
                            <c:choose>
                                <c:when test="${book.stock > 0}">
                                    <span class="badge bg-success p-2 px-3">
                                        <i class="fas fa-check me-1"></i> Còn hàng (${book.stock} cuốn)
                                    </span>
                                </c:when>
                                <c:otherwise>
                                    <span class="badge bg-danger p-2 px-3">
                                        <i class="fas fa-times me-1"></i> Hết hàng
                                    </span>
                                </c:otherwise>
                            </c:choose>
                        </div>

                        <form action="${pageContext.request.contextPath}/cart/add" method="post">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            
                            <input type="hidden" name="bookId" value="${book.id}">
                            <input type="hidden" name="redirectTo" value="/books/${book.id}${not empty pageContext.request.queryString ? '?'.concat(pageContext.request.queryString) : ''}">

                            <div class="d-flex gap-3">
                                <div class="input-group" style="width: 130px;">
                                    <button class="btn btn-outline-secondary" type="button" 
                                            onclick="var input = this.parentNode.querySelector('input'); if(input.value > 1) input.stepDown();">-</button>
                                    
                                    <input type="number" name="quantity" class="form-control text-center" 
                                           value="1" min="1" max="${book.stock}">
                                    
                                    <button class="btn btn-outline-secondary" type="button" 
                                        onclick="var input = this.parentNode.querySelector('input'); 
                                                var stock = parseInt('${book.stock}' || '0'); 
                                                if(parseInt(input.value) < stock) input.stepUp();"> + </button>
                                </div>
                                
                                <button type="submit" class="btn btn-primary btn-lg px-5 flex-grow-1" 
                                        ${book.stock == 0 ? 'disabled' : ''}>
                                    <i class="fas fa-shopping-cart me-2"></i> Thêm vào giỏ hàng
                                </button>
                            </div>
                        </form>
                        </div>
                </div>
            </div>

            <div class="row mt-5">
                <div class="col-12">
                    <div class="card border-0 shadow-sm">
                        <div class="card-body p-4">
                            <h4 class="fw-bold border-bottom pb-3 mb-3">Mô tả sản phẩm</h4>
                            <p class="text-secondary lh-lg" style="white-space: pre-line;">${book.description}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>

    <%@ include file="/WEB-INF/views/fragments/footer.jsp" %>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        function goToSlide(index, element) {
            const myCarouselEl = document.getElementById('bookCarousel');
            const carousel = bootstrap.Carousel.getOrCreateInstance(myCarouselEl);
            carousel.to(index);
        }

        function updateThumbnailActive(index) {
            const thumbnails = document.querySelectorAll('.thumb-container');
            thumbnails.forEach((el, idx) => {
                if(idx == index) {
                    el.classList.add('active-thumb');
                } else {
                    el.classList.remove('active-thumb');
                }
            });
        }

        document.addEventListener('DOMContentLoaded', function() {
            const myCarousel = document.getElementById('bookCarousel');
            if (myCarousel) {
                myCarousel.addEventListener('slid.bs.carousel', function (event) {
                    updateThumbnailActive(event.to);
                });
            }
        });
    </script>
</body>
</html>