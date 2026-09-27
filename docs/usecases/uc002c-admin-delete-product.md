# Usecase: Quản trị viên xóa sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                          |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xóa sản phẩm mỹ phẩm                                                                                                                                                                                                                                                |
| **Mã use case**                    | `uc002c-admin-delete-product`                                                                                                                                                                                                                                                     |
| **Mô tả sơ lược**                  | Quản trị viên thực hiện xóa một sản phẩm mỹ phẩm khỏi hệ thống. Hệ thống yêu cầu xác nhận thao tác xóa, kiểm tra xem sản phẩm có nằm trong bất kỳ đơn hàng nào không trước khi tiến hành xóa để bảo toàn lịch sử giao dịch; nếu không có thì tiến hành xóa và cập nhật danh sách. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                             |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                             |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Sản phẩm mỹ phẩm cần xóa đang tồn tại và hiển thị trên giao diện quản trị.                                                                                                                     |
| **Hậu điều kiện (Post-condition)** | - Sản phẩm mỹ phẩm được xóa hoàn toàn khỏi kho dữ liệu nếu không nằm trong đơn hàng nào.<br>- Hệ thống hiển thị lại danh sách sản phẩm sau khi xóa kèm thông báo kết quả.                                                                                                       |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                 |
| ------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên nhấn nút "Xóa" tại sản phẩm cần xóa trên trang danh sách sản phẩm.              |                                                                                                                                          |
|                                                                                                  | 2. Hệ thống hiển thị hộp thoại cảnh báo và yêu cầu xác nhận thao tác xóa sản phẩm.                                                       |
| 3. Quản trị viên nhấn nút "Xác nhận xóa" trên hộp thoại xác nhận.                                 |                                                                                                                                          |
|                                                                                                  | 4. Hệ thống kiểm tra điều kiện xóa: xác minh sản phẩm hiện tại có nằm trong đơn hàng nào trong lịch sử giao dịch hay không.               |
|                                                                                                  | 5. Hệ thống xác nhận sản phẩm không nằm trong đơn hàng nào, thực hiện xóa sản phẩm khỏi kho dữ liệu hệ thống.                           |
|                                                                                                  | 6. Hệ thống cập nhật lại danh sách sản phẩm và hiển thị thông báo thành công: "Xóa sản phẩm mỹ phẩm thành công!".                         |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                                               | Hệ thống                                                                                           |
| ----------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------- |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp chuột ra ngoài hộp thoại xác nhận.   |                                                                                                    |
|                                                                                     | 3.2. Hệ thống đóng hộp thoại xác nhận, không thực hiện thao tác xóa và giữ nguyên trang danh sách. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 4.1.1, 5.1.1.*

| Actor                                                                                      | Hệ thống                                                                                                                                                                                                       |
| ------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị.    |                                                                                                                                                                                                                |
|                                                                                            | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                                                                                                         |
| 4.1.1. Sản phẩm cần xóa không tồn tại hoặc đã bị xóa trước đó.                             |                                                                                                                                                                                                                |
|                                                                                            | 4.1.2. Hệ thống hiển thị thông báo lỗi: "Không tìm thấy sản phẩm mỹ phẩm cần xóa!" trên trang danh sách.                                                                                                       |
| 4.2.1. Sản phẩm đang nằm trong một hoặc nhiều đơn hàng (vi phạm điều kiện bảo toàn lịch sử giao dịch). |                                                                                                                                                                                                                |
|                                                                                            | 4.2.2. Hệ thống từ chối xóa, giữ nguyên sản phẩm và hiển thị thông báo lỗi: "Không thể xóa sản phẩm vì đã có đơn hàng liên quan đến sản phẩm này! Bạn có thể chuyển trạng thái sản phẩm sang ngưng hoạt động.". |
| 5.1.1. Hệ thống gặp sự cố trong quá trình xóa dữ liệu.                                     |                                                                                                                                                                                                                |
|                                                                                            | 5.1.2. Hệ thống hủy bỏ thao tác xóa và hiển thị thông báo lỗi: "Không thể xóa sản phẩm vào lúc này do lỗi hệ thống. Vui lòng thử lại sau!".                                                                    |
