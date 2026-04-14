<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<style>
    .navbar { z-index: 1060 !important; }
    /* Tùy chỉnh để các mục nằm ngang đẹp hơn */
    .nav-user-item {
        display: flex;
        align-items: center;
    }
    .logout-button {
        background: none;
        border: none;
        padding: 0;
        cursor: pointer;
    }
    /* Hiệu ứng hover nhẹ cho các icon */
    .nav-link:hover {
        color: #4361ee !important;
    }
</style>

<nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            <i class="fas fa-book text-primary me-2"></i>Cửa hàng Sách
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-center">
                
                <li class="nav-item">
                    <a class="nav-link px-3" href="${pageContext.request.contextPath}/books">Sách</a>
                </li>

                <li class="nav-item me-lg-3">
                    <a class="nav-link px-3 position-relative" href="${pageContext.request.contextPath}/cart">
                        <i class="fas fa-shopping-cart fs-5"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                            ${globalCartCount != null ? globalCartCount : 0}
                        </span>
                    </a>
                </li>

                <sec:authorize access="isAuthenticated()">
                    
                    <sec:authorize access="hasRole('ADMIN')">
                        <li class="nav-item">
                            <a class="nav-link px-3 text-danger fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                                <i class="fas fa-user-shield me-1"></i> Admin
                            </a>
                        </li>
                    </sec:authorize>

                    <c:if test="${sessionScope.user.isShipper}">
                        <li class="nav-item">
                            <a class="nav-link px-3 text-success fw-bold" href="${pageContext.request.contextPath}/shipper/dashboard">
                                <i class="fas fa-truck-fast me-1"></i> Shipper
                            </a>
                        </li>
                    </c:if>

                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/profile">
                            <i class="fas fa-user-circle text-primary me-1"></i> Cá nhân
                        </a>
                    </li>

                    <li class="nav-item">
                        <a class="nav-link px-3" href="${pageContext.request.contextPath}/my-orders">
                            <i class="fas fa-history text-muted me-1"></i> Lịch sử
                        </a>
                    </li>

                    <li class="nav-item ms-lg-2">
                        <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0 p-0">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                            <button type="submit" class="nav-link px-3 logout-button text-danger">
                                <i class="fas fa-sign-out-alt"></i> Thoát
                            </button>
                        </form>
                    </li>

                </sec:authorize>

                <sec:authorize access="!isAuthenticated()">
                    <li class="nav-item ms-lg-3">
                        <a class="btn btn-primary btn-sm px-4 rounded-pill" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
                    </li>
                </sec:authorize>

            </ul>
        </div>
    </div>
</nav>