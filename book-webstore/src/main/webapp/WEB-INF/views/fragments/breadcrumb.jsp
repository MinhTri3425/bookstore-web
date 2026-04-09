<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav aria-label="breadcrumb" class="bg-light py-2">
    <div class="container">
        <ol class="breadcrumb mb-0">
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/">Trang chủ</a>
            </li>
            <li class="breadcrumb-item">
                <a href="${pageContext.request.contextPath}/books">Sách</a>
            </li>
            <li class="breadcrumb-item active" aria-current="page">
                ${book.title}
            </li>
        </ol>
    </div>
</nav>