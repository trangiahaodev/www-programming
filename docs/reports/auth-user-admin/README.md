# Tài liệu thiết kế Auth & User Admin

Phạm vi đã chốt: đăng ký, đăng nhập, đăng xuất, xem/tìm kiếm/phân trang, cập nhật và xóa người dùng. Đối chiếu mã nguồn module ở commit `e5206f9`, theo hướng dẫn trong `tai-lieu-thiet-ke.docx` và bố cục mục 8 của tài liệu nhóm.

## File để sử dụng

- [Bản Markdown đầy đủ để ghép vào mục 8](auth-user-admin-design.md).
- [Bản Word gồm đặc tả và hình sơ đồ](auth-user-admin-design.docx).
- [Bản PDF để xem nhanh bố cục](auth-user-admin-design.pdf).
- [Ghi chú đối chiếu tài liệu nhóm](reconciliation.md).
- [Kết quả kiểm tra tài liệu](verification.md).

Các đặc tả nguồn nằm trong `docs/usecases/`; mỗi sơ đồ ba phần nằm trong `docs/diagrams/<feature>.md`; Activity nằm trong `docs/diagrams/activity-<feature>.md`. Thư mục `mermaid/` chứa mã sơ đồ riêng; `assets/` chứa SVG và PNG tương ứng để chèn vào báo cáo. SVG phù hợp khi cần phóng to mà giữ độ nét.

## Ghép vào báo cáo nhóm

1. Dùng các mục **8.2 — Xác thực tài khoản** và **8.3 — Quản lý người dùng** trong bản tổng hợp. Điều chỉnh số mục nếu nhóm đã dành 8.2/8.3 cho thành viên khác; giữ nguyên mã UC.
2. Dán bảng đặc tả theo từng chức năng, tiếp theo là Activity và Sequence. Không thay thế phần 8.1 Quản lý danh mục của thành viên khác.
3. Theo yêu cầu của Word hướng dẫn, dùng Activity Mermaid làm bản đối chiếu rồi **vẽ lại UML Activity trong Visual Paradigm**: hai swimlane Actor/Hệ thống, giữ số bước và nhánh lỗi, sử dụng initial/final node và decision/merge đúng UML. Tác vụ này cung cấp Mermaid và ảnh, chưa tạo file dự án Visual Paradigm.
4. Khi vẽ lại, so với bảng “Đối chiếu bước” cuối từng file Activity. Nhánh hủy phải kết thúc mà không xóa; nhánh sửa lỗi phải trở lại đúng bước nhập liệu; không bỏ ràng buộc đơn hàng.
5. Sequence đã có ảnh và mã `.mmd`; có thể dùng trực tiếp hoặc đưa mã vào công cụ Mermaid của nhóm để đổi bố cục. Bản Word chia sơ đồ dài thành các phần ảnh có vùng chồng lặp để đọc rõ khi in.

Các số tự động trên mũi tên Sequence là thứ tự thông điệp kỹ thuật. Mã bước trong nội dung (ví dụ `4.4.1–4.4.2`) dùng để truy về đặc tả nghiệp vụ; hai hệ đánh số không thay thế nhau.

Không đưa tài khoản, mật khẩu, địa chỉ SMTP cá nhân hoặc cấu hình chạy máy riêng vào tài liệu nộp nhóm.
