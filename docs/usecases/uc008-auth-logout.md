# Usecase: Đăng xuất

| Thành phần | Nội dung |
| --- | --- |
| **Tên use case** | Đăng xuất |
| **Mã use case** | uc008-auth-logout |
| **Mô tả sơ lược** | Người dùng kết thúc lần đăng nhập hiện tại trên trình duyệt đang sử dụng. |
| **Actor chính** | Khách hàng hoặc Admin |
| **Actor phụ** | Không |
| **Tiền điều kiện (Pre-condition)** | Người dùng đã đăng nhập và nhìn thấy nút Đăng xuất. |
| **Hậu điều kiện (Post-condition)** | Thành công: quyền truy cập của lần đăng nhập hiện tại bị xóa; màn hình Đăng nhập hiển thị “Bạn đã đăng xuất.” Các lần đăng nhập ở trình duyệt khác không thuộc phạm vi thao tác này. |

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| --- | --- |
| 1. Chọn nút Đăng xuất. |  |
|  | 2. Kiểm tra yêu cầu hợp lệ, kết thúc lần đăng nhập hiện tại và chuyển tới Đăng nhập với thông báo “Bạn đã đăng xuất.” |

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
| --- | --- |
| 1.1. Không chọn Đăng xuất và tiếp tục sử dụng chức năng khác. |  |
|  | 1.2. Không thực hiện đăng xuất; kết thúc use case này. |

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| --- | --- |
|  | 2.1.1. Yêu cầu đăng xuất không hợp lệ hoặc biểu mẫu đã hết hiệu lực. |
|  | 2.1.2. Từ chối yêu cầu; không xác nhận đăng xuất thành công. Kết thúc use case; người dùng cần tải lại trang để xác định trạng thái đăng nhập. |
