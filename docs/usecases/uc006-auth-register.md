# Usecase: Đăng ký tài khoản

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Đăng ký tài khoản                                                                                                                                                                                                                                  |
| **Mã use case**                    | `uc006-auth-register`                                                                                                                                                                                                                              |
| **Mô tả sơ lược**                  | Người dùng nhập thông tin cá nhân (Họ tên, Email, Số điện thoại, Địa chỉ, Mật khẩu, Xác nhận mật khẩu) để tạo tài khoản mới. Hệ thống kiểm tra dữ liệu hợp lệ, đảm bảo email không bị trùng lặp và tạo tài khoản mới với vai trò Khách hàng. |
| **Actor chính**                    | Guest (Khách vãng lai)                                                                                                                                                                                                                             |
| **Actor phụ**                      | Máy chủ thư điện tử                                                                                                                                                                                                                                |
| **Tiền điều kiện (Pre-condition)** | Người dùng chưa đăng nhập tài khoản vào hệ thống.                                                                                                                                                                                                  |
| **Hậu điều kiện (Post-condition)** | - Tài khoản khách hàng mới được tạo thành công ở trạng thái hoạt động.<br>- Người dùng được chuyển về trang đăng nhập kèm thông báo đăng ký thành công.                                                                                           |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                    | Hệ thống                                                                                                                                                        |
| ------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Người dùng chọn chức năng "Đăng ký" trên thanh điều hướng website.                                                   |                                                                                                                                                                 |
|                                                                                                                          | 2. Hệ thống hiển thị biểu mẫu đăng ký tài khoản (Họ tên, Email, Số điện thoại, Địa chỉ, Mật khẩu, Xác nhận mật khẩu).                                           |
| 3. Người dùng nhập đầy đủ thông tin cá nhân và mật khẩu, sau đó nhấn nút "Đăng ký tài khoản".                           |                                                                                                                                                                 |
|                                                                                                                          | 4. Hệ thống kiểm tra tính hợp lệ của dữ liệu đầu vào và kiểm tra tính duy nhất của Email trên toàn hệ thống.                                                    |
|                                                                                                                          | 5. Hệ thống tạo tài khoản mới ở trạng thái hoạt động với vai trò Khách hàng và gửi thư điện tử chào mừng tới email đăng ký.                                     |
|                                                                                                                          | 6. Hệ thống chuyển người dùng sang màn hình đăng nhập và hiển thị thông báo thành công: "Đăng ký tài khoản thành công! Vui lòng đăng nhập để tiếp tục mua sắm.". |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                   | Hệ thống                                              |
| ----------------------------------------------------------------------- | ----------------------------------------------------- |
| 3.1. Người dùng nhấn liên kết "Đã có tài khoản? Đăng nhập ngay".        |                                                       |
|                                                                         | 3.2. Hệ thống chuyển sang màn hình đăng nhập tài khoản. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 4.1.1, 4.2.1, 5.1.1.*

| Actor                                                                                                                                                     | Hệ thống                                                                                                                                |
| --------------------------------------------------------------------------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1.1. Dữ liệu nhập vào không hợp lệ (Họ tên để trống; Email sai định dạng; Mật khẩu dưới 6 ký tự; Mật khẩu xác nhận không khớp).                         |                                                                                                                                         |
|                                                                                                                                                           | 4.1.2. Hệ thống giữ lại các thông tin đã nhập (ngoại trừ mật khẩu) và hiển thị thông báo lỗi chi tiết tại từng trường dữ liệu vi phạm. |
|                                                                                                                                                           | 4.1.3. Quay lại bước 3 của luồng chính để người dùng điều chỉnh thông tin.                                                              |
| 4.2.1. Email đăng ký đã tồn tại trong hệ thống.                                                                                                           |                                                                                                                                         |
|                                                                                                                                                           | 4.2.2. Hệ thống giữ lại dữ liệu biểu mẫu và hiển thị thông báo lỗi: "Email đã tồn tại trong hệ thống. Vui lòng sử dụng email khác!".     |
|                                                                                                                                                           | 4.2.3. Quay lại bước 3 của luồng chính.                                                                                                 |
| 5.1.1. Gặp sự cố kết nối máy chủ thư điện tử khi gửi email chào mừng.                                                                                    |                                                                                                                                         |
|                                                                                                                                                           | 5.1.2. Hệ thống vẫn hoàn tất tạo tài khoản và ghi nhận nhật ký mà không làm gián đoạn quá trình đăng ký của người dùng.                 |
