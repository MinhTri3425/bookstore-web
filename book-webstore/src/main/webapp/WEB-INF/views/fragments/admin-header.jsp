<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${pageTitle}</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
    <style>
        /* ÉP MENU HIỆN KHI HOVER - KHÔNG CẦN JS */
        @media (min-width: 992px) {
            .navbar-nav .nav-item.dropdown:hover .dropdown-menu {
                display: block !important;
                visibility: visible !important;
                opacity: 1 !important;
                transform: translateY(0) !important;
                transition: all 0.3s ease;
                margin-top: 0;
            }
        }
        .w-20 { width: 20px; text-align: center; }
    </style>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm sticky-top">
    <div class="container-fluid">
        <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
            <i class="fas fa-user-shield text-primary me-2"></i>Admin Panel
        </a>

        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#adminNavbar">
            <span class="navbar-toggler-icon"></span>
        </button>

        <div class="collapse navbar-collapse" id="adminNavbar">
            <ul class="navbar-nav me-auto mb-2 mb-lg-0">
                
                <li class="nav-item">
                    <a class="nav-link ${pageTitle == 'Dashboard' ? 'active' : ''}" 
                       href="${pageContext.request.contextPath}/admin/dashboard">
                        <i class="fas fa-tachometer-alt me-1"></i> Tổng quan
                    </a>
                </li>

                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle ${pageTitle == 'Order Management' || pageTitle == 'Shipping Monitor' || pageTitle == 'Coupon Management' ? 'active' : ''}" 
                       href="javascript:void(0)" id="salesDropdown">
                        <i class="fas fa-shopping-cart me-1"></i> Kinh doanh
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark shadow border-0" aria-labelledby="salesDropdown">
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/orders">
                                <i class="fas fa-clipboard-list me-2 w-20"></i> Quản lý đơn hàng
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/shipping">
                                <i class="fas fa-truck-moving me-2 w-20"></i> Giám sát vận chuyển
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/coupons">
                                <i class="fas fa-ticket-alt me-2 w-20"></i> Mã giảm giá (Coupon)
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item dropdown">
                    <a class="nav-link dropdown-toggle ${pageTitle == 'Book Management' || pageTitle == 'Author Management' || pageTitle == 'Category Management' ? 'active' : ''}" 
                       href="javascript:void(0)" id="catalogDropdown">
                        <i class="fas fa-book-open me-1"></i> Kho sách
                    </a>
                    <ul class="dropdown-menu dropdown-menu-dark shadow border-0" aria-labelledby="catalogDropdown">
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/books">
                                <i class="fas fa-book me-2 w-20"></i> Tất cả sách
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/authors">
                                <i class="fas fa-pen-nib me-2 w-20"></i> Quản lý tác giả
                            </a>
                        </li>
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/admin/categories">
                                <i class="fas fa-tags me-2 w-20"></i> Danh mục sách
                            </a>
                        </li>
                    </ul>
                </li>

                <li class="nav-item">
                    <a class="nav-link ${pageTitle == 'Shipper Management' ? 'active' : ''}" 
                       href="${pageContext.request.contextPath}/admin/shippers">
                        <i class="fas fa-user-check me-1"></i> Đội ngũ Shipper
                    </a>
                </li>
            </ul>

            <ul class="navbar-nav ms-auto align-items-center">
                <li class="nav-item me-3">
                    <a class="btn btn-sm btn-outline-info px-3" href="${pageContext.request.contextPath}/" target="_blank">
                        <i class="fas fa-external-link-alt me-1"></i> Xem trang chủ
                    </a>
                </li>
                
                <li class="nav-item dropdown border-start ps-3">
                    <a class="nav-link dropdown-toggle d-flex align-items-center" href="javascript:void(0)" id="userDropdown">
                        <i class="fas fa-user-circle fs-4 me-2 text-primary"></i>
                        <span class="d-none d-sm-inline fw-bold text-light">
                            <sec:authentication property="principal.username" />
                        </span>
                    </a>
                    <ul class="dropdown-menu dropdown-menu-end dropdown-menu-dark shadow border-0">
                        <li class="dropdown-header text-info text-uppercase small">Tài khoản Quản trị</li>
                        <li>
                            <a class="dropdown-item py-2" href="${pageContext.request.contextPath}/profile">
                                <i class="fas fa-id-card me-2"></i> Hồ sơ cá nhân
                            </a>
                        </li>
                        <li><hr class="dropdown-divider"></li>
                        <li>
                            <form action="${pageContext.request.contextPath}/logout" method="post" class="m-0">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <button type="submit" class="dropdown-item text-danger py-2">
                                    <i class="fas fa-sign-out-alt me-2"></i> Đăng xuất hệ thống
                                </button>
                            </form>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </div>
</nav>