# Usecase: Quản trị viên tìm kiếm sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên tìm kiếm sản phẩm mỹ phẩm                                                                                                                                                                                                                                                                                                                                                                                                                                       |
| **Mã use case**                    | `uc002e-admin-search-products`                                                                                                                                                                                                                                                                                                                                                                                                                                                 |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) tìm kiếm nhanh danh sách sản phẩm mỹ phẩm theo **Tên sản phẩm** hoặc **Mã sản phẩm (`productCode`)**. Thanh tìm kiếm được tích hợp trực tiếp trên trang danh sách sản phẩm hiện có (`GET /admin/products`), sử dụng HTTP GET với tham số truy vấn `keyword`. Tính năng đảm bảo duy trì toàn vẹn cơ chế phân trang khi tìm kiếm, đồng thời **bắt buộc sử dụng JPQL `JOIN FETCH` kết hợp `LIKE` để triệt tiêu hoàn toàn vấn đề N+1 Query**. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập vào hệ thống với quyền `ADMIN`.<br>- Quản trị viên đang ở trang quản lý sản phẩm (`/admin/products`).                                                                                                                                                                                                                                                                                                                                          |
| **Hậu điều kiện (Post-condition)** | - Kết quả tìm kiếm sản phẩm hiển thị đúng theo từ khóa truy vấn.<br>- Các liên kết phân trang giữ nguyên tham số `keyword` để người dùng duyệt kết quả qua các trang mà không bị mất từ khóa tìm kiếm.                                                                                                                                                                                                                                                                       |

---

### Luồng sự kiện chính (Main flow):

| Actor | Hệ thống |
| :--- | :--- |
| 1. Quản trị viên nhập từ khóa tìm kiếm (Tên sản phẩm hoặc Mã sản phẩm, ví dụ: `Son dưỡng`, `SP0001`) vào ô tìm kiếm trên thanh công cụ của trang danh sách. | |
| 2. Quản trị viên nhấn phím **Enter** hoặc bấm nút **"Tìm kiếm"** (Biểu tượng kính lúp). | |
| | 3. Hệ thống gửi yêu cầu HTTP `GET /admin/products?keyword=...&page=0&size=10`. |
| | 4. Tầng Controller nhận tham số `keyword`, làm sạch khoảng trắng (`trim()`), và chuyển tiếp đến tầng Service. |
| | 5. Tầng Service gọi phương thức tìm kiếm trong `ProductRepository` với truy vấn JPQL **`JOIN FETCH p.category`** kết hợp điều kiện `LIKE` không phân biệt hoa thường (`LOWER`) cho cả `name` và `productCode`. |
| | 6. Cơ sở dữ liệu thực thi 1 câu lệnh SQL duy nhất nạp toàn bộ thông tin sản phẩm và danh mục liên quan (không bị N+1 query), đồng thời thực thi `countQuery` để tính tổng số bản ghi phù hợp. |
| | 7. Tầng Service chuyển đổi danh sách thực thể `Product` sang `Page<ProductResponseDTO>`. |
| | 8. Hệ thống kết xuất view Thymeleaf `admin/product-list`, hiển thị danh sách sản phẩm thỏa mãn điều kiện tìm kiếm, giữ lại giá trị `keyword` trong ô input và hiển thị tổng số kết quả tìm thấy. |
| 9. Quản trị viên quan sát kết quả tìm kiếm và có thể thao tác Xem, Sửa, Xóa hoặc chuyển trang. | |

---

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, ví dụ: 2.1, 9.1.*

| Actor | Hệ thống |
| :--- | :--- |
| 2.1. Quản trị viên kết hợp tìm kiếm theo từ khóa `keyword` và lọc theo danh mục `categoryId`. | |
| | 2.2. Hệ thống gửi `GET /admin/products?keyword=...&categoryId=...&page=0&size=10`, thực thi tìm kiếm đồng thời cả từ khóa và danh mục, trả về kết quả chính xác. |
| 9.1. Quản trị viên bấm chuyển sang các trang tiếp theo (Trang 2, Trang 3, Trang sau). | |
| | 9.2. Hệ thống tạo URL phân trang giữ nguyên tham số: `/admin/products?keyword=...&categoryId=...&page=1&size=10`, hiển thị dữ liệu trang tiếp theo của từ khóa tìm kiếm mà không bị mất context. |
| 9.3. Quản trị viên nhấn nút **"Xóa bộ lọc"** hoặc xóa trắng ô tìm kiếm rồi nhấn Enter. | |
| | 9.4. Hệ thống điều hướng về `GET /admin/products`, tải lại toàn bộ danh sách sản phẩm ban đầu. |

