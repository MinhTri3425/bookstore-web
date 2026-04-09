<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

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

                <li class="nav-item">
                    <a class="nav-link px-3 position-relative" href="${pageContext.request.contextPath}/cart">
                        <i class="fas fa-shopping-cart"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                            ${globalCartCount != null ? globalCartCount : 0}
                        </span>
                    </a>
                </li>

                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item dropdown ms-lg-3">
                        <a class="nav-link dropdown-toggle btn btn-outline-primary btn-sm px-3 text-dark" 
                           href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                            <i class="fas fa-user-circle me-1"></i>
                            <sec:authentication property="principal.username" />
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow">
                            <sec:authorize access="hasRole('ADMIN')">
                                <li><a class="dropdown-item text-danger fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">Quản trị</a></li>
                                <li><hr class="dropdown-divider"></li>
                            </sec:authorize>
                            <li><a class="dropdown-item" href="${pageContext.request.contextPath}/order/history">Đơn hàng</a></li>
                            <li>
                                <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                                    <button type="submit" class="dropdown-item">Đăng xuất</button>
                                </form>
                            </li>
                        </ul>
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