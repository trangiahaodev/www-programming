# Usecase: Đăng nhập và phân quyền

| Thành phần | Nội dung |
| --- | --- |
| Tên use case | Đăng nhập và phân quyền |
| Mô tả sơ lược | Email và mật khẩu hợp lệ; tài khoản hoạt động; ADMIN vào quản trị, CUSTOMER về trang chủ. Khóa/xóa tài khoản vô hiệu hóa phiên đang mở. |
| Actor chính | Guest |
| Actor phụ | Không |
| Tiền điều kiện | Chưa đăng nhập |
| Hậu điều kiện | Thao tác hợp lệ được ghi nhận; nếu thất bại, giữ nguyên dữ liệu và thông báo nguyên nhân. |

### Luồng sự kiện chính (Main flow):
| Actor | Hệ thống |
| --- | --- |
| 1. Mở chức năng đăng nhập và phân quyền. | |
| | 2. Hiển thị thông tin và thao tác phù hợp. |
| 3. Nhập hoặc chọn thông tin, xác nhận thao tác. | |
| | 4. Kiểm tra dữ liệu, quyền truy cập và quy định: Email và mật khẩu hợp lệ; tài khoản hoạt động; ADMIN vào quản trị, CUSTOMER về trang chủ. Khóa/xóa tài khoản vô hiệu hóa phiên đang mở. |
| | 5. Trả kết quả và thông báo rõ ràng. |

### Luồng sự kiện thay thế (Alternate Flow):
| Actor | Hệ thống |
| --- | --- |
| 3.1. Hủy thao tác hoặc trở lại danh sách. | |
| | 3.2. Giữ nguyên dữ liệu. |

### Luồng sự kiện ngoại lệ (Exception Flow):
| Actor | Hệ thống |
| --- | --- |
| 4.1.1. Thông tin không hợp lệ hoặc vi phạm quy định. | |
| | 4.1.2. Hiển thị lỗi; cho phép sửa hoặc quay lại, không thực hiện thao tác. |
| 4.2.1. Phiên hết hạn hoặc không có quyền. | |
| | 4.2.2. Yêu cầu đăng nhập hoặc từ chối truy cập. |

### Hợp đồng triển khai
- Route: `GET/POST /login; POST /logout`; xử lý: `loadUserByUsername(email)`.
- DTO/View fields: Email, mật khẩu, thông báo đăng nhập. Không trả password/hash trong DTO hiển thị.
- Dữ liệu form dùng Jakarta Validation; logic nghiệp vụ ở Service, ghi dữ liệu có transaction.
- Thành công dùng PRG; riêng xóa luôn PRG với Flash. Không xóa qua GET.
