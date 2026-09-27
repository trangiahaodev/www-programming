# Usecase: Khách hàng xem tin tức

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng xem tin tức                                                                                                                                                                                                                             |
| **Mã use case**                    | `uc005d-customer-view-news`                                                                                                                                                                                                                        |
| **Mô tả sơ lược**                  | Khách hàng truy cập để xem danh sách các bài báo, bài viết tin tức mới nhất của cửa hàng mỹ phẩm (xu hướng làm đẹp, kiến thức chăm sóc da, tin tức ưu đãi). Hỗ trợ tìm kiếm bài viết và xem nội dung chi tiết.                                    |
| **Actor chính**                    | Customer (Khách hàng / Khách vãng lai)                                                                                                                                                                                                             |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | Người dùng truy cập vào website của cửa hàng mỹ phẩm.                                                                                                                                                                                              |
| **Hậu điều kiện (Post-condition)** | - Danh sách tin tức và nội dung bài viết được hiển thị đầy đủ, chính xác.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                                 |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                    |
| ------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng chọn mục "Tin tức" trên thanh menu chính của website.                              |                                                                                                                                                             |
|                                                                                                  | 2. Hệ thống truy vấn danh sách các bài viết tin tức làm đẹp mới nhất.                                                                                       |
|                                                                                                  | 3. Hệ thống hiển thị giao diện tin tức bao gồm: bài viết tiêu điểm, danh sách các bài viết mới nhất (ảnh đại diện, tiêu đề, ngày đăng, tóm tắt) và bộ lọc. |
| 4. Khách hàng lướt xem danh sách các bài viết và chọn một bài viết cụ thể để đọc chi tiết.      |                                                                                                                                                             |
|                                                                                                  | 5. Hệ thống hiển thị toàn bộ nội dung chi tiết bài viết (tiêu đề, tác giả, ngày đăng, nội dung, hình ảnh minh họa và các bài viết liên quan).              |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 4.3.*

| Actor                                                                               | Hệ thống                                                                                   |
| ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| 4.1. Khách hàng chọn một chủ đề cụ thể (ví dụ: Chăm sóc da, Xu hướng làm đẹp).      |                                                                                            |
|                                                                                     | 4.2. Hệ thống lọc và hiển thị danh sách các bài viết thuộc chủ đề đã chọn.                 |
| 4.3. Khách hàng nhập từ khóa tìm kiếm bài viết vào ô tìm kiếm và nhấn tìm kiếm.    |                                                                                            |
|                                                                                     | 4.4. Hệ thống lọc và hiển thị danh sách các bài viết có tiêu đề khớp với từ khóa tìm kiếm. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 4.1.1.*

| Actor                                                                        | Hệ thống                                                                                                              |
| ---------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Hệ thống gặp sự cố trong quá trình truy vấn danh sách bài viết.       |                                                                                                                       |
|                                                                              | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải danh sách tin tức lúc này. Vui lòng thử lại sau.".             |
| 4.1.1. Bài viết được chọn không tồn tại hoặc đã bị gỡ khỏi hệ thống.         |                                                                                                                       |
|                                                                              | 4.1.2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy bài viết yêu cầu!" và chuyển người dùng về danh sách tin tức. |
