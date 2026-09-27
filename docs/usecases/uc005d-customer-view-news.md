# Đặc tả Use Case: Khách hàng xem Tin tức & Tạp chí làm đẹp

**Mã Use Case:** `uc005d-customer-view-news`  
**Tên Use Case:** Khách hàng khám phá Tạp chí làm đẹp & Tin tức xu hướng  
**Actor chính:** Khách hàng (Customer), Khách vãng lai (Guest)  
**Phân hệ:** Customer Storefront (Tạp chí làm đẹp PinkyCloud)

---

## 1. Mô tả tóm tắt
Cho phép khách hàng truy cập, tìm kiếm, lọc các bài viết chia sẻ kiến thức làm đẹp, xu hướng mỹ phẩm 2026, các bài viết review chuyên sâu, đồng thời xem các sản phẩm mỹ phẩm chính hãng được gợi ý trực tiếp trong bài viết và đăng ký nhận bản tin định kỳ.

---

## 2. Tiền điều kiện (Pre-conditions)
- Ứng dụng PinkyCloud đang hoạt động.
- Dữ liệu bài viết tin tức và sản phẩm liên quan đã được nạp sẵn sàng trong hệ thống.

---

## 3. Hậu điều kiện (Post-conditions)
- Khách hàng xem được thông tin bài viết chi tiết, danh sách bài viết theo chuyên mục.
- Khách hàng có thể chuyển hướng nhanh sang xem chi tiết sản phẩm liên quan hoặc đăng ký nhận email thành công.

---

## 4. Luồng sự kiện chính (Basic Flow)
1. Khách hàng truy cập đường dẫn `/tin-tuc` từ thanh điều hướng chính.
2. Hệ thống hiển thị bài viết tiêu điểm (Hero Featured Article), thanh lọc danh mục và danh sách các bài viết mới nhất kèm Teaser card.
3. Khách hàng nhấp vào một bài viết bất kỳ (`/tin-tuc/{slug}`).
4. Hệ thống hiển thị giao diện bài viết chuẩn Luxury Editorial gồm:
   - Thanh tiến trình đọc (Reading Progress Bar).
   - Banner Hero ấn tượng kèm metadata và nút chia sẻ mạng xã hội.
   - Thanh chuyển bài viết nhanh (Editorial Series Tracker) cuộn ngang mượt mà.
   - Thân bài viết, Key Takeaways, khối trích dẫn chuyên gia.
   - Khu vực "Mua sản phẩm trong bài" đính kèm thông tin giá, khuyến mãi và tồn kho.
   - Danh sách bài viết liên quan (3-column responsive grid).
5. Khách hàng có thể nhấp vào một sản phẩm gợi ý để chuyển sang trang chi tiết sản phẩm (`/san-pham/{id}`).

---

## 5. Luồng rẽ nhánh (Alternative Flows)
- **A1. Lọc theo chuyên mục:** Khách hàng chọn một danh mục cụ thể (ví dụ: *Chăm sóc da*, *Xu hướng làm đẹp*, *Thành phần dưỡng da*). Hệ thống lọc và tải lại danh sách bài viết tương ứng mà không làm gián đoạn trải nghiệm.
- **A2. Tìm kiếm theo từ khóa:** Khách hàng nhập từ khóa tìm kiếm. Hệ thống trả về các bài viết có tiêu đề hoặc mô tả chứa từ khóa. Nếu không có kết quả, hiển thị Empty State gợi ý thân thiện.
- **A3. Chuyển đổi nhanh qua Series Tracker:** Khách hàng nhấp vào một pill bài viết trong thanh Series Tracker. Hệ thống tải ngay bài viết mới mà không cần quay lại trang danh sách.
- **A4. Đăng ký nhận bản tin:** Khách hàng nhập email tại khối "Nhận bản tin làm đẹp" và bấm gửi. Hệ thống kiểm tra định dạng email và hiển thị Floating Toast xác nhận thành công.

---

## 6. Luồng ngoại lệ (Exception Flows)
- **E1. Không tìm thấy bài viết (`slug` không hợp lệ):** Hệ thống chuyển hướng về `/tin-tuc` kèm thông báo Flash message *"Không tìm thấy bài viết yêu cầu hoặc bài viết đã bị gỡ bỏ."*

---

## 7. Yêu cầu phi chức năng (Non-functional Requirements)
- **Giao diện:** Thiết kế chuẩn Luxury Editorial theo phong cách PinkyCloud (Pastel pink to sky-blue gradient, glassmorphic elements, bo góc mềm mại, font chữ thanh lịch).
- **Hiệu năng:** Thời gian tải trang bài viết < 1 giây; thanh Series Tracker hỗ trợ cảm ứng vuốt mượt mà trên thiết bị di động.
- **Accessibility:** Đầy đủ thẻ `alt` cho ảnh bài viết, `aria-label` cho các nút điều hướng.
