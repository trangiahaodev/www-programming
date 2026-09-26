# Usecase: Quản trị viên thêm mới sản phẩm mỹ phẩm

| Thành phần                         | Nội dung                                                                                                                                                                                                                                                                                                                                                                                                                                        |
| ---------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Tên use case**                   | Quản trị viên thêm mới sản phẩm mỹ phẩm                                                                                                                                                                                                                                                                                                                                                                                                         |
| **Mã use case**                    | `uc002a-admin-create-product`                                                                                                                                                                                                                                                                                                                                                                                                                   |
| **Mô tả sơ lược**                  | Quản trị viên (Admin) tạo mới một sản phẩm mỹ phẩm vào hệ thống với đầy đủ thông tin: Mã sản phẩm (Business Key duy nhất), Tên sản phẩm, Danh mục mỹ phẩm (`categoryId`), Giá bán (> 0), Số lượng tồn kho ($\ge$ 0), Mô tả, Hình ảnh (`imageUrl` - lưu chuỗi đường dẫn tĩnh, không upload file multipart), và Trạng thái hoạt động. Hệ thống thực hiện kiểm tra JSR-380, kiểm tra tính hợp lệ của `categoryId` tại Service, và áp dụng PRG Pattern kèm Flash message. |
| **Actor chính**                    | Admin                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Actor phụ**                      | Không                                                                                                                                                                                                                                                                                                                                                                                                                                           |
| **Tiền điều kiện (Pre-condition)** | - Quản trị viên đã đăng nhập thành công với quyền `ADMIN`.<br>- Cơ sở dữ liệu đã có ít nhất một danh mục mỹ phẩm đang hoạt động để gán sản phẩm.                                                                                                                                                                                                                                                                                               |
| **Hậu điều kiện (Post-condition)** | - Bản ghi `Product` mới được lưu vào bảng `products` trong CSDL với khóa ngoại `category_id` hợp lệ.<br>- Quản trị viên được chuyển hướng về trang danh sách sản phẩm `/admin/products` kèm thông báo flash thành công.                                                                                                                                                                                                                      |

### Luồng sự kiện chính (Main flow):

| Actor                                                                                                                                                                                | Hệ thống                                                                                                                                                                                                                                |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ | --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1. Quản trị viên truy cập màn hình tạo mới sản phẩm bằng cách nhấn nút "Thêm mới sản phẩm" (`GET /admin/products/create`).                                                          |                                                                                                                                                                                                                                         |
|                                                                                                                                                                                      | 2. Hệ thống lấy danh sách các danh mục mỹ phẩm đang hoạt động (`Category`), nạp vào `model` và hiển thị form tạo mới sản phẩm (`admin/product-create`) với đối tượng rỗng `productDTO`.                                                 |
| 3. Quản trị viên nhập thông tin sản phẩm (Payload gồm: `productCode`, `name`, `categoryId`, `price`, `stockQuantity`, `description`, `imageUrl`, `active`) và nhấn nút "Lưu sản phẩm". |                                                                                                                                                                                                                                         |
|                                                                                                                                                                                      | 4. Hệ thống tiếp nhận yêu cầu (`POST /admin/products/create`), kiểm tra tính hợp lệ dữ liệu đầu vào theo chuẩn JSR-380 (`@Valid`).                                                                                                    |
|                                                                                                                                                                                      | 5. Hệ thống gọi tầng Service để kiểm tra tính duy nhất của Mã sản phẩm (`existsByProductCode`) và Tên sản phẩm (`existsByName`).                                                                                                       |
|                                                                                                                                                                                      | 6. Tầng Service kiểm tra sự tồn tại thực tế của Danh mục trong cơ sở dữ liệu (`categoryRepository.findById(categoryId)`).                                                                                                              |
|                                                                                                                                                                                      | 7. Hệ thống chuyển đổi DTO sang Entity `Product`, gán chuỗi `imageUrl`, thiết lập mối quan hệ với `Category`, lưu vào cơ sở dữ liệu, tự động gán thời gian tạo `createdAt` và hoàn tất Transaction.                                    |
|                                                                                                                                                                                      | 8. Hệ thống chuyển hướng Quản trị viên về trang danh sách sản phẩm (`GET /admin/products` theo PRG Pattern) và hiển thị thông báo flash thành công: "Thêm mới sản phẩm mỹ phẩm thành công!".                                            |

