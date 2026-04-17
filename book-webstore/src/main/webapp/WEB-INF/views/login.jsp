<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng nhập | Book Webstore</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=Lora:ital,wght@0,400;0,600;1,400&family=Mulish:wght@300;400;600&display=swap" rel="stylesheet">
    
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/login.css">
</head>
<body>
    <div class="container d-flex align-items-center justify-content-center min-vh-100">
        <div class="row w-100 justify-content-center">
            <div class="col-12 col-lg-10 col-xl-8">
                <div class="card login-card">
                    <div class="row g-0">
                        <div class="col-md-6 d-none d-md-block bg-image">
                            <div style="background: rgba(44, 62, 80, 0.2); width: 100%; height: 100%;"></div>
                        </div>
                        
                        <div class="col-md-6">
                            <div class="card-body p-4 p-md-5">
                                <div class="text-center mb-4">
                                    <h2 class="brand-text mb-2">Book Webstore</h2>
                                    <p class="text-muted">Chào mừng bạn quay trở lại!</p>
                                </div>

                                <c:if test="${param.error != null}">
                                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                                        <small>Tên đăng nhập hoặc mật khẩu không đúng.</small>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                </c:if>
                                
                                <c:if test="${param.logout != null}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <small>Bạn đã đăng xuất thành công.</small>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                </c:if>
                                <c:if test="${param.registerSuccess == 'true'}">
                                    <div class="alert alert-success alert-dismissible fade show" role="alert">
                                        <small>Đăng ký thành công! Vui lòng đăng nhập.</small>
                                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                                    </div>
                                </c:if>
                                <form action="${pageContext.request.contextPath}/login" method="post">
                                    <div class="form-floating mb-3">
                                        <input type="text" class="form-control" id="username" name="username" placeholder="Tên đăng nhập" required autofocus>
                                        <label for="username">Tên đăng nhập</label>
                                    </div>
                                    
                                    <div class="form-floating mb-4">
                                        <input type="password" class="form-control" id="password" name="password" placeholder="Mật khẩu" required>
                                        <label for="password">Mật khẩu</label>
                                    </div>
                                    
                                    <div class="d-grid mb-4">
                                        <button type="submit" class="btn btn-primary-custom rounded-3">ĐĂNG NHẬP</button>
                                    </div>
                                    
                                    <div class="text-center mt-3">
                                        <a href="#" class="text-decoration-none text-muted"><small>Quên mật khẩu?</small></a>
                                    </div>
                                    <div class="text-center mt-2">
                                        <span class="text-muted"><small>Chưa có tài khoản?</small></span> 
                                        <a href="${pageContext.request.contextPath}/register" class="text-decoration-none fw-bold" style="color: #2c3e50;"><small>Đăng ký ngay</small></a>
                                    </div>

                                    <hr class="my-4">
                                    <div class="text-center mt-3">
                                        <p class="text-muted"><small>Hoặc đăng nhập bằng:</small></p>
                                        <div class="d-grid gap-2">
                                            <a href="/mock-login?provider=google" class="btn btn-outline-danger shadow-sm">
                                                <i class="fab fa-google me-2"></i> Đăng nhập với Google
                                            </a>
                                            <a href="/mock-login?provider=facebook" class="btn btn-outline-primary shadow-sm">
                                                <i class="fab fa-facebook-f me-2"></i> Đăng nhập với Facebook
                                            </a>
                                        </div>
                                    </div>
                                    </form>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>