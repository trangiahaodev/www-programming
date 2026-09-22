# Usecase: Khách hàng xem thông tin chi tiết sản phẩm và chọn phân loại biến thể

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                      |
| ---------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Khách hàng xem thông tin chi tiết sản phẩm và chọn phân loại biến thể                                                                                                                                                                                                                                         |
| **Mã use case**                    | `uc003d-customer-view-product-detail`                                                                                                                                                                                                                                                                         |
| **Mô tả sơ lược**                  | Khách hàng truy cập trang chi tiết của một sản phẩm mỹ phẩm cụ thể để xem toàn bộ thông tin chuyên sâu (ảnh sắc nét, thương hiệu, giá bán, công dụng, thành phần, hướng dẫn sử dụng, đánh giá). Hệ thống tự động phân loại thông minh (Mặt nạ -> Quy cách miếng/hộp; Son môi -> Tone màu swatch; Skincare -> Dung tích ml; Nước hoa -> ml chiết/fullbox) và cập nhật giá bán, tồn kho theo thời gian thực khi khách hàng lựa chọn phân loại. |
| **Actor chính**                    | Khách hàng (Customer / Khách vãng lai)                                                                                                                                                                                                                                                                        |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | Khách hàng đã click vào một sản phẩm từ trang chủ, danh sách sản phẩm hoặc truy cập trực tiếp qua đường dẫn `/san-pham/{id}`.                                                                                                                                                                                |
| **Hậu điều kiện (Post-condition)** | - Thông tin sản phẩm và phân loại phù hợp được hiển thị trực quan, đầy đủ.<br>- Giá tiền và số lượng tồn kho được tính toán cập nhật chính xác theo biến thể đã chọn.<br>- Trạng thái hệ thống và cơ sở dữ liệu không bị thay đổi (Thao tác Read-only).                                                    |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                 | Hệ thống                                                                                                                                                                                                                                                               |
| ----------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Khách hàng click vào một sản phẩm trên website hoặc truy cập link `/san-pham/{id}`.                |                                                                                                                                                                                                                                                                        |
|                                                                                                       | 2. Hệ thống tiếp nhận yêu cầu (`GET /san-pham/{id}`), truy vấn thông tin chi tiết sản phẩm từ Database thông qua `id` hoặc `productCode`.                                                                                                                            |
|                                                                                                       | 3. Hệ thống kích hoạt **Dynamic Variant Engine** tại Service Layer: phân tích ngữ cảnh tên và danh mục sản phẩm để tự động sinh ra danh sách phân loại (ví dụ: Mặt nạ sinh ra [1 Miếng, Hộp 5 miếng, Hộp 10 miếng, Combo 2 hộp]; Son môi sinh ra bảng màu Swatches). |
|                                                                                                       | 4. Hệ thống tải 4 sản phẩm liên quan cùng danh mục (`getRelatedProducts`) và render giao diện chi tiết (`customer/product-detail`).                                                                                                                                    |
| 5. Khách hàng xem các thông tin: ảnh lớn, điểm đánh giá, giá bán, các cam kết chính hãng, công dụng, thành phần, đánh giá của người mua trước. |                                                                                                                                                                                                                                        |
| 6. Khách hàng bấm chọn một phân loại biến thể mong muốn (ví dụ: "Hộp 5 miếng" hoặc màu son "#01 Đỏ Cam"). |                                                                                                                                                                                                                                                                        |
|                                                                                                       | 7. JavaScript phía client cập nhật lập tức giá bán, giá gốc, số lượng tồn kho và nhãn đang chọn theo thời gian thực tương ứng với biến thể đã click.                                                                                                                   |
| 8. Khách hàng điều chỉnh số lượng (nút `+` / `-`) và bấm "Thêm Vào Giỏ" hoặc "Mua Ngay".               |                                                                                                                                                                                                                                                                        |
|                                                                                                       | 9. Hệ thống ghi nhận sản phẩm kèm phân loại đã chọn, hiển thị thông báo Toast thành công và cập nhật giỏ hàng.                                                                                                                                                        |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 5.1, 6.1.*