### Luồng sự kiện thay thế (Alternate Flow):

*Đánh số bắt đầu từ bước rẽ nhánh ở luồng chính, vd: 3.1.*

| Actor                                                         | Hệ thống                                                                                                                                  |
| ------------------------------------------------------------- | ----------------------------------------------------------------------------------------------------------------------------------------- |
| 3.1. Quản trị viên nhấn nút "Hủy bỏ" / "Quay lại danh sách".  |                                                                                                                                           |
|                                                               | 3.2. Hệ thống hủy thao tác tạo, không lưu dữ liệu vào CSDL và chuyển hướng người dùng về trang danh sách sản phẩm `/admin/products`.       |
|                                                               | 3.3. Kết thúc use case.                                                                                                                   |

### Luồng sự kiện ngoại lệ (Exception Flow):

*Đánh số cấp 3 dựa trên bước rẽ nhánh, vd: 4.1.1, 5.1.1, 6.1.1.*

| Actor                                                                                                                                                                                                                                                   | Hệ thống                                                                                                                                                                                             |
| ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 4.1.1. Dữ liệu nhập vào vi phạm ràng buộc JSR-380:<br>- `productCode` bị rỗng hoặc sai định dạng.<br>- `name` bị rỗng hoặc dưới 2 / trên 150 ký tự.<br>- `categoryId` chưa được chọn.<br>- `price` $\le 0$ hoặc rỗng.<br>- `stockQuantity` $< 0$ hoặc rỗng.<br>- `imageUrl` vượt quá 500 ký tự. |                                                                                                                                                                                                      |
|                                                                                                                                                                                                                                                         | 4.1.2. Hệ thống tải lại form tạo mới, giữ nguyên các dữ liệu đã nhập, tải lại danh mục mỹ phẩm và hiển thị thông báo lỗi inline chi tiết màu đỏ (`th:errors`) ngay dưới từng trường dữ liệu vi phạm. |
|                                                                                                                                                                                                                                                         | 4.1.3. Quay lại bước 3 của luồng chính để Quản trị viên chỉnh sửa.                                                                                                                                   |
| 5.1.1. Mã sản phẩm (`productCode`) đã tồn tại trong CSDL.                                                                                                                                                                                               |                                                                                                                                                                                                      |
|                                                                                                                                                                                                                                                         | 5.1.2. Tầng Service ném `IllegalArgumentException("Mã sản phẩm '[productCode]' đã tồn tại trong hệ thống!")`. Controller bắt ngoại lệ, đưa thông báo lỗi lên form và hiển thị alert lỗi.           |
|                                                                                                                                                                                                                                                         | 5.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                                              |
| 5.2.1. Tên sản phẩm (`name`) đã tồn tại trong CSDL.                                                                                                                                                                                                     |                                                                                                                                                                                                      |
|                                                                                                                                                                                                                                                         | 5.2.2. Tầng Service ném `IllegalArgumentException("Tên sản phẩm '[name]' đã tồn tại trong hệ thống!")`. Controller bắt ngoại lệ, đưa thông báo lỗi lên form và hiển thị alert lỗi.                 |
|                                                                                                                                                                                                                                                         | 5.2.3. Quay lại bước 3 của luồng chính.                                                                                                                                                              |
| 6.1.1. Danh mục được chọn (`categoryId`) không tồn tại trong CSDL (bị xóa hoặc ID không hợp lệ).                                                                                                                                                        |                                                                                                                                                                                                      |
|                                                                                                                                                                                                                                                         | 6.1.2. Tầng Service ném `IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!")`. Controller bắt ngoại lệ và hiển thị thông báo lỗi màu đỏ trên form.                 |
|                                                                                                                                                                                                                                                         | 6.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                                              |
| 7.1.1. Lỗi kết nối CSDL hoặc lỗi hệ thống bất khả kháng.                                                                                                                                                                                                |                                                                                                                                                                                                      |
|                                                                                                                                                                                                                                                         | 7.1.2. Hệ thống rollback Transaction, giữ lại dữ liệu form và hiển thị thông báo lỗi tổng thể: "Không thể thêm sản phẩm vào lúc này. Vui lòng thử lại sau!".                                       |
|                                                                                                                                                                                                                                                         | 7.1.3. Quay lại bước 3 của luồng chính.                                                                                                                                                              |

