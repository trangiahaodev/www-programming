# Usecase: Đăng ký tài khoản

| Thành phần | Nội dung |
| --- | --- |
| **Tên use case** | Đăng ký tài khoản |
| **Mã use case** | uc006-auth-register |
| **Mô tả sơ lược** | Khách tạo tài khoản khách hàng bằng họ tên, email, mật khẩu và xác nhận mật khẩu; có thể nhận email chào mừng nếu chức năng gửi thư được bật. |
| **Actor chính** | Khách vãng lai |
| **Actor phụ** | Dịch vụ gửi email |
| **Tiền điều kiện (Pre-condition)** | Khách truy cập được website và chưa có tài khoản dùng email đăng ký. |
| **Hậu điều kiện (Post-condition)** | Thành công: tài khoản hoạt động, có mã duy nhất và vai trò khách hàng; có thể đăng nhập ngay, không cần xác minh email.<br>Thất bại khi tạo: không có tài khoản mới. Lỗi gửi thư không làm mất tài khoản đã tạo. |

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| --- | --- |
| 1. Chọn chức năng Đăng ký. |  |
|  | 2. Hiển thị biểu mẫu gồm họ tên, email, mật khẩu và xác nhận mật khẩu. |
| 3. Nhập họ tên, email, mật khẩu, xác nhận mật khẩu và chọn Đăng ký. |  |
|  | 4. Kiểm tra họ tên bắt buộc, tối đa 100 ký tự; email bắt buộc, đúng định dạng, tối đa 100 ký tự và chưa được sử dụng (không phân biệt hoa thường); mật khẩu từ 8 đến 72 ký tự, tối đa 72 byte khi biểu diễn bằng UTF-8, xác nhận phải khớp. Tạo mã người dùng duy nhất, tài khoản hoạt động với vai trò khách hàng; chuyển tới Đăng nhập và báo “Đăng ký thành công. Bạn có thể đăng nhập ngay.” Việc gửi thư chào mừng chỉ bắt đầu sau khi tài khoản được tạo thành công và không bắt khách chờ gửi thư. |

### Luồng sự kiện thay thế (Alternate Flow):

| Actor | Hệ thống |
| --- | --- |
| 3.1. Chọn liên kết Đăng nhập thay vì gửi biểu mẫu. |  |
|  | 3.2. Hiển thị Đăng nhập, không tạo tài khoản; kết thúc use case. |

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| --- | --- |
|  | 4.1.1. Phát hiện dữ liệu bắt buộc bị thiếu, vượt giới hạn, email sai định dạng hoặc mật khẩu xác nhận không khớp. |
|  | 4.1.2. Hiển thị lỗi tại trường tương ứng hoặc lỗi chung; giữ họ tên/email, xóa nội dung hai ô mật khẩu. Quay lại bước 3. |
|  | 4.2.1. Phát hiện email đã được sử dụng, kể cả email chỉ khác chữ hoa/chữ thường. |
|  | 4.2.2. Báo “Email này đã được sử dụng.”; giữ họ tên/email và xóa hai ô mật khẩu. Quay lại bước 3. |
|  | 4.3.1. Phát hiện thông tin xung đột khi hoàn tất tạo tài khoản, chẳng hạn email vừa được người khác đăng ký. |
|  | 4.3.2. Không tạo tài khoản và không gửi thư; báo “Không thể tạo tài khoản với thông tin này. Vui lòng kiểm tra email và thử lại.”; giữ họ tên/email, xóa mật khẩu. Quay lại bước 3. |
|  | 4.4.1. Không gửi được thư chào mừng hoặc không thể tiếp nhận thêm yêu cầu gửi thư sau khi tài khoản đã được tạo. |
|  | 4.4.2. Ghi nhận việc gửi thư không thành công, giữ tài khoản sử dụng bình thường; không thông báo đã gửi thư cho khách. Kết thúc use case. |
|  | 4.5.1. Yêu cầu gửi biểu mẫu không hợp lệ hoặc không thể xác thực nguồn gửi. |
|  | 4.5.2. Từ chối thao tác, không tạo tài khoản và không gửi thư. Kết thúc use case; khách cần mở lại Đăng ký trước khi thử lại. |
