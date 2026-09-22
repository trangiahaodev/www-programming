# Usecase: Khách hàng duyệt danh sách sản phẩm, tìm kiếm và lọc danh mục

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                          |
| ---------------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng duyệt danh sách sản phẩm, tìm kiếm và lọc danh mục                                                                                                                                                                                                                     |
| **Mã use case**                    | `uc002d-customer-view-products`                                                                                                                                                                                                                                                   |
| **Mô tả sơ lược**                  | Khách hàng truy cập trang Danh sách sản phẩm của PinkyCloud để xem các dòng mỹ phẩm chính hãng, tìm kiếm nhanh theo tên sản phẩm / thương hiệu, lọc theo từng danh mục (thông qua thanh cuộn điều hướng mượt hoặc Modal danh bạ A-Z), sắp xếp theo giá / độ phổ biến / mới nhất và phân trang danh sách. |
| **Actor chính**                    | Khách hàng (Customer / Khách vãng lai)                                                                                                                                                                                                                                            |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                             |
| **Tiền điều kiện (Pre-condition)** | Hệ thống đang hoạt động và người dùng có kết nối Internet truy cập vào website PinkyCloud.                                                                                                                                                                                        |
| **Hậu điều kiện (Post-condition)** | - Danh sách sản phẩm được tải và hiển thị chính xác theo các tiêu chí tìm kiếm, lọc danh mục và sắp xếp.<br>- Trạng thái hệ thống và dữ liệu trong Database không bị biến đổi (Thao tác Read-only).                                                                            |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                 | Hệ thống                                                                                                                                                                                                                                                                   |
| ----------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng chọn menu "Sản phẩm" trên thanh điều hướng hoặc truy cập đường dẫn `/san-pham`.         |                                                                                                                                                                                                                                                                            |
|                                                                                                       | 2. Hệ thống tiếp nhận yêu cầu (`GET /san-pham`), truy vấn danh sách danh mục mỹ phẩm hoạt động và phân trang sản phẩm theo trang đầu tiên (mặc định 12 sản phẩm/trang, sắp xếp theo độ bán chạy & thời gian tạo).                                                      |
|                                                                                                       | 3. Hệ thống render giao diện trang sản phẩm (`customer/product-list`) bao gồm: Hero Banner nghệ thuật kèm họa tiết mỹ phẩm `back_nen.png`, Sticky Filter Bar với các nút cuộn `<` `>`, nút mở Modal A-Z, thanh sắp xếp tiêu chí và Lưới sản phẩm (Product Grid 2-4 cột). |
| 4. Khách hàng xem danh sách thẻ sản phẩm (ảnh, thương hiệu, tên, giá bán, giá gốc, badge giảm giá/mới).|                                                                                                                                                                                                                                                                            |
| 5. Khách hàng bấm chọn một danh mục trên thanh lọc hoặc mở Modal Danh bạ danh mục để lọc.             |                                                                                                                                                                                                                                                                            |
|                                                                                                       | 6. Hệ thống lọc danh sách sản phẩm thuộc danh mục được chọn, đưa về trang đầu tiên và cập nhật lại giao diện.                                                                                                                                                             |
| 7. Khách hàng chọn chuyển trang trên thanh phân trang (nếu có nhiều hơn 1 trang).                     |                                                                                                                                                                                                                                                                            |
|                                                                                                       | 8. Hệ thống tải dữ liệu sản phẩm của trang tương ứng và cuộn trang mượt mà lên đầu danh sách.                                                                                                                                                                              |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 4.1.*

