## 🔑 Hướng dẫn Tạo tài khoản Test và Đăng nhập

Vì hệ thống sử dụng thuật toán mã hóa **BCrypt** để bảo mật, bạn không thể gõ mật khẩu thuần (như `123456`) trực tiếp vào database. Hãy làm theo 2 bước sau để tạo tài khoản và đăng nhập thử nghiệm:

### Thêm dữ liệu tài khoản vào MySQL
Mở công cụ quản lý cơ sở dữ liệu (MySQL Workbench, phpMyAdmin, DBeaver...) của bạn, chọn database `book_webstore` và chạy đoạn lệnh SQL sau để tạo 2 tài khoản mẫu. Cả 2 tài khoản này đều được thiết lập mật khẩu mặc định là `123456`:
```sql
-- Tạo tài khoản Quản trị viên (ADMIN)
INSERT INTO users (email, password, name, phone_number, role) 
VALUES ('admin@gmail.com', '$2a$10$WEc2pPmjsqfugfUbl3OVX.FmS2dr.fvsrGUNs6qUY6uTn9hUG.VAu', 'Quản trị viên', '0123456789', 'ADMIN');

-- Tạo tài khoản Khách hàng (USER)
INSERT INTO users (email, password, name, phone_number, role) 
VALUES ('user@gmail.com', '$2a$10$WEc2pPmjsqfugfUbl3OVX.FmS2dr.fvsrGUNs6qUY6uTn9hUG.VAu', 'Khách hàng', '0987654321', 'USER');
```

### Thêm dữ liệu tài khoản vào MySQL
🛡️ Kiểm tra quyền Admin:
Tài khoản: admin@gmail.com

Mật khẩu: 123456

Kết quả: Sau khi ấn Đăng nhập, hệ thống sẽ tự động chuyển hướng bạn vào trang quản trị: /admin/dashboard.

🛒 Kiểm tra quyền Người dùng (User):
Tài khoản: user@gmail.com

Mật khẩu: 123456

Kết quả: Sau khi ấn Đăng nhập, hệ thống sẽ chuyển hướng bạn về trang chủ mua sắm: /home.

Note: Cái này mới là bản test các role nha ae