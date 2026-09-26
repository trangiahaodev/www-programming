# Findings & Technical Context: admin-create-product

## 1. Cấu trúc CSDL & Entity liên quan
- **Bảng:** `products`
  - `id`: `VARCHAR(36)` (UUID surrogate key, `@Id @GeneratedValue(strategy = GenerationType.UUID)`)
  - `product_code`: `VARCHAR(20)`, UNIQUE, NOT NULL, `updatable = false` (Business Key)
  - `name`: `VARCHAR(150)`, NOT NULL (Index `idx_products_name`)
  - `price`: `DECIMAL(18,2)` / `BigDecimal`, NOT NULL, `>= 0`
  - `stock_quantity`: `INT`, NOT NULL, default `0`, `>= 0`
  - `brand`: `VARCHAR(100)`
  - `description`: `VARCHAR(2000)` / `TEXT`
  - `image_url`: `VARCHAR(500)`
  - `active`: `BIT` / `BOOLEAN`, default `true`, NOT NULL
  - `category_id`: `VARCHAR(36)` (FK liên kết bảng `categories`, `NOT NULL`, `@ManyToOne(fetch = FetchType.LAZY)`)
  - `created_at`: `DATETIME2` (`@CreationTimestamp`, non-updatable)
  - `updated_at`: `DATETIME2` (`@UpdateTimestamp`)

- **Bảng liên quan:** `categories`
  - Cần lấy danh sách danh mục đang hoạt động (`active = true`) để render vào `<select>` dropdown cho admin chọn khi tạo sản phẩm.

## 2. Ràng buộc DTO & Validation (JSR-380)
- `ProductCreateDTO`:
  - `productCode`: `@NotBlank(message = "Mã sản phẩm không được để trống")`, `@Pattern(regexp = "^SP[0-9]{6,8}$", message = "Mã sản phẩm phải có định dạng SP theo sau là 6-8 chữ số (VD: SP260001)")`
  - `name`: `@NotBlank(message = "Tên sản phẩm không được để trống")`, `@Size(min = 2, max = 150, message = "Tên sản phẩm từ 2 đến 150 ký tự")`
  - `categoryId`: `@NotBlank(message = "Vui lòng chọn danh mục mỹ phẩm")`
  - `price`: `@NotNull(message = "Giá sản phẩm không được để trống")`, `@DecimalMin(value = "0.0", inclusive = true, message = "Giá sản phẩm phải lớn hơn hoặc bằng 0")`
  - `stockQuantity`: `@NotNull(message = "Số lượng tồn kho không được để trống")`, `@Min(value = 0, message = "Số lượng tồn kho không được âm")`
  - `brand`: `@Size(max = 100, message = "Tên thương hiệu tối đa 100 ký tự")`
  - `description`: `@Size(max = 2000, message = "Mô tả sản phẩm tối đa 2000 ký tự")`
  - `imageUrl`: `@Size(max = 500, message = "Đường dẫn hình ảnh tối đa 500 ký tự")`
  - `active`: `Boolean` (default `true`)

## 3. Quy tắc nghiệp vụ & Kiến trúc
- Controller:
  - `GET /admin/products/create`: Chuẩn bị form `ProductCreateDTO`, load danh sách danh mục hoạt động từ `categoryService` đưa vào model (`categories`).
  - `POST /admin/products/create`: Nhận `@Valid @ModelAttribute("productDTO") ProductCreateDTO dto`, xử lý `BindingResult`, gọi Service tạo sản phẩm và redirect về `/admin/products` kèm Flash message thành công.
- Service:
  - Kiểm tra trùng `productCode`: `productRepository.existsByProductCode(code)`
  - Kiểm tra trùng `name`: `productRepository.existsByName(name)`
  - Tìm Category: `categoryRepository.findById(dto.getCategoryId()).orElseThrow(...)`
  - Tạo thực thể `Product` qua Lombok `@Builder` và lưu qua `productRepository.save(product)`.
  - Transaction: `@Transactional`.
- Thymeleaf SSR View: `templates/admin/product-create.html` kế thừa layout `layout/admin-layout.html`, tuân thủ style Minimalism + Subtle Neo-brutalism.