---

### Luồng sự kiện ngoại lệ (Exception Flow):

| Actor | Hệ thống |
| :--- | :--- |
| 1.1.1. Từ khóa tìm kiếm không khớp với bất kỳ sản phẩm nào trong hệ thống. | |
| | 1.1.2. Hệ thống hiển thị bảng rỗng với giao diện **Empty State** trang nhã: Icon tìm kiếm, thông báo *"Không tìm thấy sản phẩm nào phù hợp với từ khóa '{keyword}'"*, cùng nút bấm *"Xóa bộ lọc"* để quay về danh sách đầy đủ. |
| 3.1.1. Quản trị viên nhập từ khóa chỉ toàn khoảng trắng hoặc ký tự đặc biệt không hợp lệ. | |
| | 3.1.2. Hệ thống tự động `trim()` từ khóa, nếu rỗng thì xử lý như xem toàn bộ danh sách, tránh phát sinh lỗi truy vấn CSDL. |
| 5.1.1. Xảy ra lỗi kết nối CSDL hoặc ngoại lệ hệ thống trong quá trình tìm kiếm. | |
| | 5.1.2. Hệ thống bắt ngoại lệ, ghi log chi tiết, hiển thị thông báo alert lỗi màu đỏ trên đầu trang và giữ an toàn cho ứng dụng. |

---

## 🛠 Yêu cầu kỹ thuật & Thiết kế CSDL / Code

### 1. Repository Layer (`ProductRepository.java`) — **LUẬT SỐNG CÒN CHỐNG N+1 QUERY**
Sử dụng câu truy vấn JPQL với mệnh đề **`JOIN FETCH p.category c`** kết hợp `LIKE` không phân biệt chữ hoa/chữ thường (`LOWER`):

```java
@Query(
    value = """
        SELECT p FROM Product p
        JOIN FETCH p.category c
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR :categoryId = '' OR c.id = :categoryId)
    """,
    countQuery = """
        SELECT COUNT(p) FROM Product p
        WHERE (:keyword IS NULL OR :keyword = ''
               OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
               OR LOWER(p.productCode) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:categoryId IS NULL OR :categoryId = '' OR p.category.id = :categoryId)
    """
)
Page<Product> searchProducts(
        @Param("keyword") String keyword,
        @Param("categoryId") String categoryId,
        Pageable pageable
);
```

### 2. Service Layer (`ProductService.java` & `ProductServiceImpl.java`)
- Phương thức `Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable)`
- Đánh dấu `@Transactional(readOnly = true)`.
- Chuẩn hóa đầu vào `keyword`: kiểm tra null hoặc empty để truyền giá trị sạch vào repository.
- Chuyển đổi an toàn từ Entity `Product` sang `ProductResponseDTO` (bao gồm `categoryName`).

### 3. Controller Layer (`AdminProductController.java`)
- Xử lý HTTP Request `GET /admin/products`:
```java
@GetMapping
public String listProducts(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "categoryId", required = false) String categoryId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        Model model) {
    ...
}
```
- Truyền `keyword` và `categoryId` trở lại `Model` (`model.addAttribute("keyword", keyword)`) để giữ giá trị trong input trên giao diện.

### 4. UI/Thymeleaf Layer (`product-list.html`)
- Form tìm kiếm phương thức `GET`:
```html
<form th:action="@{/admin/products}" method="get" class="search-filter-form">
    <div class="search-input-group">
        <input type="text" name="keyword" th:value="${keyword}" 
               placeholder="Tìm theo tên hoặc mã sản phẩm..." class="form-input">
        <button type="submit" class="btn btn-secondary">🔍 Tìm kiếm</button>
    </div>
    ...
</form>
```
- Các nút phân trang bắt buộc truyền kèm `keyword` và `categoryId`:
```html
<a th:href="@{/admin/products(keyword=${keyword}, categoryId=${categoryId}, page=${pageNum}, size=${pageSize})}">...</a>
```