---

## 🛠 Yêu cầu kỹ thuật & Thiết kế CSDL / Code

### 1. Entity Specification (`Product.java`)
```java
package iuh.wwwprogramming.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_products_code", columnList = "product_code"),
        @Index(name = "idx_products_name", columnList = "name"),
        @Index(name = "idx_products_category", columnList = "category_id")
    }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(length = 36, updatable = false, nullable = false)
    private String id;

    @Column(name = "product_code", unique = true, updatable = false, length = 20, nullable = false)
    private String productCode;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(length = 2000)
    private String description;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Category category;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

### 2. DTO Specification (`ProductCreateDTO.java`)
```java
package iuh.wwwprogramming.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDTO {

    @NotBlank(message = "Mã sản phẩm không được để trống")
    @Size(min = 3, max = 20, message = "Mã sản phẩm phải từ 3 đến 20 ký tự")
    private String productCode;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(min = 2, max = 150, message = "Tên sản phẩm phải từ 2 đến 150 ký tự")
    private String name;

    @NotBlank(message = "Vui lòng chọn danh mục mỹ phẩm")
    private String categoryId;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.01", inclusive = true, message = "Giá bán phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0, message = "Số lượng tồn kho phải lớn hơn hoặc bằng 0")
    private Integer stockQuantity;

    @Size(max = 2000, message = "Mô tả sản phẩm tối đa 2000 ký tự")
    private String description;

    @Size(max = 500, message = "Đường dẫn hình ảnh tối đa 500 ký tự")
    private String imageUrl;

    @Builder.Default
    private Boolean active = true;
}
```

### 3. Repository Layer (`ProductRepository.java`)
```java
package iuh.wwwprogramming.repository;

import iuh.wwwprogramming.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, String> {
    boolean existsByProductCode(String productCode);
    boolean existsByName(String name);
    long countByCategoryId(String categoryId);
}
```

### 4. Service Layer (`ProductService.java` & `ProductServiceImpl.java`)
```java
public interface ProductService {
    ProductResponseDTO createProduct(ProductCreateDTO dto);
}
```
**Quy tắc kiểm tra tại tầng Service (`ProductServiceImpl`):**
- `@Transactional`
- Kiểm tra `productRepository.existsByProductCode(...)` $\rightarrow$ ném `IllegalArgumentException`.
- Kiểm tra `productRepository.existsByName(...)` $\rightarrow$ ném `IllegalArgumentException`.
- Kiểm tra `categoryRepository.findById(dto.getCategoryId())` $\rightarrow$ ném `IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!")` nếu không tìm thấy.
- Map DTO sang `Product` Entity qua Lombok `@Builder`, gán chuỗi `imageUrl`, gán `category` và gọi `productRepository.save(product)`.

### 5. Controller Layer (`AdminProductController.java`)
- `GET /admin/products/create`: Nạp `categories` từ `categoryService` và form `productDTO`.
- `POST /admin/products/create`: Nhận `@Valid @ModelAttribute("productDTO") ProductCreateDTO dto`, xử lý `BindingResult`, gọi `productService.createProduct(dto)`, gán Flash message và redirect về `/admin/products`.
