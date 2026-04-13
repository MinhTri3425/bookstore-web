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

                <li class="nav-item me-lg-2">
                    <a class="nav-link px-3 position-relative" href="${pageContext.request.contextPath}/cart">
                        <i class="fas fa-shopping-cart fs-5"></i>
                        <span class="position-absolute top-0 start-100 translate-middle badge rounded-pill bg-danger">
                            ${globalCartCount != null ? globalCartCount : 0}
                        </span>
                    </a>
                </li>

                <sec:authorize access="isAuthenticated()">
                    <li class="nav-item dropdown ms-lg-3">
                        <a class="nav-link dropdown-toggle btn btn-outline-primary btn-sm px-3 text-dark border-0" 
                           href="#" id="userDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                            <i class="fas fa-user-circle fs-5 me-1"></i>
                            <sec:authentication property="principal.username" />
                        </a>
                        <ul class="dropdown-menu dropdown-menu-end shadow border-0 mt-2 p-2" style="min-width: 220px;">
                            
                            <li class="px-3 py-2 border-bottom mb-2 bg-light rounded-top">
                                <span class="small text-muted d-block">Tài khoản của</span>
                                <span class="fw-bold text-dark text-truncate d-block">
                                    <sec:authentication property="principal.username" />
                                </span>
                            </li>

                            <sec:authorize access="hasRole('ADMIN')">
                                <li>
                                    <a class="dropdown-item text-danger fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                                        <i class="fas fa-user-shield me-2"></i>Quản trị hệ thống
                                    </a>
                                </li>
                                <li><hr class="dropdown-divider"></li>
                            </sec:authorize>

                            <sec:authorize access="hasRole('SHIPPER') or hasRole('USER')">
                                <c:if test="${sessionScope.user.isShipper}">
                                    <li>
                                        <a class="dropdown-item fw-bold text-success" href="${pageContext.request.contextPath}/shipper/dashboard">
                                            <i class="fas fa-truck-fast me-2"></i>Nhiệm vụ giao hàng
                                        </a>
                                    </li>
                                    <li><hr class="dropdown-divider"></li>
                                </c:if>
                            </sec:authorize>

                            <li>
                                <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile">
                                    <i class="fas fa-id-card me-2 text-primary"></i> <strong>Thông tin cá nhân</strong>
                                </a>
                            </li>
                            
                            <li>
                                <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/order/history">
                                    <i class="fas fa-history me-2 text-muted"></i>Lịch sử mua hàng
                                </a>
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
