<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm sticky-top">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-user-shield me-2"></i>Admin Panel
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar" 
                aria-controls="adminNavbar" aria-expanded="false" aria-label="Toggle navigation">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="adminNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                <li class="nav-item">
                    <a class="nav-link ${pageTitle == 'Dashboard' ? 'active' : ''}" 
                       href="${pageContext.request.contextPath}/admin/dashboard">
                        <i class="fas fa-tachometer-alt me-1"></i> Dashboard
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link ${pageTitle == 'Order Management' ? 'active' : ''}" 
                       href="${pageContext.request.contextPath}/admin/orders">
                        <i class="fas fa-clipboard-list me-1"></i> Đơn hàng
                    </a>
                </li>

                <li class="nav-item">
                    <a class="nav-link ${pageTitle == 'Shipping Monitor' ? 'active' : ''}" 
                       href="${pageContext.request.contextPath}/admin/shipping">
                        <i class="fas fa-truck-moving me-1"></i> Vận chuyển
                    </a>
                </li>

                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="catalogDropdown" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                        <i class="fas fa-book-open me-1"></i> Kho sách
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark shadow" aria-labelledby="catalogDropdown">
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/books">
                                <i class="fas fa-book me-2"></i> Quản lý sách
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/authors">
                                <i class="fas fa-pen-nib me-2"></i> Tác giả
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/categories">
                                <i class="fas fa-tags me-2"></i> Danh mục
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item" href="${pageContext.request.contextPath}/admin/shippers">
                                <i class="fas fa-user-check me-2"></i> Quản lý Shipper
                            </a>
                        </li>
                    </ul>
                </li>
            </ul>

            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item">
                    <a class="nav-link text-info" href="${pageContext.request.contextPath}/" target="_blank">
                        <i class="fas fa-external-link-alt me-1"></i> Trang chủ
                    </a>
                </li>
                
                <li class="nav-item">
                    <a class="nav-link ${pageTitle == 'Profile' ? 'active' : ''}" href="${pageContext.request.contextPath}/profile">
                        <i class="fas fa-id-card me-1"></i> Hồ sơ
                    </a>
                </li>

                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle" href="#" id="userDropdown" role="button" data-bs-toggle="dropdown">
                        <i class="fas fa-user-circle fs-5"></i>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end dropdown-menu-dark shadow">
                        <li class="dropdown-header text-info text-uppercase small">Quản trị viên</li>
                        <li><a class="dropdown-item" href="${pageContext.request.contextPath}/profile"><i class="fas fa-cog me-2"></i>Thiết lập</a></li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="dropdown-item text-danger">
                                    <i class="fas fa-sign-out-alt me-2"></i>Đăng xuất
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>