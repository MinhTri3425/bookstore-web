<nav aria-label="breadcrumb" class="bg-light py-2 shadow-sm mb-4">
    <div class="container">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/" class="text-decoration-none">
                    <i class="fas fa-home"></i> Trang chủ
                </a>
            </li>
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/category/${book.categoryId}" class="text-decoration-none">
                    ${book.categoryName}
                </a>
            </li>
            <li class="breadcrumb-item active text-truncate" aria-current="page" style="max-width: 200px;">
                ${book.title}
            </li>
        </ol>
    </div>
</nav>