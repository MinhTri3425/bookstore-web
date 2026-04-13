-- Sample data for Book Webstore
-- This file will be automatically executed when Spring Boot starts with ddl-auto=create

-- Insert Authors
INSERT INTO authors (name) VALUES
(N'Dale Carnegie'),
(N'Adam Khoo'),
(N'Stephen Covey'),
(N'Robert Kiyosaki'),
(N'Rosie Nguyễn'),
(N'Yuval Noah Harari'),
(N'James Clear'),
(N'Napoleon Hill');

-- Insert Categories
INSERT INTO categories (name, slug, parent_id) VALUES
(N'Sách Phát Triển Bản Thân', 'phat-trien-ban-than', NULL),
(N'Sách Tư Duy', 'tu-duy', NULL),
(N'Sách Quản Lý Thời Gian', 'quan-ly-thoi-gian', NULL),
(N'Sách Tài Chính', 'tai-chinh', NULL),
(N'Sách Thanh Niên', 'thanh-nien', NULL),
(N'Sách Lịch Sử', 'lich-su', NULL),
(N'Sách Thói Quen', 'thoi-quen', NULL),
(N'Sách Thành Công', 'thanh-cong', NULL);

-- Insert Books
INSERT INTO books (title, isbn, description, price, stock, author_id, category_id) VALUES
(N'Đắc Nhân Tâm', '978-604-1-12345-6', N'Cuốn sách về nghệ thuật giao tiếp và thu phục lòng người của Dale Carnegie. Một tác phẩm kinh điển giúp cải thiện kỹ năng xã hội và thành công trong cuộc sống.', 89000.00, 100, 1, 1),
(N'Tôi Tài Giỏi, Bạn Cũng Thế!', '978-604-1-23456-7', N'Cuốn sách của Adam Khoo về phát triển tư duy và thành công. Hướng dẫn cách thay đổi mindset để đạt được mục tiêu trong cuộc sống.', 95000.00, 20, 2, 2),
(N'7 Thói Quen Của Người Thành Đạt', '978-604-1-34567-8', N'Tác phẩm nổi tiếng của Stephen Covey về 7 thói quen giúp con người đạt được thành công và hạnh phúc lâu dài.', 120000.00, 40, 3, 3),
(N'Cha Giàu Cha Nghèo', '978-604-1-45678-9', N'Cuốn sách kinh điển về giáo dục tài chính của Robert Kiyosaki. Giúp hiểu rõ sự khác biệt giữa tư duy của người giàu và người nghèo.', 110000.00, 80, 4, 4),
(N'Tuổi Trẻ Đáng Giá Bao Nhiêu', '978-604-1-56789-0', N'Tác phẩm của Rosie Nguyễn về giá trị của tuổi trẻ và cách sống có ý nghĩa. Khuyến khích giới trẻ dám nghĩ dám làm.', 85000.00, 60, 5, 5),
(N'Sapiens: Lược Sử Loài Người', '978-604-1-67890-1', N'Cuốn sách của Yuval Noah Harari về lịch sử nhân loại từ thời đồ đá đến thời hiện đại. Nhìn nhận lại lịch sử loài người dưới góc nhìn mới.', 150000.00, 90, 6, 6),
(N'Atomic Habits', '978-604-1-78901-2', N'Cuốn sách của James Clear về sức mạnh của thói quen nhỏ. Hướng dẫn cách xây dựng thói quen tốt và loại bỏ thói quen xấu.', 135000.00, 50, 7, 7),
(N'Nghĩ Giàu & Làm Giàu', '978-604-1-89012-3', N'Tác phẩm kinh điển của Napoleon Hill về nguyên tắc thành công. Dựa trên nghiên cứu của hàng nghìn người thành đạt.', 125000.00, 50, 8, 8);

-- Insert Inventory for books
-- INSERT INTO inventory (book_id, quantity) VALUES 
-- (1, 50),
-- (2, 30),
-- (3, 40),
-- (4, 35),
-- (5, 45),
-- (6, 25),
-- (7, 55),
-- (8, 20);

-- Insert Book Images
INSERT INTO book_images (book_id, url, alt_text, sort_order) VALUES
(1, '/images/books/dac-nhan-tam.jpg', N'Đắc Nhân Tâm - Bìa sách', 1),
(2, '/images/books/toi-tai-gioi-ban-cung-the.jpg', N'Tôi Tài Giỏi, Bạn Cũng Thế! - Bìa sách', 1),
(3, '/images/books/7-thoi-quen.jpg', N'7 Thói Quen Của Người Thành Đạt - Bìa sách', 1),
(4, '/images/books/cha-giau-cha-ngheo.jpg', N'Cha Giàu Cha Nghèo - Bìa sách', 1),
(5, '/images/books/tuoi-tre-dang-gia-bao-nhieu.jpg', N'Tuổi Trẻ Đáng Giá Bao Nhiêu - Bìa sách', 1),
(6, '/images/books/sapiens.jpg', N'Sapiens: Lược Sử Loài Người - Bìa sách', 1),
(7, '/images/books/atomic-habits.jpg', N'Atomic Habits - Bìa sách', 1),
(8, '/images/books/nghi-giau-lam-giau.jpg', N'Nghĩ Giàu & Làm Giàu - Bìa sách', 1);

INSERT INTO users (email, password, name, phone_number, role, is_shipper) 
VALUES ('admin@gmail.com', '$2a$10$WEc2pPmjsqfugfUbl3OVX.FmS2dr.fvsrGUNs6qUY6uTn9hUG.VAu', 'Quản trị viên', '0123456789', 'ADMIN', FALSE);

-- Tạo tài khoản Khách hàng (USER)
INSERT INTO users (email, password, name, phone_number, role, is_shipper) 
VALUES ('user@gmail.com', '$2a$10$WEc2pPmjsqfugfUbl3OVX.FmS2dr.fvsrGUNs6qUY6uTn9hUG.VAu', 'Khách hàng', '0987654321', 'USER', FALSE);
