# Usecase: Quản trị viên xóa danh mục mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                             |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xóa danh mục mỹ phẩm                                                                                                                                                                                                                                   |
| **Mã use case**                    | `uc001c-admin-delete-category`                                                                                                                                                                                                                                       |
| **Mô tả sơ lược**                  | Quản trị viên thực hiện xóa một danh mục mỹ phẩm không còn sử dụng khỏi hệ thống. Hệ thống yêu cầu xác nhận trước khi xóa, kiểm tra danh mục có đang chứa sản phẩm hay không; nếu danh mục trống thì tiến hành xóa và cập nhật lại danh sách danh mục hiển thị. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Danh mục mỹ phẩm cần xóa đang tồn tại và hiển thị trên giao diện quản trị.                                                                                                       |
| **Hậu điều kiện (Post-condition)** | - Danh mục mỹ phẩm được xóa hoàn toàn khỏi hệ thống nếu không chứa sản phẩm.<br>- Hệ thống hiển thị lại danh sách danh mục sau khi xóa kèm thông báo kết quả.                                                                                                       |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                     |
| ------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Xóa" tại danh mục cần xóa trên trang danh sách danh mục.             |                                                                                                                                              |
|                                                                                                  | 2. Hệ thống hiển thị hộp thoại cảnh báo và yêu cầu xác nhận thao tác xóa danh mục.                                                           |
| 3. Quản trị viên nhấn nút "Xác nhận xóa" trên hộp thoại.                                         |                                                                                                                                              |
|                                                                                                  | 4. Hệ thống kiểm tra điều kiện xóa: xác minh danh mục hiện tại có đang chứa sản phẩm liên kết nào hay không.                                 |
|                                                                                                  | 5. Hệ thống xác nhận danh mục không chứa sản phẩm (danh mục trống), thực hiện xóa danh mục khỏi hệ thống.                                    |
|                                                                                                  | 6. Hệ thống cập nhật lại danh sách danh mục và hiển thị thông báo thành công: "Xóa danh mục mỹ phẩm thành công!".                             |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                               | Hệ thống                                                                                               |
| ----------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp chuột ra ngoài hộp thoại xác nhận.   |                                                                                                        |
|                                                                                     | 3.2. Hệ thống đóng hộp thoại xác nhận, không thực hiện thao tác xóa và giữ nguyên trang danh sách.     |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 4.1.1, 5.1.1.*

| Actor                                                                                      | Hệ thống                                                                                                                                                                                   |
| ------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị.    |                                                                                                                                                                                            |
|                                                                                            | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                                                                                     |
| 4.1.1. Danh mục cần xóa không tồn tại hoặc đã bị xóa trước đó.                             |                                                                                                                                                                                            |
|                                                                                            | 4.1.2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy danh mục mỹ phẩm cần xóa!" trên trang danh sách.                                                                                   |
| 4.2.1. Danh mục đang chứa một hoặc nhiều sản phẩm liên kết (danh mục không trống).         |                                                                                                                                                                                            |
|                                                                                            | 4.2.2. Hệ thống từ chối xóa, giữ nguyên danh mục và hiển thị thông báo lỗi: "Không thể xóa danh mục vì đang có sản phẩm thuộc danh mục này! Vui lòng xóa hoặc chuyển sản phẩm sang danh mục khác trước.". |
| 5.1.1. Hệ thống gặp sự cố trong quá trình xóa dữ liệu.                                     |                                                                                                                                                                                            |
|                                                                                            | 5.1.2. Hệ thống hủy bỏ thao tác xóa và hiển thị thông báo lỗi: "Không thể xóa danh mục vào lúc này do lỗi hệ thống. Vui lòng thử lại sau!".                                                |
