# Kiểm tra tài liệu Auth & User Admin

Ngày đối chiếu: 27/09/2026. Nền mã nguồn: `e5206f9`. Phạm vi kiểm tra là tài liệu, không chạy lại kiểm thử ứng dụng.

## Sơ đồ

Mermaid 11 đã parse và dựng thành công 25 sơ đồ: 6 Activity, 6 Sequence chính, 6 kiến trúc, 6 class theo use case và 1 Sequence hỗ trợ kiểm tra tài khoản hiện hành. 13 sơ đồ dùng trong báo cáo được xuất đủ `.mmd`, `.svg`, `.png`.

Kiểm tra tự động đã đạt:

| Use case | Số bước | Đối ứng Activity | Đường đi của sơ đồ |
| --- | --- | --- | --- |
| uc005-auth-login | 12 | Đủ, không trùng mã bước | Mọi nút tới được từ Start và có đường tới End |
| uc006-auth-register | 16 | Đủ, không trùng mã bước | Mọi nút tới được từ Start và có đường tới End |
| uc007b-admin-update-user | 20 | Đủ, không trùng mã bước | Mọi nút tới được từ Start và có đường tới End |
| uc007c-admin-delete-user | 18 | Đủ, không trùng mã bước | Mọi nút tới được từ Start và có đường tới End |
| uc007d-admin-view-users | 14 | Đủ, không trùng mã bước | Mọi nút tới được từ Start và có đường tới End |
| uc008-auth-logout | 6 | Đủ, không trùng mã bước | Mọi nút tới được từ Start và có đường tới End |

Tổng 86 bước. Không có nút Activity chưa khai báo hoặc bị cô lập; các đường quay lại được đối chiếu với số bước nhập liệu. Đã kiểm tra 71 liên kết nội bộ, tất cả tồn tại. Mã Mermaid xuất riêng khớp nguồn Markdown. Đã xem trực tiếp ảnh mẫu đăng nhập, xóa và đăng xuất để kiểm tra chữ và đường nối.

## Bản Word

Đã tạo `.docx` với 27 bảng và 39 phần ảnh nhúng, gồm sáu đặc tả và sơ đồ. Ảnh dài được chia phần có vùng chồng lặp; ảnh SVG đầy đủ được giữ riêng. Đã kiểm tra cấu trúc ZIP/XML, mọi mã use case và nội dung từng bước có trong bảng Word, mọi ảnh nhúng tham chiếu tới file tồn tại trong gói.

Đã xuất PDF bằng Microsoft Word: **55 trang**, đủ 39 phần ảnh. Kiểm tra tự động không có trang trống hoặc khối nội dung vượt biên trang. Đã xem ảnh tổng quan các trang cùng các trang mẫu bảng đặc tả và Sequence; điều chỉnh chia ảnh để tránh một trang chỉ còn phần đuôi rất nhỏ. Khổ giấy: đặc tả A4 dọc, sơ đồ A3 ngang. Khi ghép báo cáo khác vẫn cần cập nhật ngắt trang và số trang theo bố cục chung của nhóm.

Lần xuất qua PowerShell ban đầu gặp lỗi thư viện COM; đã chuyển sang giao tiếp Word trực tiếp và xuất thành công. Không sửa cài đặt Office.

## Giới hạn bàn giao

- Activity được tạo bằng Mermaid, chưa vẽ lại thành dự án Visual Paradigm. Hướng dẫn nhóm yêu cầu bước vẽ lại này khi hoàn thiện báo cáo cuối.
- Không chỉnh mã nguồn ứng dụng hoặc `application.properties`. Không đưa cấu hình máy riêng vào gói tài liệu.
- Không ghi nhận lại các test ứng dụng cũ như kết quả kiểm thử mới của tác vụ tài liệu.
