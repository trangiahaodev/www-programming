# Usecase: Quản lý giỏ hàng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản lý giỏ hàng                                                                                                                                                                                                                                   |
| **Mã use case**                    | `uc002-shopping-cart`                                                                                                                                                                                                                              |
| **Mô tả sơ lược**                  | Khách hàng xem giỏ hàng, thêm sản phẩm vào giỏ, điều chỉnh số lượng hoặc xóa sản phẩm khỏi giỏ hàng. Hệ thống tự động kiểm tra tồn kho và tính toán lại thành tiền của từng món cũng như tổng tiền tạm tính của giỏ hàng.                               |
| **Actor chính**                    | Customer (Khách hàng / Khách vãng lai)                                                                                                                                                                                                             |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | Khách hàng đang truy cập vào website cửa hàng mỹ phẩm.                                                                                                                                                                                             |
| **Hậu điều kiện (Post-condition)** | - Danh sách sản phẩm trong giỏ hàng được cập nhật tương ứng với thao tác của khách hàng.<br>- Số lượng sản phẩm và tổng tiền tạm tính của giỏ hàng được tính toán lại chính xác.<br>- Chưa phát sinh đơn hàng hay thay đổi dữ liệu mua bán chính thức. |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                              | Hệ thống                                                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng chọn sản phẩm, nhập số lượng mong muốn và nhấn nút "Thêm vào giỏ hàng".                              |                                                                                                                                                      |
|                                                                                                                    | 2. Hệ thống kiểm tra sản phẩm đang mở bán và số lượng yêu cầu không vượt quá số lượng tồn kho.                                                       |
|                                                                                                                    | 3. Hệ thống thêm sản phẩm vào giỏ hàng (cộng dồn số lượng nếu đã có), tự động tính lại tổng tiền tạm tính và thông báo: "Thêm vào giỏ thành công!". |
| 4. Khách hàng mở xem màn hình giỏ hàng.                                                                            |                                                                                                                                                      |
|                                                                                                                    | 5. Hệ thống hiển thị thông tin giỏ hàng gồm: danh sách sản phẩm, đơn giá, số lượng, thành tiền từng món, tổng tiền tạm tính và nút thanh toán.       |
| 6. Khách hàng thay đổi số lượng của một món hàng trong giỏ (số lượng lớn hơn 0) và nhấn cập nhật.                 |                                                                                                                                                      |
|                                                                                                                    | 7. Hệ thống kiểm tra số lượng hợp lệ, cập nhật số lượng và tự động tính lại tổng tiền tạm tính của toàn bộ giỏ hàng.                                 |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 5.1, 6.1, 6.3.*

| Actor                                                            | Hệ thống                                                                                               |
| ---------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------ |
| 5.1. Giỏ hàng hiện tại đang trống.                               |                                                                                                        |
|                                                                  | 5.2. Hệ thống hiển thị thông báo "Giỏ hàng của bạn đang trống" kèm nút "Tiếp tục mua sắm".              |
| 6.1. Khách hàng nhấn nút "Xóa" tại một món hàng trong giỏ hàng. |                                                                                                        |
|                                                                  | 6.2. Hệ thống xóa món hàng khỏi giỏ, tự động tính lại tổng tiền tạm tính và cập nhật hiển thị giỏ hàng. |
| 6.3. Khách hàng nhấn nút "Xóa toàn bộ giỏ hàng".                 |                                                                                                        |
|                                                                  | 6.4. Hệ thống làm trống toàn bộ các món trong giỏ hàng và hiển thị giao diện giỏ hàng trống.           |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 2.2.1, 7.1.1, 7.2.1.*

| Actor                                                                               | Hệ thống                                                                                                                |
| ----------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Sản phẩm yêu cầu thêm vào giỏ không tồn tại hoặc đã ngừng kinh doanh.        |                                                                                                                         |
|                                                                                     | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Sản phẩm không hợp lệ hoặc đã ngừng kinh doanh!".                              |
| 2.2.1. Số lượng thêm vào giỏ vượt quá số lượng hàng tồn kho của sản phẩm.           |                                                                                                                         |
|                                                                                     | 2.2.2. Hệ thống hiển thị thông báo lỗi: "Số lượng sản phẩm trong kho không đủ đáp ứng!".                                |
| 7.1.1. Số lượng cập nhật không hợp lệ (nhỏ hơn hoặc bằng 0).                        |                                                                                                                         |
|                                                                                     | 7.1.2. Hệ thống hiển thị thông báo lỗi: "Số lượng sản phẩm phải lớn hơn 0!" và giữ nguyên số lượng cũ của món hàng.    |
| 7.2.1. Số lượng cập nhật mới vượt quá số lượng hàng tồn trong kho.                  |                                                                                                                         |
|                                                                                     | 7.2.2. Hệ thống hiển thị thông báo lỗi: "Số lượng sản phẩm trong kho không đủ đáp ứng!" và giữ nguyên số lượng ban đầu. |
