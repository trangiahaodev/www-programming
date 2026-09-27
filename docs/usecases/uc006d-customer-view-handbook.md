# Usecase: Khách hàng xem cẩm nang

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng xem cẩm nang                                                                                                                                                                                                                                 |
| **Mã use case**                    | `uc006d-customer-view-handbook`                                                                                                                                                                                                                         |
| **Mô tả sơ lược**                  | Khách hàng truy cập chuyên mục cẩm nang làm đẹp để xem các bài viết hướng dẫn chăm sóc da khoa học, quy trình các bước dưỡng da, thực hiện bài kiểm tra chẩn đoán loại da cá nhân và xem các sản phẩm mỹ phẩm phù hợp được gợi ý.                     |
| **Actor chính**                    | Customer (Khách hàng / Khách vãng lai)                                                                                                                                                                                                                  |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                   |
| **Tiền điều kiện (Pre-condition)** | Người dùng truy cập vào website của cửa hàng mỹ phẩm.                                                                                                                                                                                                   |
| **Hậu điều kiện (Post-condition)** | - Nội dung cẩm nang làm đẹp và kết quả chẩn đoán loại da hiển thị đầy đủ, chính xác.<br>- Dữ liệu hệ thống không bị thay đổi.                                                                                                                           |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                                               |
| ------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng chọn mục "Cẩm nang" trên thanh menu chính của website.                             |                                                                                                                                                                                        |
|                                                                                                  | 2. Hệ thống truy vấn nội dung cẩm nang làm đẹp, quy trình chăm sóc da và bộ câu hỏi chẩn đoán da.                                                                                      |
|                                                                                                  | 3. Hệ thống hiển thị giao diện cẩm nang gồm: bài viết hướng dẫn các bước chăm sóc da, công cụ chẩn đoán loại da và danh sách sản phẩm gợi ý phù hợp cho từng bước.                     |
| 4. Khách hàng xem các bài viết hướng dẫn quy trình chăm sóc da và danh mục mỹ phẩm được khuyên dùng. |                                                                                                                                                                                        |
| 5. Khách hàng nhấn chọn một sản phẩm khuyên dùng trong cẩm nang để xem thông tin chi tiết.       |                                                                                                                                                                                        |
|                                                                                                  | 6. Hệ thống chuyển sang màn hình hiển thị chi tiết của sản phẩm được chọn.                                                                                                             |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 4.1, 4.3.*

| Actor                                                                                                              | Hệ thống                                                                                                                               |
| ------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Khách hàng trả lời các câu hỏi trong phần chẩn đoán loại da cá nhân (da dầu, da khô, da hỗn hợp, nhạy cảm). |                                                                                                                                        |
|                                                                                                                    | 4.2. Hệ thống phân tích kết quả trả lời và hiển thị loại da tương ứng cùng gợi ý quy trình chăm sóc và danh mục mỹ phẩm phù hợp nhất. |
| 4.3. Khách hàng nhấn nút làm lại bài kiểm tra chẩn đoán loại da.                                                   |                                                                                                                                        |
|                                                                                                                    | 4.4. Hệ thống đặt lại trạng thái ban đầu và cho phép khách hàng thực hiện lại bài kiểm tra.                                            |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1.*

| Actor                                                                 | Hệ thống                                                                                                           |
| --------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------ |
| 2.1.1. Hệ thống gặp sự cố trong quá trình truy vấn nội dung cẩm nang. |                                                                                                                    |
|                                                                       | 2.1.2. Hệ thống hiển thị thông báo lỗi: "Không thể tải nội dung cẩm nang lúc này. Vui lòng thử lại sau.".          |