| Actor                                                                                                 | Hệ thống                                                                                                                                                             |
| ----------------------------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 5.1. Sản phẩm đã hết hàng trong kho (`stock = 0` hoặc `isOutOfStock = true`).                         |                                                                                                                                                                      |
|                                                                                                       | 5.2. Hệ thống hiển thị badge "Tạm thời hết hàng", ẩn bộ tăng giảm số lượng và vô hiệu hóa (disable) nút "Thêm vào giỏ" / "Mua ngay".                               |
|                                                                                                       | 5.3. Kết thúc tác vụ mua hàng.                                                                                                                                       |
| 5.4. Khách hàng click vào số sao hoặc link đánh giá ở đầu trang (`#pd-reviews-section`).             |                                                                                                                                                                      |
|                                                                                                       | 5.5. Trình duyệt cuộn mượt xuống khối "Đánh Giá Từ Khách Hàng Thực Tế" bên dưới ảnh sản phẩm để đọc chi tiết phản hồi của người mua.                                 |
|                                                                                                       | 5.6. Quay lại bước 5 của luồng chính.                                                                                                                                |
| 5.7. Khách hàng bấm nút "Đánh giá ngay" hoặc "So sánh".                                               |                                                                                                                                                                      |
|                                                                                                       | 5.8. Hệ thống mở Modal viết đánh giá trải nghiệm hoặc Modal so sánh tính năng sản phẩm với các sản phẩm tương đương.                                                 |
|                                                                                                       | 5.9. Quay lại bước 5 của luồng chính khi đóng modal.                                                                                                                 |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 2.2.1.*

| Actor                                                                             | Hệ thống                                                                                                                                                               |
| --------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Mã sản phẩm `id` không tồn tại trong hệ thống hoặc đã bị xóa / ẩn.        |                                                                                                                                                                        |
|                                                                                   | 2.1.2. Service ném `IllegalArgumentException`, Controller bắt lỗi và chuyển hướng người dùng về trang danh sách `/san-pham` kèm thông báo "Không tìm thấy sản phẩm yêu cầu." |
|                                                                                   | 2.1.3. Kết thúc use case.                                                                                                                                              |
| 2.2.1. Lỗi kết nối cơ sở dữ liệu máy chủ trong quá trình truy vấn sản phẩm.      |                                                                                                                                                                        |
|                                                                                   | 2.2.2. Hệ thống chuyển hướng tới trang lỗi 500 hoặc hiển thị thông báo lỗi thân thiện.                                                                                 |
|                                                                                   | 2.2.3. Kết thúc use case.                                                                                                                                              |

---

## 🛠 Yêu cầu cập nhật Database / Entity / DTO

### 1. Database & Entity: `Product` (Bảng `products`)
- Lưu trữ đầy đủ các thông tin chuyên sâu của mỹ phẩm cao cấp:
  - `origin` (NVARCHAR 100): Xuất xứ thương hiệu (Nhật Bản, Hàn Quốc, Pháp...).
  - `description` (NVARCHAR MAX): Mô tả công dụng nổi bật.
  - `ingredients` (NVARCHAR MAX): Bảng thành phần chi tiết (Hyaluronic Acid, Vitamin C, Niacinamide...).
  - `usageInstructions` (NVARCHAR MAX): Hướng dẫn sử dụng và bảo quản.
  - `stock` (INT): Số lượng tồn kho.
  - `rating`, `reviewCount`, `soldCount`.

### 2. DTO: `ProductDetailDTO` & `ProductVariantDTO`
- **`ProductDetailDTO`**:
  - `variantType`: Nhóm loại biến thể (`PACK`, `COLOR`, `TONE`, `CAPACITY`, `DEVICE`).
  - `variantGroupTitle`: Tiêu đề ngữ cảnh tương ứng (`"Quy cách đóng gói:"`, `"Tone màu thời thượng:"`, `"Dung tích nước hoa:"`, `"Dung tích / Kích thước:"`).
  - `variantIcon`: Icon đại diện (`"box"`, `"palette"`, `"droplet"`, `"sparkles"`).
  - `variants`: Danh sách các đối tượng `ProductVariantDTO`.
  - `getDefaultVariant()`: Lấy biến thể mặc định để hiển thị lúc đầu trang.
- **`ProductVariantDTO`**:
  - `id`: Mã biến thể (`piece-1`, `box-5`, `color-01`, `size-50ml`...).
  - `name`: Tên ngắn gọn (`1 Miếng`, `#01 Đỏ Cam`, `50ml`).
  - `subName`: Mô tả phụ (`Dùng thử`, `Chuẩn hãng`, `Tiết kiệm 15%`).
  - `fullLabel`: Nhãn đầy đủ hiển thị trạng thái hiện tại.
  - `priceMultiplier`: Hệ số nhân giá (`1.0`, `4.8`, `8.8`, `16.5`...).
  - `price`: Giá tiền tính toán làm tròn đến hàng nghìn.
  - `colorHex`: Mã màu HEX (dành riêng cho son môi và cushion: `#e63946`, `#f9e4d4`...).
  - `badge`: Huy hiệu nổi bật (`Bán chạy`, `Tiết kiệm 15%`, `HOT DEAL`...).
  - `stock`: Tồn kho theo từng biến thể.
  - `isDefault`: Đánh dấu biến thể kích hoạt đầu tiên.

### 3. Controller & Service Requirements
- **Endpoint**: `GET /san-pham/{id}`
- **Service Method**: `ProductDetailDTO getProductDetail(String idOrCode)`
- **View trả về**: `customer/product-detail`
