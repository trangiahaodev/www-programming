# Usecase: Quản trị viên cập nhật số lượng đơn hàng

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                 |
| ---------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên cập nhật số lượng đơn hàng                                                                                                                                                                                                                                                 |
| **Mã use case**                    | `uc002b-admin-order-update-quantity`                                                                                                                                                                                                                                                     |
| **Mô tả sơ lược**                  | Quản trị viên thay đổi số lượng của một sản phẩm trong một đơn hàng đang ở trạng thái Chờ xử lý. Hệ thống kiểm tra số lượng hợp lệ, kiểm tra tồn kho của sản phẩm, cập nhật số lượng món hàng và tự động tính toán lại thành tiền cũng như tổng tiền thanh toán của toàn bộ đơn hàng. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                    |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                    |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền quản trị viên.<br>- Đơn hàng cần cập nhật đang ở trạng thái "Chờ xử lý" (Pending).                                                                                                                                       |
| **Hậu điều kiện (Post-condition)** | - Số lượng sản phẩm trong đơn hàng được cập nhật thành công.<br>- Tổng tiền đơn hàng được tự động tính toán lại và lưu trữ chính xác trong hệ thống.<br>- Hệ thống hiển thị thông báo cập nhật thành công.                                                                              |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                           | Hệ thống                                                                                                                                  |
| ------------------------------------------------------------------------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên mở màn hình chi tiết của một đơn hàng đang ở trạng thái Chờ xử lý.                                             |                                                                                                                                           |
|                                                                                                                                 | 2. Hệ thống hiển thị danh sách các món hàng kèm trường nhập số lượng cho phép điều chỉnh.                                                 |
| 3. Quản trị viên nhập số lượng mới cho một món hàng (số lượng lớn hơn 0) và nhấn nút "Cập nhật".                                |                                                                                                                                           |
|                                                                                                                                 | 4. Hệ thống kiểm tra đơn hàng đang ở trạng thái Chờ xử lý, kiểm tra số lượng hợp lệ và kiểm tra lượng tồn kho của sản phẩm trong kho.     |
|                                                                                                                                 | 5. Hệ thống cập nhật số lượng món hàng, tự động tính toán lại thành tiền của món hàng và tổng tiền mới của toàn bộ đơn hàng.              |
|                                                                                                                                 | 6. Hệ thống hiển thị lại chi tiết đơn hàng với số liệu đã cập nhật kèm thông báo thành công: "Cập nhật số lượng và tổng tiền thành công!". |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                               | Hệ thống                                                                                   |
| ------------------------------------------------------------------- | ------------------------------------------------------------------------------------------ |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" hoặc không xác nhận cập nhật.  |                                                                                            |
|                                                                     | 3.2. Hệ thống hủy bỏ thay đổi, giữ nguyên số lượng cũ và tải lại màn hình chi tiết đơn.    |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 1.1.1, 4.1.1, 4.2.1, 4.3.1, 5.1.1.*

| Actor                                                                                   | Hệ thống                                                                                                                                            |
| --------------------------------------------------------------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1.1.1. Phiên làm việc của Quản trị viên hết hạn hoặc tài khoản không có quyền quản trị. |                                                                                                                                                     |
|                                                                                         | 1.1.2. Hệ thống từ chối truy cập và yêu cầu người dùng đăng nhập lại tài khoản hợp lệ.                                                               |
| 4.1.1. Đơn hàng không còn ở trạng thái Chờ xử lý (ví dụ: đã chuyển sang Đang giao hàng, Đã hoàn tất hoặc Đã hủy). |                                                                                                                                                     |
|                                                                                         | 4.1.2. Hệ thống từ chối cập nhật và hiển thị thông báo lỗi: "Chỉ được phép thay đổi số lượng đối với đơn hàng đang ở trạng thái Chờ xử lý!".       |
| 4.2.1. Số lượng nhập vào không hợp lệ (nhỏ hơn hoặc bằng 0).                            |                                                                                                                                                     |
|                                                                                         | 4.2.2. Hệ thống hiển thị thông báo lỗi: "Số lượng sản phẩm phải lớn hơn 0!".                                                                        |
|                                                                                         | 4.2.3. Quay lại bước 3 của luồng chính để Quản trị viên nhập lại.                                                                                   |
| 4.3.1. Số lượng yêu cầu vượt quá số lượng hàng tồn kho của sản phẩm.                     |                                                                                                                                                     |
|                                                                                         | 4.3.2. Hệ thống hiển thị thông báo lỗi: "Số lượng sản phẩm trong kho không đủ để đáp ứng yêu cầu!".                                                 |
|                                                                                         | 4.3.3. Quay lại bước 3 của luồng chính để Quản trị viên điều chỉnh lại.                                                                             |
| 5.1.1. Hệ thống gặp sự cố trong quá trình lưu trữ thông tin cập nhật.                    |                                                                                                                                                     |
|                                                                                         | 5.1.2. Hệ thống hủy bỏ thao tác cập nhật, giữ nguyên số liệu cũ và hiển thị thông báo lỗi: "Không thể cập nhật số lượng lúc này. Vui lòng thử lại sau!". |
