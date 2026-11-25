# Usecase: Quản trị viên xem chi tiết và cập nhật trạng thái đơn hàng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                  |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem chi tiết và cập nhật trạng thái đơn hàng                                                                                                                                                                                                                                               |
| **Mã use case**                    | `uc002d-admin-order-detail`                                                                                                                                                                                                                                                                               |
| **Mô tả sơ lược**                  | Quản trị viên xem thông tin đơn hàng và chuyển đơn sang trạng thái hợp lệ tiếp theo. Số lượng sản phẩm và giá trị đơn hàng chỉ được xem, không được chỉnh sửa.                                                                                                                                              |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                     |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                     |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Đơn hàng cần xem đang tồn tại trong hệ thống.                                                                                                                                                                          |
| **Hậu điều kiện (Post-condition)** | - Thông tin chi tiết đơn hàng được hiển thị đầy đủ, chính xác.<br>- Nếu quản trị viên cập nhật trạng thái, trạng thái mới được lưu; khi hủy đơn, tồn kho được hoàn trả theo quy tắc nghiệp vụ.<br>- Số lượng và giá trị đơn hàng không bị thay đổi. |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                               | Hệ thống                                                                                                                                                                                                                             |
| --------------------------------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1. Quản trị viên nhấn nút "Xem chi tiết" tại một đơn hàng trên trang danh sách đơn hàng.                                                            |                                                                                                                                                                                                                                      |
|                                                                                                                                                     | 2. Hệ thống truy vấn và hiển thị thông tin người nhận, thanh toán, trạng thái và danh sách sản phẩm ở chế độ chỉ đọc.                                                                                                                  |
| 3. Quản trị viên chọn một trạng thái tiếp theo được hệ thống cho phép và nhấn "Cập nhật trạng thái".                                                |                                                                                                                                                                                                                                      |
|                                                                                                                                                     | 4. Hệ thống kiểm tra quy tắc chuyển trạng thái tuần tự và lưu trạng thái mới. Nếu trạng thái mới là "Đã hủy", hệ thống hoàn trả tồn kho theo quy tắc hủy đơn.                                                                          |
|                                                                                                                                                     | 5. Hệ thống tải lại chi tiết đơn hàng với trạng thái mới và hiển thị thông báo cập nhật thành công.                                                                                                                                   |
| 6. Quản trị viên tiếp tục theo dõi chi tiết đơn hàng hoặc quay lại danh sách.                                                                       |                                                                                                                                                                                                                                      |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1.*

| Actor                                                               | Hệ thống                                                 |
| ------------------------------------------------------------------- | -------------------------------------------------------- |
| 3.1. Quản trị viên không chọn trạng thái và tiếp tục xem đơn hàng.  |                                                          |
|                                                                     | 3.2. Không có thay đổi nào được lưu vào đơn hàng.        |
| 6.1. Quản trị viên nhấn nút "Quay lại" hoặc liên kết danh sách đơn. |                                                          |
|                                                                     | 6.2. Hệ thống chuyển về màn hình danh sách các đơn hàng. |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 2.1.1, 2.2.1.*

| Actor                                                                                                    | Hệ thống                                                                                                                |
| -------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị.                  |                                                                                                                         |
|                                                                                                          | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                  |
| 2.1.1. Đơn hàng cần xem hoặc cập nhật không tồn tại.                                                     |                                                                                                                         |
|                                                                                                          | 2.1.2. Hệ thống hiển thị thông báo lỗi và chuyển về danh sách đơn hàng.                                                 |
| 3.1.1. Trạng thái được gửi không phải trạng thái kế tiếp hợp lệ hoặc đơn đã ở trạng thái kết thúc.       |                                                                                                                         |
|                                                                                                          | 3.1.2. Hệ thống từ chối cập nhật, giữ nguyên trạng thái hiện tại và hiển thị thông báo lỗi.                              |
| 4.1.1. Lỗi xảy ra khi lưu trạng thái hoặc hoàn trả tồn kho khi hủy đơn.                                  |                                                                                                                         |
|                                                                                                          | 4.1.2. Hệ thống rollback giao dịch, giữ nguyên dữ liệu trước thao tác và hiển thị thông báo lỗi.                          |
