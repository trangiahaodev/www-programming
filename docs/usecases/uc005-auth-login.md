# Usecase: Đăng nhập

| Thành phần | Nội dung |
| --- | --- |
| **Tên use case** | Đăng nhập |
| **Mã use case** | uc005-auth-login |
| **Mô tả sơ lược** | Người dùng đăng nhập bằng email và mật khẩu để sử dụng chức năng phù hợp với vai trò khách hàng hoặc quản trị viên. |
| **Actor chính** | Khách vãng lai có tài khoản |
| **Actor phụ** | Không |
| **Tiền điều kiện (Pre-condition)** | Người dùng có tài khoản đã đăng ký hoặc được quản trị hệ thống khởi tạo; truy cập được màn hình Đăng nhập. |
| **Hậu điều kiện (Post-condition)** | Thành công: người dùng được nhận diện với quyền của tài khoản; khách hàng về trang chủ, quản trị viên vào danh sách người dùng.<br>Thất bại: không cấp quyền đăng nhập mới. |

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| --- | --- |
| 1. Mở chức năng Đăng nhập. |  |
|  | 2. Hiển thị biểu mẫu email và mật khẩu. |
| 3. Nhập email, mật khẩu và chọn Đăng nhập. |  |
|  | 4. Kiểm tra email và mật khẩu, yêu cầu tài khoản đang hoạt động. Khi tài khoản là khách hàng, cho phép đăng nhập và hiển thị trang chủ. |

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
| --- | --- |
| 3.1. Chọn Đăng ký khi chưa có tài khoản. |  |
|  | 3.2. Mở biểu mẫu Đăng ký; kết thúc use case Đăng nhập. |
|  | 4.1. Xác định tài khoản hợp lệ có quyền quản trị viên. |
|  | 4.2. Cho phép đăng nhập và hiển thị danh sách người dùng; kết thúc use case. |

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| --- | --- |
|  | 4.1.1. Không tìm thấy email, mật khẩu không đúng hoặc tài khoản đã bị khóa. |
|  | 4.1.2. Hiển thị lại Đăng nhập với thông báo “Email, mật khẩu không đúng hoặc tài khoản đã bị khóa.” Quay lại bước 3. |
|  | 4.2.1. Yêu cầu gửi từ biểu mẫu không còn hợp lệ hoặc không thể xác thực nguồn gửi. |
|  | 4.2.2. Từ chối yêu cầu; không đăng nhập. Người dùng cần mở lại Đăng nhập ở bước 1 trước khi thử lại. |
