# Usecase: Quản trị viên xem danh sách các danh mục mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                           |
| ---------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem danh sách các danh mục mỹ phẩm                                                                                                                                                                                                   |
| **Mã use case**                    | `uc001d-admin-view-categories`                                                                                                                                                                                                                     |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) truy cập vào khu vực quản trị để xem danh sách toàn bộ các danh mục mỹ phẩm hiện có trong hệ thống, bao gồm thông tin mã danh mục (`categoryCode`), tên danh mục, mô tả, trạng thái hoạt động và số lượng sản phẩm liên quan. Hỗ trợ tìm kiếm theo từ khóa và phân trang danh sách. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                              |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                              |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công vào hệ thống với quyền `ADMIN`.<br>- Quản trị viên đang ở trang Quản trị (Admin Dashboard) hoặc thanh điều hướng quản trị.                                                                                |
| **Hậu điều kiện (Post-condition)** | - Danh sách danh mục mỹ phẩm được hiển thị đầy đủ, chính xác theo điều kiện tìm kiếm và phân trang.<br>- Trạng thái hệ thống và dữ liệu trong Database không bị thay đổi (Thao tác Read-only).                                                    |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                            | Hệ thống                                                                                                                                                                                                                           |
| ------------------------------------------------------------------------------------------------ | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên chọn menu "Quản lý danh mục" trên thanh điều hướng quản trị.                    |                                                                                                                                                                                                                                    |
|                                                                                                  | 2. Hệ thống tiếp nhận yêu cầu (`GET /admin/categories`), truy vấn danh sách danh mục mỹ phẩm từ Database theo trang đầu tiên (mặc định trang 0/1, kích thước 10 mục) và sắp xếp theo thời gian tạo mới nhất (`createdAt DESC`). |
|                                                                                                  | 3. Hệ thống hiển thị giao diện danh sách danh mục (`admin/category-list`) bao gồm: bảng dữ liệu (Mã danh mục, Tên danh mục, Mô tả, Số lượng sản phẩm, Trạng thái), thanh tìm kiếm, nút "Thêm mới", và bộ phân trang.             |
| 4. Quản trị viên nhập từ khóa tìm kiếm vào ô tìm kiếm (nếu cần) và nhấn nút "Tìm kiếm".          |                                                                                                                                                                                                                                    |
|                                                                                                  | 5. Hệ thống lọc danh sách danh mục theo từ khóa tìm kiếm (so khớp không phân biệt hoa thường theo tên danh mục hoặc mã danh mục) và cập nhật hiển thị bảng danh sách danh mục tương ứng.                                          |
| 6. Quản trị viên chọn chuyển sang trang khác trên thanh phân trang (nếu danh sách có nhiều trang). |                                                                                                                                                                                                                                    |
|                                                                                                  | 7. Hệ thống tải dữ liệu các danh mục thuộc trang được chọn kèm điều kiện tìm kiếm hiện tại và hiển thị lại danh sách.                                                                                                              |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 2.1.*

| Actor                                                          | Hệ thống                                                                                                                                                                   |
| -------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1. Quản trị viên nhấn nút "Làm mới" / "Xóa bộ lọc tìm kiếm". |                                                                                                                                                                            |
|                                                                | 4.2. Hệ thống xóa trắng từ khóa tìm kiếm, tải lại danh sách toàn bộ danh mục mỹ phẩm ở trang đầu tiên.                                                                     |
|                                                                | 4.3. Quay lại bước 3 của luồng chính.                                                                                                                                      |
| 5.1. Không có danh mục nào khớp với từ khóa tìm kiếm.          |                                                                                                                                                                            |
|                                                                | 5.2. Hệ thống hiển thị thông báo "Không tìm thấy danh mục mỹ phẩm nào phù hợp" trên bảng dữ liệu kèm gợi ý thử lại từ khóa khác. Phân trang được ẩn hoặc đưa về trang 1. |
|                                                                | 5.3. Quay lại bước 4 của luồng chính.                                                                                                                                      |
| 6.1. Quản trị viên thay đổi số lượng danh mục hiển thị mỗi trang (Page Size). |                                                                                                                                                                            |
|                                                                | 6.2. Hệ thống tính toán lại tổng số trang và tải dữ liệu trang đầu tiên với số lượng dòng mới được chọn.                                                                   |
|                                                                | 6.3. Quay lại bước 3 của luồng chính.                                                                                                                                      |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 4.1.1, 7.1.2.*

