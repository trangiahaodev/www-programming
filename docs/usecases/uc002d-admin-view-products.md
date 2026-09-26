# Usecase: Quản trị viên xem danh sách sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                                                      |
| ---------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên xem danh sách sản phẩm mỹ phẩm                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| **Mã use case**                    | `uc002d-admin-view-products`                                                                                                                                                                                                                                                                                                                                                                                                                                                  |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) xem danh sách phân trang toàn bộ các sản phẩm mỹ phẩm trong hệ thống với các trường: Hình ảnh thumbnail, Mã sản phẩm, Tên sản phẩm, Giá bán, Số lượng tồn kho, Tên danh mục mỹ phẩm (`Category`), Trạng thái hoạt động và Ngày tạo. Hệ thống hỗ trợ tìm kiếm theo từ khóa (Mã hoặc Tên), lọc theo Danh mục, phân trang SSR linh hoạt và **bắt buộc sử dụng JPQL `JOIN FETCH` hoặc `@EntityGraph` để triệt tiêu hoàn toàn vấn đề N+1 Query**. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.                                                                                                                                                                                                                                                                                                                                                                                                                    |
| **Hậu điều kiện (Post-condition)** | - Danh sách sản phẩm được kết xuất và hiển thị trực quan trên giao diện quản trị theo đúng trang, kích thước trang và tiêu chí tìm kiếm/lọc được chọn mà không gây quá tải CSDL.                                                                                                                                                                                                                                                                                           |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                                                    | Hệ thống                                                                                                                                                                                                                                                                  |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên chọn mục "Sản phẩm" trên thanh điều hướng Sidebar hoặc truy cập đường dẫn `GET /admin/products`.                                                       |                                                                                                                                                                                                                                                                           |
|                                                                                                                                                                          | 2. Hệ thống tiếp nhận yêu cầu với các tham số mặc định (`page=0`, `size=10`, `keyword=null`, `categoryId=null`), chuẩn hóa và tạo đối tượng `Pageable` với sắp xếp giảm dần theo ngày tạo (`createdAt DESC`).                                                         |
|                                                                                                                                                                          | 3. Hệ thống thực thi truy vấn JPQL **`JOIN FETCH p.category`** trong một câu truy vấn duy nhất để nạp toàn bộ danh sách `Product` cùng thực thể `Category` liên kết, đồng thời chạy `countQuery` để tính tổng số trang.                                                 |
|                                                                                                                                                                          | 4. Tầng Service chuyển đổi danh sách `Product` sang `Page<ProductResponseDTO>` (bao gồm: `id`, `productCode`, `name`, `price`, `stockQuantity`, `imageUrl`, `active`, `categoryId`, `categoryName`, `createdAt`).                                                      |
|                                                                                                                                                                          | 5. Hệ thống tải danh sách các danh mục mỹ phẩm đang hoạt động (`getActiveCategories()`) để nạp vào dropdown bộ lọc danh mục.                                                                                                                                            |
|                                                                                                                                                                          | 6. Hệ thống kết xuất view Thymeleaf `admin/product-list` hiển thị bảng dữ liệu sản phẩm, thông tin phân trang (Tổng số bản ghi, Số trang hiện tại), thanh công cụ tìm kiếm và lọc danh mục.                                                                            |
| 7. Quản trị viên quan sát bảng danh sách sản phẩm gồm: Ảnh đại diện, Mã SP, Tên SP, Giá bán (định dạng VNĐ), Tồn kho, Tên danh mục, Badge trạng thái, và Cụm nút thao tác. |                                                                                                                                                                                                                                                                           |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 1.1, 7.1.*

| Actor                                                                                                                          | Hệ thống                                                                                                                                                                      |
| ------------------------------------------------------------------------------------------------------------------------------ | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 7.1. Quản trị viên nhập từ khóa tìm kiếm (Mã hoặc Tên sản phẩm) và/hoặc chọn Danh mục mỹ phẩm trên thanh công cụ rồi bấm Tìm. |                                                                                                                                                                               |
|                                                                                                                                | 7.2. Hệ thống gửi yêu cầu `GET /admin/products?keyword=...&categoryId=...&page=0&size=10`, thực thi lọc JPQL và hiển thị danh sách sản phẩm thỏa mãn tiêu chí.              |
| 7.3. Quản trị viên bấm chuyển trang (Trang trước, Trang sau, hoặc số trang cụ thể).                                            |                                                                                                                                                                               |
|                                                                                                                                | 7.4. Hệ thống giữ nguyên `keyword` và `categoryId` hiện tại, điều hướng đến trang được chọn (`page=k`) và hiển thị dữ liệu trang tương ứng.                                  |
| 7.5. Quản trị viên thay đổi số lượng bản ghi hiển thị trên một trang (Dropdown: 10, 20, 50).                                   |                                                                                                                                                                               |
|                                                                                                                                | 7.6. Hệ thống reset về trang đầu (`page=0`) với `size` mới được chọn và hiển thị lại bảng danh sách.                                                                         |
| 7.7. Quản trị viên nhấn nút "Xóa bộ lọc".                                                                                      |                                                                                                                                                                               |
|                                                                                                                                | 7.8. Hệ thống điều hướng về `GET /admin/products?size=...`, loại bỏ toàn bộ `keyword` và `categoryId`, hiển thị lại toàn bộ sản phẩm mặc định.                               |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 2.1.1, 3.1.1.*

