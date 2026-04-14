-- =========================
-- INSERT AUTHORS (15)
-- =========================
INSERT INTO authors (name) VALUES
('Dale Carnegie'),
('Adam Khoo'),
('Stephen Covey'),
('Robert Kiyosaki'),
('Rosie Nguyễn'),
('Yuval Noah Harari'),
('James Clear'),
('Napoleon Hill'),
('Mark Manson'),
('Cal Newport'),
('Daniel Kahneman'),
('Simon Sinek'),
('Tony Robbins'),
('Brian Tracy'),
('Eckhart Tolle');

-- =========================
-- INSERT CATEGORIES (8)
-- =========================
INSERT INTO categories (name, slug, parent_id) VALUES
('Sách Phát Triển Bản Thân', 'phat-trien-ban-than', NULL),
('Sách Tư Duy', 'tu-duy', NULL),
('Sách Quản Lý Thời Gian', 'quan-ly-thoi-gian', NULL),
('Sách Tài Chính', 'tai-chinh', NULL),
('Sách Thanh Niên', 'thanh-nien', NULL),
('Sách Lịch Sử', 'lich-su', NULL),
('Sách Thói Quen', 'thoi-quen', NULL),
('Sách Thành Công', 'thanh-cong', NULL);

-- =========================
-- INSERT BOOKS (15)
-- =========================
INSERT INTO books (title, isbn, description, price, stock, author_id, category_id) VALUES
-- 1 -> 8 (cũ)
('Đắc Nhân Tâm', '978-604-1-12345-6',
 'Cuốn sách về nghệ thuật giao tiếp và thu phục lòng người của Dale Carnegie.',
 89000.00, 100, 1, 1),

('Tôi Tài Giỏi, Bạn Cũng Thế!', '978-604-1-23456-7',
 'Cuốn sách của Adam Khoo về phát triển tư duy và thành công.',
 95000.00, 20, 2, 2),

('7 Thói Quen Của Người Thành Đạt', '978-604-1-34567-8',
 'Tác phẩm nổi tiếng của Stephen Covey về 7 thói quen thành công.',
 120000.00, 40, 3, 3),

('Cha Giàu Cha Nghèo', '978-604-1-45678-9',
 'Cuốn sách kinh điển về giáo dục tài chính.',
 110000.00, 80, 4, 4),

('Tuổi Trẻ Đáng Giá Bao Nhiêu', '978-604-1-56789-0',
 'Tác phẩm truyền cảm hứng cho giới trẻ.',
 85000.00, 60, 5, 5),

('Sapiens: Lược Sử Loài Người', '978-604-1-67890-1',
 'Lịch sử nhân loại từ góc nhìn mới.',
 150000.00, 90, 6, 6),

('Atomic Habits', '978-604-1-78901-2',
 'Sức mạnh của thói quen nhỏ.',
 135000.00, 50, 7, 7),

('Nghĩ Giàu & Làm Giàu', '978-604-1-89012-3',
 'Nguyên tắc thành công từ Napoleon Hill.',
 125000.00, 50, 8, 8),

-- 9 -> 15 (mới)
('Nghệ Thuật Tinh Tế Của Việc Đếch Quan Tâm', '978-604-1-90123-4',
 'Sống thực tế và tập trung vào điều quan trọng.',
 115000.00, 70, 9, 1),

('Deep Work - Làm Ra Làm, Chơi Ra Chơi', '978-604-1-91234-5',
 'Phương pháp làm việc sâu để đạt hiệu suất cao.',
 140000.00, 40, 10, 3),

('Tư Duy Nhanh Và Chậm', '978-604-1-92345-6',
 'Giải thích hai hệ thống tư duy của con người.',
 160000.00, 60, 11, 2),

('Bắt Đầu Với Câu Hỏi Tại Sao', '978-604-1-93456-7',
 'Lãnh đạo bằng cảm hứng.',
 130000.00, 30, 12, 8),

('Đánh Thức Con Người Phi Thường Trong Bạn', '978-604-1-94567-8',
 'Chiến lược phát triển bản thân.',
 150000.00, 45, 13, 8),

('Ăn Con Ếch Đó Đi', '978-604-1-95678-9',
 'Quản lý thời gian hiệu quả.',
 90000.00, 55, 14, 3),

('Sức Mạnh Của Hiện Tại', '978-604-1-96789-0',
 'Sống trọn vẹn trong hiện tại.',
 125000.00, 35, 15, 1);


-- Insert Book Images
INSERT INTO book_images (book_id, url, alt_text, sort_order) VALUES
(1, '/images/books/dac-nhan-tam.jpg', 'Đắc Nhân Tâm - Bìa sách', 1),
(2, '/images/books/toi-tai-gioi-ban-cung-the.jpg', 'Tôi Tài Giỏi, Bạn Cũng Thế! - Bìa sách', 1),
(3, '/images/books/7-thoi-quen.jpg', '7 Thói Quen Của Người Thành Đạt - Bìa sách', 1),
(4, '/images/books/cha-giau-cha-ngheo.jpg', 'Cha Giàu Cha Nghèo - Bìa sách', 1),
(5, '/images/books/tuoi-tre-dang-gia-bao-nhieu.jpg', 'Tuổi Trẻ Đáng Giá Bao Nhiêu - Bìa sách', 1),
(6, '/images/books/sapiens.jpg', 'Sapiens: Lược Sử Loài Người - Bìa sách', 1),
(7, '/images/books/atomic-habits.jpg', 'Atomic Habits - Bìa sách', 1),
(8, '/images/books/nghi-giau-lam-giau.jpg', 'Nghĩ Giàu & Làm Giàu - Bìa sách', 1);

-- Insert Users
INSERT INTO users (email, password, name, phone_number, role, is_shipper) 
VALUES ('admin@gmail.com', '$2a$10$WEc2pPmjsqfugfUbl3OVX.FmS2dr.fvsrGUNs6qUY6uTn9hUG.VAu', 'Quản trị viên', '0123456789', 'ADMIN', FALSE);

INSERT INTO users (email, password, name, phone_number, role, is_shipper) 
VALUES ('user@gmail.com', '$2a$10$WEc2pPmjsqfugfUbl3OVX.FmS2dr.fvsrGUNs6qUY6uTn9hUG.VAu', 'Khách hàng', '0987654321', 'USER', FALSE);