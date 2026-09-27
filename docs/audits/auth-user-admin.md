# Audit Auth & User Admin

## Kết quả

Đã kiểm tra luồng Controller → DTO → Service → Repository → SQL Server của đăng ký, xác thực, xem/cập nhật/xóa User. Không phát hiện lỗi chặn bàn giao trong phạm vi module này.

| Yêu cầu | Bằng chứng |
| --- | --- |
| Logic nghiệp vụ ở Service | RegistrationServiceImpl và UserServiceImpl kiểm tra email, quyền thay đổi tài khoản và đơn hàng trước khi ghi dữ liệu. |
| Validation hai lớp | Form DTO dùng Jakarta Validation; Controller dùng `@Valid` + BindingResult; Service thay đổi User có method validation. Form login do Spring Security xử lý theo cấu hình, không đi qua Controller nghiệp vụ. |
| Transaction | Các method register/update/delete/bootstrap có `@Transactional`; method đọc dùng readOnly. |
| JPA, không JDBC/native query | Quét source module: dùng JpaRepository, derived query và JPQL; khóa dòng qua `@Lock(PESSIMISTIC_WRITE)`. |
| Bảo vệ đơn hàng | `existsByUserId` không lọc trạng thái, thực thi trước delete; User.orders không cascade delete; FK vẫn là lớp bảo vệ bổ sung. |
| PRG và CSRF | POST xóa redirect với Flash ở cả nhánh thành công, dữ liệu sai, lỗi nghiệp vụ và lỗi FK/khóa. GET không xóa. |
| Phân quyền | Guest chuyển tới login, CUSTOMER nhận 403 ở Admin; khóa/xóa User vô hiệu hóa principal ở request tiếp theo. |
| Email | Sự kiện AFTER_COMMIT chuyển sang bean `@Async` riêng; SMTP chậm không giữ request, lỗi SMTP không rollback tài khoản; rollback không gửi mail. |
| DTO và hiệu suất danh sách | View không nhận Entity/password; mapping danh sách không duyệt hoặc tải User.orders. |
| Modal | Một dialog nằm trong user-list; tên/mã dùng textContent; Hủy/Escape không gửi POST; focus trả về nút mở; chỉ một POST khi xác nhận. |

## Kiểm thử đã thực hiện

- 42 test Maven đạt, không failure/error/skip, trên database riêng `DB_PinkyCloud_Auth_Test`.
- Bao gồm login, CSRF, email trùng/giả role, SMTP chậm/lỗi, rollback, tìm kiếm/phân trang, cập nhật trường cho phép, tự khóa/xóa và Admin cuối cùng.
- Kiểm tra cấm xóa với đơn PENDING, CONFIRMED, SHIPPING, DELIVERED, CANCELLED và CANCELED; dữ liệu đơn và User được giữ nguyên.
- MockMvc kiểm tra nhánh lỗi toàn vẹn dữ liệu bằng mô phỏng FK race; không coi đây là kiểm thử tải đồng thời.
- Hồi quy Customer và CRUD Product/Category: ánh xạ giá/tồn kho/ảnh, xem sản phẩm ngừng bán ở Admin và ẩn khỏi Customer.
- Edge headless: đăng ký → login Customer → 403 Admin → login Admin → tìm/sửa User → Modal Hủy/Escape → xác nhận xóa → Flash một lần. Kiểm tra mobile 390px không tràn ngang.
- Lệnh `./mvnw.cmd -o -B -Dspring.profiles.active=auth-test verify` cuối cùng đạt BUILD SUCCESS: 42 test và đóng gói executable JAR thành công.

## Lỗi đã xử lý

- Hòa giải Product của nhánh Customer với DTO/truy vấn CRUD Admin; tách truy vấn Admin để không ẩn sản phẩm ngừng bán.
- Sửa biểu thức Math.min Integer/Long gây lỗi render danh sách Product.
- Bổ sung CSRF cho form xóa Product/Category có action được đặt bằng JavaScript.
- Sửa email Admin dài gây tràn header trên mobile.
- Sửa import Order mơ hồ trong test và escape biểu thức email bootstrap khi biên dịch.

## Giới hạn và vận hành

- Mail thật cần cấu hình SMTP và `MAIL_ENABLED=true`; kiểm thử dùng mail giả lập, không gửi email ra ngoài.
- Bộ kiểm thử dùng SQL Server local riêng; runner CI cần được cấp SQL Server tương ứng trước khi chạy các integration test.
- Order/OrderDetail giữ schema của Cart_Checkout. OrderDetail là entity kế thừa chưa có business key riêng; không tự đổi schema module Checkout trong Auth. Không hợp nhất mô hình OrderItem của admin-order.
- Hàng đợi email ở bộ nhớ, không có lưu bền hoặc retry tự động; đúng phạm vi email chào mừng đã chốt.
- Các nhánh nối tiếp phụ thuộc nhánh trước; nhánh cuối chứa toàn bộ module. Chưa merge vào dev/main.
