# Usecase: Đăng nhập hệ thống

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Đăng nhập hệ thống                                                                                                                                                                                                                                 |
| **Mã use case**                    | `uc005-auth-login`                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Người dùng nhập email và mật khẩu để xác thực tài khoản. Hệ thống kiểm tra thông tin đăng nhập, trạng thái hoạt động của tài khoản và cấp quyền truy cập tương ứng theo vai trò (Quản trị viên vào khu vực quản trị, Khách hàng về trang chủ). |
| **Actor chính**                    | Người dùng (Khách vãng lai / Khách hàng / Quản trị viên)                                                                                                                                                                                           |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | Người dùng chưa đăng nhập và đang mở trang đăng nhập của hệ thống.                                                                                                                                                                                 |
| **Hậu điều kiện (Post-condition)** | - Người dùng được xác thực thành công và chuyển đến giao diện tương ứng theo vai trò.<br>- Trường hợp thất bại, hệ thống từ chối truy cập và giữ nguyên trạng thái tài khoản.                                                                    |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                               | Hệ thống                                                                                                                                                                                   |
| ------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1. Người dùng chọn chức năng "Đăng nhập" trên thanh điều hướng website.                                             |                                                                                                                                                                                            |
|                                                                                                                     | 2. Hệ thống hiển thị biểu mẫu đăng nhập bao gồm: trường nhập Email, Mật khẩu và tùy chọn ghi nhớ đăng nhập.                                                                                |
| 3. Người dùng nhập Email và Mật khẩu tài khoản, sau đó nhấn nút "Đăng nhập".                                        |                                                                                                                                                                                            |
|                                                                                                                     | 4. Hệ thống kiểm tra tính hợp lệ của định dạng dữ liệu, đối chiếu thông tin tài khoản và kiểm tra trạng thái hoạt động của tài khoản.                                                      |
|                                                                                                                     | 5. Hệ thống xác thực thành công, cấp quyền truy cập tương ứng theo vai trò (Quản trị viên vào trang quản trị, Khách hàng vào trang chủ) và hiển thị thông báo đăng nhập thành công. |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                   | Hệ thống                                                  |
| ----------------------------------------------------------------------- | --------------------------------------------------------- |
| 3.1. Người dùng nhấn liên kết "Chưa có tài khoản? Đăng ký ngay".        |                                                           |
|                                                                         | 3.2. Hệ thống chuyển sang màn hình đăng ký tài khoản mới. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 4.1.1, 4.2.1, 4.3.1.*

| Actor                                                                                | Hệ thống                                                                                                                                         |
| ------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------ |
| 4.1.1. Email hoặc Mật khẩu bị để trống hoặc không đúng định dạng.                    |                                                                                                                                                  |
|                                                                                      | 4.1.2. Hệ thống hiển thị thông báo lỗi yêu cầu nhập đầy đủ Email và Mật khẩu hợp lệ.                                                             |
|                                                                                      | 4.1.3. Quay lại bước 3 của luồng chính để người dùng nhập lại.                                                                                   |
| 4.2.1. Email hoặc Mật khẩu không chính xác.                                          |                                                                                                                                                  |
|                                                                                      | 4.2.2. Hệ thống hiển thị thông báo lỗi: "Email hoặc mật khẩu không chính xác. Vui lòng thử lại!".                                                 |
|                                                                                      | 4.2.3. Quay lại bước 3 của luồng chính.                                                                                                          |
| 4.3.1. Tài khoản người dùng đang ở trạng thái bị khóa hoặc ngừng hoạt động.          |                                                                                                                                                  |
|                                                                                      | 4.3.2. Hệ thống từ chối đăng nhập và hiển thị thông báo: "Tài khoản của bạn đã bị khóa. Vui lòng liên hệ quản trị viên để được hỗ trợ!".         |
|                                                                                      | 4.3.3. Quay lại bước 3 của luồng chính.                                                                                                          |
