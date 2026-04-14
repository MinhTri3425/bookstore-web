<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<footer class="bg-dark text-white py-5 mt-5">
    <div class="container">
        <div class="row">
            <div class="col-md-4 mb-4">
                <h5><i class="fas fa-book"></i> Cửa hàng Sách</h5>
                <p class="text-white-50">Nơi tốt nhất để tìm kiếm những cuốn sách yêu thích của bạn.</p>
            </div>
            <div class="col-md-4 mb-4">
                <h5>Liên kết</h5>
                <ul class="list-unstyled text-white-50">
                    <li><a href="${pageContext.request.contextPath}/" class="text-white-50 text-decoration-none">Trang chủ</a></li>
                    <li><a href="${pageContext.request.contextPath}/books" class="text-white-50 text-decoration-none">Danh sách sách</a></li>
                </ul>
            </div>
            <div class="col-md-4 mb-4">
                <h5>Liên hệ</h5>
                <ul class="list-unstyled text-white-50">
                    <li class="mb-2"><i class="fas fa-phone me-2"></i> 0378.959.263</li>
                    <li class="mb-2"><i class="fas fa-envelope me-2"></i> Hominhtri0605@gmail.com</li>
                </ul>
            </div>
        </div>
    </div>
</footer>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>

<script>
    document.addEventListener('DOMContentLoaded', function() {
        var dropdownElementList = [].slice.call(document.querySelectorAll('.dropdown-toggle'))
        var dropdownList = dropdownElementList.map(function (dropdownToggleEl) {
            return new bootstrap.Dropdown(dropdownToggleEl)
        });
    });
</script>

<c:if test="${not empty pageContext.request.contextPath}">
    <script src="${pageContext.request.contextPath}/js/main.js"></script>
</c:if>