| Actor                                                                               | Hệ thống                                                                                                                                                                         |
| ----------------------------------------------------------------------------------- | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Phiên đăng nhập của Quản trị viên hết hạn hoặc không có quyền `ROLE_ADMIN`. |                                                                                                                                                                                  |
|                                                                                     | 2.1.2. Hệ thống chặn truy cập và chuyển hướng người dùng về trang đăng nhập `/login` kèm thông báo lỗi "Phiên làm việc đã hết hạn hoặc bạn không có quyền truy cập trang này." |
|                                                                                     | 2.1.3. Kết thúc use case.                                                                                                                                                        |
| 2.2.1. Xảy ra lỗi kết nối Database hoặc lỗi hệ thống khi tải danh sách danh mục.    |                                                                                                                                                                                  |
|                                                                                     | 2.2.2. Hệ thống hiển thị thông báo lỗi "Không thể tải danh sách danh mục mỹ phẩm lúc này, vui lòng thử lại sau." trên giao diện trang lỗi chung.                                |
|                                                                                     | 2.2.3. Kết thúc use case.                                                                                                                                                        |
| 4.1.1. Từ khóa tìm kiếm chứa ký tự đặc biệt không hợp lệ hoặc vượt quá độ dài tối đa (ví dụ > 100 ký tự). |                                                                                                                                                                                  |
|                                                                                     | 4.1.2. Hệ thống tự động cắt tỉa khoảng trắng thừa, loại bỏ ký tự không hợp lệ hoặc hiển thị thông báo cảnh báo "Từ khóa tìm kiếm không hợp lệ".                                 |
|                                                                                     | 4.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                          |

---

## 🛠 Yêu cầu cập nhật Database / Entity / DTO

### 1. Database & Entity: `Category` (Bảng `categories`)
Tuân thủ Dual-Key Strategy và các quy tắc backend entity:
- **`id`**: String (UUID, length = 36), Primary Key (`@Id`, `@GeneratedValue(strategy = GenerationType.UUID)`), Surrogate Key.
- **`categoryCode`**: String (length = 20, unique = true, updatable = false), Business Key (ví dụ: `DM26000001`, sinh qua Custom Sequence Generator).
- **`name`**: String (length = 150, nullable = false) - Tên danh mục.
- **`description`**: String (length = 500, nullable = true) - Mô tả danh mục.
- **`active`**: Boolean (default = true) - Trạng thái hoạt động.
- **`products`**: `@OneToMany(mappedBy = "category", fetch = FetchType.LAZY)` kèm `@ToString.Exclude`, `@EqualsAndHashCode.Exclude`.
- **`createdAt`**, **`updatedAt`**: Timestamp auditing.

### 2. DTO: `CategoryResponseDTO` / `CategoryListDTO`
Dùng để truyền dữ liệu hiển thị trên Thymeleaf View (`admin/category-list.html`), tránh đưa trực tiếp Entity ra View:
- `id`: String (Surrogate Key)
- `categoryCode`: String (Business Key)
- `name`: String
- `description`: String
- `active`: Boolean
- `productCount`: Long / Integer (Số lượng sản phẩm thuộc danh mục, tính toán qua query count hoặc mapping từ Service)
- `createdAt`: LocalDateTime

### 3. Controller & Service Requirements
- **Endpoint**: `GET /admin/categories`
- **Params hỗ trợ**: `keyword` (String, optional), `page` (int, default 0), `size` (int, default 10).
- **Phân quyền**: Yêu cầu quyền `ROLE_ADMIN` (`@PreAuthorize("hasRole('ADMIN')")` hoặc SecurityFilterChain cấu hình `/admin/**`).
- **View trả về**: `admin/category-list`