| Actor                                                                                                              | Hệ thống                                                                                                                                                                         |
| ------------------------------------------------------------------------------------------------------------------ | -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 2.1.1. Tham số `page` hoặc `size` trên URL không hợp lệ (ví dụ: `page < 0`, `size <= 0` hoặc `size > 100`).       |                                                                                                                                                                                  |
|                                                                                                                    | 2.1.2. Hệ thống tự động chuẩn hóa tham số: gán `page = 0` nếu `page < 0`; gán `size = 10` nếu `size <= 0` hoặc `size > 100`. Tiếp tục luồng chính bình thường.                  |
| 3.1.1. Cơ sở dữ liệu chưa có bất kỳ sản phẩm nào hoặc kết quả tìm kiếm/lọc không có sản phẩm phù hợp.             |                                                                                                                                                                                  |
|                                                                                                                    | 3.1.2. Hệ thống hiển thị giao diện **Empty State** trang nhã với icon mỹ phẩm, thông điệp hướng dẫn trung tính và nút Call-to-Action "Thêm mới sản phẩm" hoặc "Xóa bộ lọc".     |
| 3.2.1. Xảy ra sự cố gián đoạn kết nối CSDL hoặc ngoại lệ hệ thống.                                                 |                                                                                                                                                                                  |
|                                                                                                                    | 3.2.2. Hệ thống ghi log lỗi, hiển thị thông báo alert màu đỏ trên đầu trang: "Không thể tải danh sách sản phẩm vào lúc này. Vui lòng thử lại sau!" và giữ an toàn cho ứng dụng. |

---

## 🛠 Yêu cầu kỹ thuật & Thiết kế CSDL / Code

### 1. Repository Layer (`ProductRepository.java`) — **LUẬT SỐNG CÒN CHỐNG N+1 QUERY**
Sử dụng câu truy vấn JPQL với mệnh đề **`JOIN FETCH p.category c`** để nạp đồng thời toàn bộ thực thể Category trong 1 câu SQL duy nhất:

```java
package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {

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

    boolean existsByProductCode(String productCode);

    boolean existsByName(String name);

    long countByCategoryId(String categoryId);
}
```

### 2. Service Layer (`ProductService.java` & `ProductServiceImpl.java`)
```java
public interface ProductService {
    Page<ProductResponseDTO> getProducts(String keyword, String categoryId, Pageable pageable);
    ProductResponseDTO createProduct(ProductCreateDTO dto);
}
```

**Cài đặt trong `ProductServiceImpl.java`:**
- Đánh dấu class `@Transactional(readOnly = true)`.
- Chuẩn hóa chuỗi `keyword` và `categoryId` (chuyển chuỗi rỗng thành `null`).
- Chuẩn hóa `Pageable` với sắp xếp mặc định `createdAt DESC`.
- Gọi `productRepository.searchProducts(searchKeyword, searchCategoryId, sortedPageable)`.
- Chuyển đổi an toàn sang `Page<ProductResponseDTO>`.

### 3. Controller Layer (`AdminProductController.java`)
```java
@GetMapping
public String listProducts(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "categoryId", required = false) String categoryId,
        @RequestParam(name = "page", defaultValue = "0") int page,
        @RequestParam(name = "size", defaultValue = "10") int size,
        Model model) {

    int pageNumber = Math.max(0, page);
    int pageSize = (size <= 0 || size > 100) ? 10 : size;

    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<ProductResponseDTO> productPage = productService.getProducts(keyword, categoryId, pageable);

    model.addAttribute("products", productPage.getContent());
    model.addAttribute("categories", categoryService.getActiveCategories());
    model.addAttribute("currentPage", productPage.getNumber());
    model.addAttribute("totalPages", productPage.getTotalPages());
    model.addAttribute("totalElements", productPage.getTotalElements());
    model.addAttribute("size", pageSize);
    model.addAttribute("keyword", keyword);
    model.addAttribute("categoryId", categoryId);

    return "admin/product-list";
}
```

### 4. Giao diện Thymeleaf (`templates/admin/product-list.html`)
- Hiển thị bảng dữ liệu chuẩn phong cách **Minimalism & Subtle Neo-brutalism**:
  - Cột ảnh: Thumbnail 44x44px bo góc 6px kèm fallback placeholder.
  - Cột mã SP: Badge monospace xám nhạt viền phẳng.
  - Cột tên SP: Tên đậm, dễ đọc.
  - Cột giá bán: Định dạng VNĐ nổi bật (ví dụ: `250.000 ₫`).
  - Cột tồn kho: Hiển thị số lượng, gán badge màu cam nếu tồn kho $\le 5$, badge màu đỏ nếu tồn kho $= 0$ ("Hết hàng").
  - Cột danh mục: Tên danh mục mỹ phẩm.
  - Cột trạng thái: Badge Hoạt động (xanh lá) / Tạm ẩn (xám).
  - Cột thao tác: Cụm nút icon Sửa và Xóa (chuẩn bị sẵn hook cho các use case tiếp theo).
- Toolbar lọc: Input tìm kiếm từ khóa + Dropdown lọc theo Danh mục mỹ phẩm + Selector kích thước trang.
- Pagination: Thanh phân trang số trang SSR linh hoạt.
- Empty state: Hiển thị đẹp mắt khi không có dữ liệu.
