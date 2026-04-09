<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<nav class="navbar navbar-expand-lg navbar-light bg-light shadow-sm sticky-top">
    <div class="container">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/">
            <i class="fas fa-book text-primary me-2"></i>Cửa hàng Sách
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav" 
                aria-controls="navbarNav" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item">
                    <a class="nav-link px-3" href="${pageContext.request.contextPath}/books">
                        <i class="fas fa-th-list me-1"></i>Sách
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link px-3 position-relative" href="${pageContext.request.contextPath}/cart">
                        <i class="fas fa-shopping-cart fs-5"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger" id="cart-count">
                            0
                        </span>
                    </a>
                </li>

                <sec:authorize access="hasRole('ADMIN')">
                    <li class="nav-item">
                        <a class="nav-link px-3 text-danger fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                            <i class="fas fa-user-shield me-1"></i>Quản trị
                        </a>
                    </li>
                </sec:authorize>

                <hr class="d-lg-none my-2 text-dark-50">

                <c:choose>
                    <%-- TRƯỜNG HỢP: ĐÃ ĐĂNG NHẬP --%>
                    <c:when test="${pageContext.request.userPrincipal != null}">
                        <li class="nav-item dropdown ms-lg-3">
                            <a class="nav-link dropdown-toggle btn btn-outline-primary btn-sm px-3 text-dark d-flex align-items-center" 
                               href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                                <i class="fas fa-user-circle fs-5 me-2"></i>
                                <span class="fw-medium">${pageContext.request.userPrincipal.name}</span>
                            </a>
                            <ul class="dropdown-menu dropdown-menu-end shadow border-0" aria-labelledby="userDropdown">
                                <li>
                                    <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/order/history">
                                        <i class="fas fa-history me-2"></i>Lịch sử mua hàng
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                                <li>
                                    <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0 p-0">
                                        <button type="submit" class="dropdown-item text-danger py-2">
                                            <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
                                        </button>
                                    </form>
                                </li>
                            </ul>
                        </li>
                    </c:when>

                    <%-- TRƯỜNG HỢP: CHƯA ĐĂNG NHẬP --%>
                    <c:otherwise>
                        <li class="nav-item ms-lg-3">
                            <a class="btn btn-primary btn-sm px-4 rounded-pill shadow-sm" href="${pageContext.request.contextPath}/login">
                                <i class="fas fa-sign-in-alt me-1"></i>Đăng nhập
                            </a>
                        </li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </div>
    </div>
</nav>