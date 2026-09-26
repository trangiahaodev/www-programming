# Auth & User Admin

## Chạy ứng dụng

Giữ Spring Boot và JDK theo `pom.xml`. SQL Server lấy cấu hình từ Spring datasource; ưu tiên biến môi trường `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` khi triển khai.

Tạo Admin đầu tiên bằng cách bật `BOOTSTRAP_ADMIN_ENABLED=true`, đặt `BOOTSTRAP_ADMIN_EMAIL` và `BOOTSTRAP_ADMIN_PASSWORD` (ít nhất 8 ký tự, tối đa 72 byte UTF-8). Khởi động một lần rồi tắt tùy chọn bootstrap. Tài khoản đã tồn tại không bị ghi đè hoặc nâng quyền. Không lưu mật khẩu thật vào Git.

Guest xem catalog và giỏ hàng; Customer dùng checkout/profile/orders; Admin dùng `/admin/**`. Admin đăng nhập được chuyển đến `/admin/users` (được bổ sung trên nhánh danh sách User). Đăng ký được bổ sung trên nhánh `feature/auth-register`.

Email chào mừng: đặt `MAIL_ENABLED=true`, `MAIL_FROM`, `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, `MAIL_STARTTLS`. Mặc định mail bị tắt để môi trường chưa cấu hình SMTP vẫn chạy được. Khi bật, email được gửi sau commit bởi executor hai luồng, hàng đợi tối đa 100; timeout SMTP 5 giây. Lỗi SMTP hoặc hàng đợi đầy được log theo ID User, không gửi lại tự động. Không hứa email đã đến trong Flash message. Email này không phải bước xác minh và không ảnh hưởng quyền đăng nhập.

Mô hình `users`, `orders`, `order_details` kế thừa từ nhánh Cart_Checkout. Không hợp nhất với mô hình OrderItem/customer_name của nhánh admin-order. Khi tích hợp nhánh đó sau này cần thống nhất schema riêng. Không cascade từ User sang Order.

## Kiểm thử

Tạo database SQL Server **riêng** tên `DB_PinkyCloud_Auth_Test` trên localhost:1433, cấp quyền DDL/DML cho tài khoản kiểm thử. Profile `auth-test` sử dụng database này, không dùng DB_PinkyCloud. Có thể truyền username/password kiểm thử qua biến môi trường Spring datasource. Không ghi đè URL thành database ứng dụng khi chạy test.

```powershell
.\mvnw.cmd -B "-Dspring.profiles.active=auth-test" verify
```

Các test cũ về catalog cần seed sản phẩm; profile kiểm thử giữ seed mặc định. Test xác thực tạo tài khoản ngẫu nhiên và rollback. Không sử dụng tài khoản Admin thực để kiểm thử.

## Quyết định tương thích

- Giữ Product của nhánh Customer; DTO Admin ánh xạ price/stock/image vào các trường hiện có. Truy vấn Admin riêng để vẫn xem/sửa sản phẩm ngừng bán.
- Các form xóa Product/Category dùng action JavaScript được bổ sung CSRF thủ công; các form có `th:action` được Thymeleaf thêm token.
- Không thay thế bộ sinh mã sẵn có; mã User mới sử dụng `USR` và 17 ký tự UUID ngẫu nhiên, unique ở database.
- Filter kiểm tra User theo ID ở mỗi request đã đăng nhập; khóa/xóa hoặc thay đổi email/mật khẩu/quyền buộc đăng nhập lại. Đây là chi phí một truy vấn để trạng thái có hiệu lực ngay, không tải danh sách đơn hàng.
