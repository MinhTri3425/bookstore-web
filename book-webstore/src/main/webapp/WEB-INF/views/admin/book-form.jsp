<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>

<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${empty book.id ? 'Thêm Sách Mới' : 'Sửa Sách'} - Admin</title>
    
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/admin.css">
</head>
<body>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark shadow-sm">
        <div class="container-fluid">
            <a class="navbar-brand fw-bold" href="${pageContext.request.contextPath}/admin/dashboard">
                <i class="fas fa-cog me-2"></i> Admin Panel
            </a>
            <div class="collapse navbar-collapse" id="navbarNav">
                <ul class="navbar-nav me-auto">
                    <li class="nav-item"><a class="nav-link" href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                    <li class="nav-item"><a class="nav-link active" href="${pageContext.request.contextPath}/admin/books">Quản lý sách</a></li>
                </ul>
            </div>
        </div>
    </nav>

    <div class="container-fluid py-4">
        <div class="row">
            <div class="col-md-3">
                <div class="card shadow-sm border-0">
                    <div class="card-header bg-white py-3 border-0">
                        <h5 class="mb-0 fw-bold">Menu Quản lý</h5>
                    </div>
                    <div class="list-group list-group-flush sidebar-nav">
                        <a href="${pageContext.request.contextPath}/admin/dashboard" class="list-group-item list-group-item-action border-0 px-4 py-3">
                            <i class="fas fa-tachometer-alt me-2"></i> Dashboard
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/books" class="list-group-item list-group-item-action border-0 px-4 py-3">
                            <i class="fas fa-book me-2"></i> Quản lý sách
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/books/add" class="list-group-item list-group-item-action border-0 px-4 py-3 active">
                            <i class="fas fa-plus me-2"></i> Thêm sách mới
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/authors" class="list-group-item list-group-item-action border-0 px-4 py-3">
                            <i class="fas fa-user me-2"></i> Tác giả
                        </a>
                        <a href="${pageContext.request.contextPath}/admin/categories" class="list-group-item list-group-item-action border-0 px-4 py-3">
                            <i class="fas fa-tags me-2"></i> Danh mục
                        </a>
                    </div>
                </div>
            </div>

            <div class="col-md-9">
                <div class="d-flex justify-content-between align-items-center mb-4">
                    <h2 class="fw-bold">${empty book.id ? 'Thêm Sách Mới' : 'Chỉnh Sửa Sách'}</h2>
                    <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-outline-secondary px-4 shadow-sm">
                        <i class="fas fa-arrow-left me-2"></i>Quay lại
                    </a>
                </div>

                <form class="book-form" method="post" action="${pageContext.request.contextPath}/admin/books/save" enctype="multipart/form-data">
                    <input type="hidden" name="id" value="${book.id}">
                    
                    <div class="row">
                        <div class="col-lg-8">
                            <div class="card shadow-sm border-0 mb-4">
                                <div class="card-header bg-light"><h5 class="mb-0">Thông Tin Cơ Bản</h5></div>
                                <div class="card-body p-4">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Tiêu Đề <span class="text-danger">*</span></label>
                                        <input type="text" class="form-control form-control-lg border-light-subtle shadow-none" 
                                               id="title" name="title" value="${book.title}" placeholder="Nhập tiêu đề sách" required>
                                    </div>

                                    <div class="row g-3 mb-3">
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">ISBN <span class="text-danger">*</span></label>
                                            <input type="text" class="form-control border-light-subtle shadow-none" 
                                                   name="isbn" value="${book.isbn}" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Tác Giả <span class="text-danger">*</span></label>
                                            <select class="form-select border-light-subtle shadow-none" id="authorSelect" name="authorId" required>
                                                <option value="" disabled ${empty book.authorId ? 'selected' : ''}>Chọn tác giả</option>
                                                <c:forEach var="a" items="${authors}">
                                                    <option value="${a.id}" ${a.id == book.authorId ? 'selected' : ''}>${a.name}</option>
                                                </c:forEach>
                                            </select>
                                        </div>
                                    </div>

                                    <div class="mb-3">
                                        <label class="form-label fw-bold">Danh Mục <span class="text-danger">*</span></label>
                                        <select class="form-select border-light-subtle shadow-none" name="categoryId" required>
                                            <option value="" disabled ${empty book.categoryId ? 'selected' : ''}>Chọn danh mục</option>
                                            <c:forEach var="c" items="${categories}">
                                                <option value="${c.id}" ${c.id == book.categoryId ? 'selected' : ''}>${c.name}</option>
                                            </c:forEach>
                                        </select>
                                    </div>

                                    <div class="mb-0">
                                        <label class="form-label fw-bold">Mô Tả</label>
                                        <textarea class="form-control border-light-subtle shadow-none" name="description" rows="4">${book.description}</textarea>
                                    </div>
                                </div>
                            </div>

                            <div class="card shadow-sm border-0 mb-4">
                                <div class="card-header bg-light"><h5 class="mb-0">Giá & Tồn Kho</h5></div>
                                <div class="card-body p-4">
                                    <div class="row g-3">
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Giá Bán (₫) <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control border-light-subtle shadow-none" 
                                                   id="priceInput" name="price" value="${book.price}" step="1000" required>
                                        </div>
                                        <div class="col-md-6">
                                            <label class="form-label fw-bold">Tồn Kho <span class="text-danger">*</span></label>
                                            <input type="number" class="form-control border-light-subtle shadow-none" 
                                                   name="stock" value="${not empty book.stock ? book.stock : 0}" required>
                                        </div>
                                    </div>
                                </div>
                            </div>

                            <div class="card shadow-sm border-0 mb-4">
                                <div class="card-header bg-light"><h5 class="mb-0">Album Hình Ảnh</h5></div>
                                <div class="card-body p-4">
                                    <div class="upload-area border-2 border-dashed p-4 rounded bg-light text-center" id="uploadArea" style="cursor: pointer;">
                                        <i class="fas fa-images fa-2x text-primary mb-2"></i>
                                        <p class="mb-0 fw-bold">Click để chọn nhiều ảnh cho Album</p>
                                        <input type="file" id="extraImages" name="extraImages" accept="image/*" multiple style="display: none;">
                                    </div>
                                    
                                    <div id="imagePreviewGrid" class="row g-2 mt-3"></div>

                                    <c:if test="${not empty book.id and not empty book.images}">
                                        <div class="mt-4">
                                            <p class="small fw-bold text-muted mb-2">Ảnh hiện tại:</p>
                                            <div class="row g-2">
                                                <c:forEach var="img" items="${book.images}">
                                                    <div class="col-md-2 col-4">
                                                        <div class="border rounded p-1">
                                                            <img src="${img.url}" class="img-fluid rounded" style="height: 80px; width: 100%; object-fit: cover;">
                                                        </div>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                            <small class="text-danger mt-2 d-block">* Lưu ý: Chọn ảnh mới sẽ thay thế toàn bộ album cũ.</small>
                                        </div>
                                    </c:if>
                                </div>
                            </div>

                            <div class="d-flex gap-2 mb-5">
                                <button type="submit" class="btn btn-primary btn-lg px-5 shadow-sm">
                                    <i class="fas fa-save me-2"></i>Lưu Sách
                                </button>
                                <a href="${pageContext.request.contextPath}/admin/books" class="btn btn-light btn-lg border px-4">Hủy</a>
                            </div>
                        </div>

                        <div class="col-lg-4">
                            <div class="card border-0 shadow-sm sticky-top" style="top: 20px;">
                                <div class="card-header bg-white py-3"><strong>Xem trước hiển thị</strong></div>
                                <div class="card-body text-center">
                                    <div id="previewCoverContainer" class="mb-3 bg-light rounded d-flex align-items-center justify-content-center" style="height: 250px; overflow: hidden; border: 1px solid #eee;">
                                        <i class="fas fa-book fa-4x text-muted"></i>
                                    </div>
                                    <h5 id="previewTitle" class="text-truncate fw-bold">Tiêu đề sách</h5>
                                    <p id="previewAuthor" class="text-muted small">Tên tác giả</p>
                                    <h4 id="previewPrice" class="text-danger fw-bold">0 ₫</h4>
                                </div>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
    <script>
        // JS của ông được giữ nguyên vì logic DOM không đổi
        const fileInput = document.getElementById('extraImages');
        const uploadArea = document.getElementById('uploadArea');
        const imagePreviewGrid = document.getElementById('imagePreviewGrid');
        const priceInput = document.getElementById('priceInput');
        const titleInput = document.getElementById('title');
        const authorSelect = document.getElementById('authorSelect');

        uploadArea.onclick = () => fileInput.click();

        fileInput.onchange = function() {
            imagePreviewGrid.innerHTML = '';
            const files = Array.from(this.files);
            if (files.length > 0) {
                files.forEach((file, index) => {
                    const reader = new FileReader();
                    reader.onload = (e) => {
                        const col = document.createElement('div');
                        col.className = 'col-md-3 col-6 mb-2';
                        col.innerHTML = `
                            <div class="card border-0 shadow-sm">
                                <img src="\${e.target.result}" class="card-img-top rounded" style="height: 120px; object-fit: cover;">
                                <div class="card-footer bg-white p-1 text-center">
                                    <small class="text-muted small">Ảnh \${index + 1}</small>
                                </div>
                            </div>
                        `;
                        imagePreviewGrid.appendChild(col);
                        if (index === 0) {
                            document.getElementById('previewCoverContainer').innerHTML = 
                                `<img src="\${e.target.result}" style="width: 100%; height: 100%; object-fit: cover;">`;
                        }
                    };
                    reader.readAsDataURL(file);
                });
            }
        };

        titleInput.oninput = function() { 
            document.getElementById('previewTitle').innerText = this.value || 'Tiêu đề sách'; 
        };

        priceInput.oninput = function() {
            const val = parseInt(this.value) || 0;
            document.getElementById('previewPrice').innerText = val.toLocaleString('vi-VN') + ' ₫';
        };

        authorSelect.onchange = function() {
            if(this.selectedIndex > 0) {
                document.getElementById('previewAuthor').innerText = this.options[this.selectedIndex].text;
            }
        };

        window.onload = () => {
            if(titleInput.value) titleInput.oninput();
            if(priceInput.value) priceInput.oninput();
            if(authorSelect.selectedIndex > 0) authorSelect.onchange();

            const firstExistingImg = document.querySelector('.card-body .row.g-2 img');
            if (firstExistingImg) {
                const url = firstExistingImg.getAttribute('src');
                document.getElementById('previewCoverContainer').innerHTML = 
                    `<img src="\${url}" style="width: 100%; height: 100%; object-fit: cover;">`;
            }
        };
    </script>
</body>
</html>