| Actor                                                                           | Hệ thống                                                                                                                                                                        |
| ------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Khách hàng nhập từ khóa vào ô tìm kiếm ở Hero Banner và nhấn "Tìm Kiếm".   |                                                                                                                                                                                 |
|                                                                                 | 4.2. Hệ thống lọc sản phẩm khớp theo tên sản phẩm, thương hiệu hoặc mã sản phẩm và hiển thị số lượng kết quả tìm được.                                                          |
|                                                                                 | 4.3. Quay lại bước 4 của luồng chính.                                                                                                                                           |
| 4.4. Không tìm thấy sản phẩm nào khớp với từ khóa hoặc danh mục được chọn.       |                                                                                                                                                                                 |
|                                                                                 | 4.5. Hệ thống hiển thị giao diện Empty State thông báo "Không tìm thấy sản phẩm nào phù hợp", kèm nút "Xem tất cả sản phẩm" để người dùng dễ dàng xóa bộ lọc.                 |
|                                                                                 | 4.6. Quay lại bước 4 của luồng chính khi khách hàng bấm xem lại tất cả.                                                                                                        |
| 5.1. Khách hàng thay đổi tiêu chí sắp xếp (Giá Thấp - Cao, Giá Cao - Thấp, Mới nhất, HOT). |                                                                                                                                                                                 |
|                                                                                 | 5.2. Hệ thống áp dụng Sort parameter tương ứng trong Spring Data JPA (`price ASC/DESC`, `createdAt DESC`, `discount DESC`) và render lại danh sách đã sắp xếp.                |
|                                                                                 | 5.3. Quay lại bước 4 của luồng chính.                                                                                                                                           |
| 5.4. Khách hàng mở Modal Danh bạ danh mục A-Z và gõ tìm kiếm nhanh danh mục.   |                                                                                                                                                                                 |
|                                                                                 | 5.5. JavaScript phía client lọc tức thời các chip danh mục theo thời gian thực (Live Filter), khách hàng click chọn danh mục mong muốn và modal tự động đóng để chuyển hướng. |
|                                                                                 | 5.6. Quay lại bước 6 của luồng chính.                                                                                                                                           |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 8.1.1.*

| Actor                                                                            | Hệ thống                                                                                                                                     |
| -------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Lỗi kết nối cơ sở dữ liệu hoặc hệ thống máy chủ bị gián đoạn.            |                                                                                                                                              |
|                                                                                  | 2.1.2. Hệ thống chuyển hướng tới trang thông báo lỗi thân thiện hoặc hiển thị thông báo "Dịch vụ tạm thời bận, vui lòng thử lại sau ít phút." |
|                                                                                  | 2.1.3. Kết thúc use case.                                                                                                                    |
| 4.1.1. Khách hàng cố tình truyền số trang không hợp lệ (ví dụ: `page=-5` hoặc `page=99999`). |                                                                                                                                              |
|                                                                                  | 4.1.2. Hệ thống tự động chuẩn hóa `pageNumber = Math.max(0, page)` hoặc hiển thị trang rỗng nếu vượt quá tổng số trang.                      |
|                                                                                  | 4.1.3. Quay lại bước 4 của luồng chính.                                                                                                      |

---

## 🛠 Yêu cầu cập nhật Database / Entity / DTO

### 1. Database & Entity: `Product` (Bảng `products`) & `Category` (Bảng `categories`)
- Hỗ trợ quan hệ `@ManyToOne` từ `Product` sang `Category` thông qua `category_id`.
- Tối ưu hóa truy vấn tìm kiếm bằng các chỉ mục composite trên SQL Server:
  - `idx_products_name` (trên cột `name`)
  - `idx_products_brand` (trên cột `brand`)
  - `idx_products_category` (trên cột `category_id`)
- Chỉ lấy các sản phẩm có `is_active = true` khi phục vụ khách hàng.

### 2. DTO: `ProductCardDTO` & `CategoryResponseDTO`
- **`ProductCardDTO`**: Đóng gói thông tin thu gọn tối ưu để tải nhanh danh sách:
  - `id`: UUID sản phẩm
  - `name`, `brand`, `categoryName`, `image`
  - `price`, `discount`, `originalPrice`, `formattedPrice`, `formattedOriginalPrice`
  - `isOutOfStock`, `isHot`, `isNew`, `rating`, `reviewCount`, `soldCount`
- **`CategoryResponseDTO`**: Cung cấp danh sách tên danh mục cho Filter Bar và Modal danh bạ.

### 3. Controller & Service Requirements
- **Endpoint**: `GET /san-pham` (hoặc alias `/products`)
- **Params**: `keyword` (String), `category` (String, default 'all'), `sort` (String, default 'popular'), `page` (int, default 0), `size` (int, default 12).
- **Phân quyền**: Công khai (`permitAll()`).
- **View trả về**: `customer/product-list`
