# Đối chiếu với tài liệu nhóm

Đối chiếu bản `THU THẬP YÊU CẦU - SƠ ĐỒ THIẾT KẾ.md` người dùng cung cấp ngày 27/09/2026. Chỉ bổ sung phần Auth & User Admin; các quy tắc đặt hàng/voucher/báo cáo ngoài module cần chủ sở hữu của từng phần xác nhận.

## Ánh xạ mã use case

| Mã trong bảng tổng hợp nhóm | Đặc tả trong project | Cách ghép |
| --- | --- | --- |
| UC-AUTH-01 | uc006-auth-register | Đăng ký; email chỉ chào mừng, không xác minh |
| UC-AUTH-02 | uc005-auth-login | Đăng nhập bằng email và mật khẩu |
| UC-AUTH-03 | uc008-auth-logout | Bổ sung đặc tả riêng cho đăng xuất; uc008 chưa dùng tại thời điểm tạo |
| UC-AUTH-04 | Cơ chế kiểm tra tài khoản hiện hành | Xem sơ đồ hỗ trợ `auth-current-account.md`; không tự tạo thao tác người dùng mới |
| UC-ADM-USR-01 | uc007d-admin-view-users | Tách mục tiêu xem, tìm kiếm, phân trang |
| UC-ADM-USR-01 | uc007b-admin-update-user | Tách mục tiêu sửa hồ sơ và khóa/mở |
| UC-ADM-USR-01 | uc007c-admin-delete-user | Tách mục tiêu xóa có xác nhận và kiểm tra đơn hàng |
| UC-SYS-01 | Cấu hình khởi tạo Admin | Chức năng vận hành, không nằm trong sáu use case người dùng |

Không đổi mã UC của thành viên khác. Repository hiện có hai file cùng tiền tố `uc002d` cho xem Product ở phía Customer/Admin; đây là bất nhất có sẵn ngoài module này, cần nhóm thống nhất khi lập bảng UC toàn hệ thống.

## Các nội dung cần thống nhất khi ghép

| Nội dung bản nhóm | Hành vi xác nhận từ module | Đề nghị ghi trong báo cáo |
| --- | --- | --- |
| Đăng nhập bằng username/email | Security dùng trường email | Ghi “email và mật khẩu” |
| Admin mặc định được tạo khi chạy lần đầu | Chỉ tạo khi bật bootstrap rõ ràng và cung cấp email/mật khẩu; email đã tồn tại thì bỏ qua | Không ghi có tài khoản/mật khẩu mặc định công khai |
| Guest có thể checkout | Quyền của `/checkout/**`, `/profile/**`, `/orders/**` yêu cầu CUSTOMER | Phần checkout cần thống nhất với nhóm Cart; đây là ràng buộc Security, không chứng minh các màn hình đó đã được triển khai trong nhánh này |
| SYS là actor cho kiểm tra phiên nội bộ | ActiveAccountFilter là thành phần bên trong hệ thống | Nếu biên hệ thống là toàn bộ PinkyCloud, mô tả đây là cơ chế hỗ trợ, không vẽ hệ thống tự làm actor bên ngoài của chính nó |
| Tên khách 2–50 ký tự, điện thoại đúng 10 số | Đăng ký/sửa User: tên bắt buộc tối đa 100; điện thoại sửa tùy chọn tối đa 20 với tập ký tự cho phép | Dùng đúng ràng buộc của User; không áp quy tắc người nhận hàng vào User |
| Quản lý User gộp một dòng | Ba mục tiêu xem, cập nhật, xóa | Giữ nhóm UC-ADM-USR-01 ở bảng tổng quan, liên kết ba đặc tả con |
| Thời gian phản hồi dưới 1,5 giây | Đây là chỉ tiêu trong tài liệu nhóm, không có phép đo của tác vụ tài liệu này | Giữ ở mục yêu cầu phi chức năng; không ghi “đã đạt” |

## Ranh giới hành vi đã đối chiếu

- User có đơn ở **mọi trạng thái**, kể cả hủy, bị chặn tại `existsByUserId`. Không cascade xóa đơn từ User. Khóa ngoại bảo vệ thêm khi có thay đổi đồng thời.
- Chặn tự khóa/xóa và Admin hoạt động cuối cùng. Hai quy tắc độc lập; ở giao diện thông thường, trường hợp chỉ còn một Admin sẽ thường bị chặn bởi quy tắc tự thao tác trước.
- Email chào mừng chạy nền sau khi tài khoản được lưu thành công. Mail tắt, lỗi cấu hình/SMTP hoặc queue đầy không hủy tài khoản; không có cơ chế xác minh, tự retry hoặc bảo đảm người nhận đã đọc email.
- POST login/logout do Spring Security xử lý. Không thêm LoginController xử lý mật khẩu hay LogoutService giả vào Sequence.
- Danh sách trả UserResponseDTO, không trả password và không tải collection orders. Cập nhật không sửa role/password/userCode.
- Khi email/role/password thay đổi hoặc tài khoản bị khóa/xóa, lần truy cập tiếp theo qua bộ kiểm tra sẽ hủy lần đăng nhập cũ. Cập nhật họ tên riêng lẻ không có hiệu ứng này.
- Xóa nghiệp vụ dùng PRG và Flash. Lỗi CSRF/phân quyền bị Security chặn trước Controller, vì vậy không hứa mọi lỗi đều quay về danh sách với Flash.
- Lỗi dữ liệu đồng thời khi đăng ký/cập nhật được Controller xử lý là `DataIntegrityViolationException`. Lỗi khóa khi xóa có xử lý riêng. Không suy diễn mọi sự cố kết nối hoặc mọi lỗi khóa khi cập nhật đều đã có thông báo thân thiện; đó không phải nhánh nghiệp vụ đã được hiện thực đầy đủ.

## Nguồn để kiểm tra lại

- `src/main/java/iuh/wwwprogramming/config/SecurityConfig.java`
- `src/main/java/iuh/wwwprogramming/security/ActiveAccountFilter.java`
- `src/main/java/iuh/wwwprogramming/controller/RegistrationController.java`
- `src/main/java/iuh/wwwprogramming/controller/AdminUserController.java`
- `src/main/java/iuh/wwwprogramming/service/impl/RegistrationServiceImpl.java`
- `src/main/java/iuh/wwwprogramming/service/impl/UserServiceImpl.java`
- `src/main/java/iuh/wwwprogramming/event/WelcomeMailListener.java`
- `src/main/java/iuh/wwwprogramming/service/impl/WelcomeMailService.java`
- `src/main/resources/static/js/admin-users.js`

Không sao chép cấu hình máy cá nhân khi đối chiếu.
