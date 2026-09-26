# Mermaid Diagrams: admin-update-product

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên cập nhật thông tin sản phẩm mỹ phẩm** (`uc002b-admin-update-product`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình tải form chỉnh sửa và cập nhật dữ liệu sản phẩm từ trình duyệt Quản trị viên qua bộ lọc Spring Security, Spring MVC Controller, Bean Validation (JSR-380), Service Layer (kiểm tra nghiệp vụ, trùng tên có loại trừ ID và quan hệ danh mục), Spring Data JPA Repository tới cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Security["Spring Security Filter Chain<br>(Role: ROLE_ADMIN)"]
    Controller["AdminProductController<br>(Spring MVC @Controller)"]
    Validation["Jakarta Bean Validation<br>(JSR-380 @Valid)"]
    CatService["CategoryService<br>(getActiveCategories)"]
    ProdService["ProductService / ProductServiceImpl<br>(Service Layer @Transactional)"]
    CatRepo["CategoryRepository<br>(Spring Data JPA)"]
    ProdRepo["ProductRepository<br>(Spring Data JPA)"]
    Database[("Microsoft SQL Server<br>(Tables: products, categories)")]
    View["Thymeleaf Engine<br>(admin/product-edit.html)"]

    Client -->|"1. HTTP GET /admin/products/edit/{id}"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    Controller -->|"getProductForEdit(id)"| ProdService
    ProdService -->|"findByIdWithCategory(id)"| ProdRepo
    ProdRepo -->|"SQL: SELECT p.*, c.* FROM products p JOIN categories c WHERE p.id = ?"| Database
    Database -->|"Product record"| ProdRepo
    ProdRepo -->|"Product entity"| ProdService
    ProdService -->|"ProductUpdateDTO"| Controller
    Controller -->|"getActiveCategories()"| CatService
    CatService -->|"List<CategoryResponseDTO>"| Controller
    Controller -->|"Render edit form with prefilled DTO & categories"| View
    View -->|"Render HTML form (productCode readonly)"| Client

    Client -->|"2. HTTP POST /admin/products/edit/{id} (FormData)"| Security
    Security -->|"Authenticated"| Controller
    Controller -->|"Validate ProductUpdateDTO"| Validation
    Validation --"Has Errors (BindingResult)"--> Controller
    Controller --"Return View with Field Errors & categories"--> View
    
    Validation --"Valid"--> Controller
    Controller -->|"updateProduct(id, ProductUpdateDTO)"| ProdService
    ProdService -->|"findById(id)"| ProdRepo
    ProdService -->|"existsByNameAndIdNot(name, id)"| ProdRepo
    ProdService -->|"findById(categoryId)"| CatRepo
    CatRepo -->|"Category entity"| ProdService
    ProdService -->|"Save updated fields (keep productCode unchanged)"| ProdRepo
    ProdRepo -->|"SQL: UPDATE products SET name=?, price=?, stock_quantity=?, ... WHERE id=?"| Database
    Database -->|"Updated record"| ProdRepo
    ProdRepo -->|"Product entity"| ProdService
    ProdService -->|"ProductResponseDTO"| Controller
    Controller -->|"Redirect: GET /admin/products (PRG Pattern)"| Client
```

---

## 2. Sequence Diagram (Comprehensive Academic Flow)

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, bao gồm Giai đoạn tải dữ liệu form, kiểm tra mã sản phẩm bất biến, quy tắc kiểm tra trùng tên loại trừ chính mình (`existsByNameAndIdNot`), kiểm tra danh mục trong CSDL và các kịch bản ngoại lệ.

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Client)
    participant Security as Spring Security
    participant Controller as AdminProductController
    participant CatService as CategoryServiceImpl
    participant ProdService as ProductServiceImpl
    participant CatRepo as CategoryRepository
    participant ProdRepo as ProductRepository
    participant DB as SQL Server (products, categories)
    participant View as Thymeleaf (product-edit.html)

    %% GIAI ĐOẠN 1: TẢI FORM CHỈNH SỬA
    Note over Admin, View: Giai đoạn 1: Tải thông tin sản phẩm và hiển thị Form chỉnh sửa
    Admin->>Browser: Nhấn nút "Chỉnh sửa" tại dòng sản phẩm
    Browser->>Security: HTTP GET /admin/products/edit/{id}
    Security->>Controller: Forward request tới showEditForm(id, model, redirectAttributes)
    activate Controller
    
    Controller->>ProdService: getProductForEdit(id)
    activate ProdService
    ProdService->>ProdRepo: findByIdWithCategory(id)
    activate ProdRepo
    ProdRepo->>DB: SELECT p.*, c.* FROM products p INNER JOIN categories c ON p.category_id = c.id WHERE p.id = ?
    
    alt 2.1.1 Không tìm thấy sản phẩm theo ID (Exception Flow)
        DB-->>ProdRepo: Optional.empty()
        ProdRepo-->>ProdService: Optional.empty()
        deactivate ProdRepo
        ProdService-->>Controller: throw IllegalArgumentException("Sản phẩm mỹ phẩm không tồn tại!")
        deactivate ProdService
        Controller->>Controller: redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại!")
        Controller-->>Browser: HTTP 302 Redirect to /admin/products
        Browser-->>Admin: Chuyển về trang danh sách kèm Alert đỏ báo lỗi
    else 2. Tìm thấy sản phẩm hợp lệ
        DB-->>ProdRepo: Product entity
        activate ProdRepo
        ProdRepo-->>ProdService: Product entity
        deactivate ProdRepo
        activate ProdService
        ProdService->>ProdService: Map Product sang ProductUpdateDTO
        ProdService-->>Controller: ProductUpdateDTO
        deactivate ProdService
        
        Controller->>CatService: getActiveCategories()
        activate CatService
        CatService->>CatRepo: findByActiveTrueOrderByNameAsc()
        activate CatRepo
        CatRepo->>DB: SELECT * FROM categories WHERE active = 1 ORDER BY name ASC
        DB-->>CatRepo: List<Category>
        deactivate CatRepo
        CatService-->>Controller: List<CategoryResponseDTO>
        deactivate CatService
        
        Controller->>Controller: model.addAttribute("productDTO", dto)<br>model.addAttribute("categories", categories)
        Controller->>View: render("admin/product-edit", model)
        deactivate Controller
        activate View
        View-->>Browser: Render HTML Form chỉnh sửa (Điền sẵn dữ liệu, Mã SP readonly)
        deactivate View
        Browser-->>Admin: Hiển thị giao diện Form chỉnh sửa sản phẩm
    end

    %% GIAI ĐOẠN 2: SUBMIT FORM & XỬ LÝ
    Note over Admin, DB: Giai đoạn 2: Gửi dữ liệu cập nhật & Xử lý nghiệp vụ Backend
    alt 3.1 Quản trị viên nhấn nút "Hủy bỏ" (Alternate Flow)
        Admin->>Browser: Nhấn nút "Hủy bỏ"
        Browser->>Controller: HTTP GET /admin/products
        Controller-->>Browser: Chuyển hướng về trang danh sách
        Browser-->>Admin: Hiển thị trang danh sách (Không lưu thay đổi)
    else 3. Quản trị viên thay đổi thông tin và nhấn "Lưu thay đổi" (Main Flow)
        Admin->>Browser: Chỉnh sửa (name, categoryId, price, stockQuantity, description, imageUrl, active) và nhấn Lưu
        Browser->>Security: HTTP POST /admin/products/edit/{id} (FormData kèm _csrf)
        Security->>Controller: Forward request tới updateProduct(id, dto, bindingResult, ...)
        activate Controller
        Controller->>Controller: Kiểm tra validation JSR-380 (@Valid)
        
        alt 4.1.1 Lỗi validation JSR-380 (Tên rỗng, Giá <= 0, Tồn kho < 0, v.v.)
            Controller->>CatService: getActiveCategories()
            CatService-->>Controller: List<CategoryResponseDTO>
            Controller->>Controller: model.addAttribute("categories", list)
            Controller->>View: render("admin/product-edit", model kèm BindingResult errors)
            activate View
            View-->>Browser: Render HTML hiển thị lỗi đỏ inline dưới từng input vi phạm
            deactivate View
            Browser-->>Admin: Báo lỗi nhập liệu và giữ nguyên dữ liệu đang chỉnh sửa
        else Dữ liệu hợp lệ theo JSR-380
            Controller->>ProdService: updateProduct(id, ProductUpdateDTO)
            activate ProdService
            
            %% Tìm sản phẩm gốc
            ProdService->>ProdRepo: findById(id)
            activate ProdRepo
            ProdRepo->>DB: SELECT * FROM products WHERE id = ?
            DB-->>ProdRepo: Product entity
            deactivate ProdRepo
            
            %% Kiểm tra trùng tên loại trừ ID
            ProdService->>ProdRepo: existsByNameAndIdNot(name, id)
            activate ProdRepo
            ProdRepo->>DB: SELECT COUNT(*) FROM products WHERE name = ? AND id <> ?
            DB-->>ProdRepo: boolean exists
            deactivate ProdRepo
            
            alt 6.1.1 Tên sản phẩm đã bị dùng bởi sản phẩm khác (Exception Flow)
                ProdService-->>Controller: throw IllegalArgumentException("Tên sản phẩm '...' đã được sử dụng bởi sản phẩm khác!")
                Controller->>CatService: getActiveCategories()
                CatService-->>Controller: List<CategoryResponseDTO>
                Controller->>Controller: model.addAttribute("errorMessage", e.getMessage())
                Controller->>View: render("admin/product-edit", model)
                activate View
                View-->>Browser: Render HTML kèm Alert đỏ báo trùng tên sản phẩm
                deactivate View
                Browser-->>Admin: Hiển thị thông báo trùng tên
            else Tên sản phẩm hợp lệ
                %% Kiểm tra danh mục
                ProdService->>CatRepo: findById(categoryId)
                activate CatRepo
                CatRepo->>DB: SELECT * FROM categories WHERE id = ?
                DB-->>CatRepo: Category record
                deactivate CatRepo
                
                alt 7.1.1 Danh mục không tồn tại trong CSDL (Exception Flow)
                    ProdService-->>Controller: throw IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!")
                    Controller->>CatService: getActiveCategories()
                    CatService-->>Controller: List<CategoryResponseDTO>
                    Controller->>Controller: model.addAttribute("errorMessage", e.getMessage())
                    Controller->>View: render("admin/product-edit", model)
                    activate View
                    View-->>Browser: Render HTML kèm Alert đỏ báo lỗi danh mục
                    deactivate View
                    Browser-->>Admin: Hiển thị thông báo lỗi danh mục
                else Danh mục hợp lệ
                    Note over ProdService, DB: [BẢO VỆ BẤT BIẾN] Cập nhật các trường, giữ nguyên productCode
                    ProdService->>ProdService: Cập nhật name, price, stockQuantity, description, imageUrl, active, category
                    ProdService->>ProdRepo: save(Product entity)
                    activate ProdRepo
                    ProdRepo->>DB: UPDATE products SET name=?, price=?, stock_quantity=?, description=?, image_url=?, active=?, category_id=?, updated_at=? WHERE id=?
                    DB-->>ProdRepo: 1 row updated
                    ProdRepo-->>ProdService: Saved Product entity
                    deactivate ProdRepo
                    
                    ProdService->>ProdService: Map Product sang ProductResponseDTO
                    ProdService-->>Controller: ProductResponseDTO
                    deactivate ProdService
                    
                    Controller->>Controller: redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm mỹ phẩm thành công!")
                    Controller-->>Browser: HTTP 302 Redirect to /admin/products (PRG Pattern)
                    deactivate Controller
                    
                    Browser->>Controller: HTTP GET /admin/products
                    activate Controller
                    Controller->>View: render("admin/product-list", model với flash successMessage)
                    deactivate Controller
                    activate View
                    View-->>Browser: Render HTML danh sách sản phẩm kèm Flash Alert xanh lá
                    deactivate View
                    Browser-->>Admin: Hiển thị thông báo cập nhật sản phẩm thành công
                end
            end
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp thể hiện mối quan hệ giữa thực thể CSDL `Product`, `Category`, DTO `ProductUpdateDTO`, `ProductResponseDTO` và các tầng xử lý trong use case `admin-update-product`.

```mermaid
classDiagram
    class Product {
        <<entity>>
        -String id
        -String productCode
        -String name
        -BigDecimal price
        -Integer stockQuantity
        -String description
        -String imageUrl
        -Boolean active
        -Category category
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +getId() String
        +getProductCode() String
        +getName() String
        +getPrice() BigDecimal
        +getStockQuantity() Integer
        +getDescription() String
        +getImageUrl() String
        +getActive() Boolean
        +getCategory() Category
        +setName(String name) void
        +setPrice(BigDecimal price) void
        +setStockQuantity(Integer qty) void
        +setDescription(String desc) void
        +setImageUrl(String url) void
        +setActive(Boolean active) void
        +setCategory(Category category) void
    }

    class Category {
        <<entity>>
        -String id
        -String categoryCode
        -String name
        -Boolean active
        +getId() String
        +getCategoryCode() String
        +getName() String
        +getActive() Boolean
    }

    class ProductUpdateDTO {
        <<DTO>>
        -String id
        -String productCode
        -String name
        -String categoryId
        -BigDecimal price
        -Integer stockQuantity
        -String description
        -String imageUrl
        -Boolean active
        +getId() String
        +getProductCode() String
        +getName() String
        +getCategoryId() String
        +getPrice() BigDecimal
        +getStockQuantity() Integer
        +getDescription() String
        +getImageUrl() String
        +getActive() Boolean
    }

    class ProductResponseDTO {
        <<DTO>>
        -String id
        -String productCode
        -String name
        -BigDecimal price
        -Integer stockQuantity
        -String categoryName
        -Boolean active
        -LocalDateTime createdAt
    }

    class ProductRepository {
        <<interface>>
        +findByIdWithCategory(String id) Optional~Product~
        +existsByNameAndIdNot(String name, String id) boolean
        +existsByProductCode(String productCode) boolean
    }

    class ProductService {
        <<interface>>
        +getProductForEdit(String id) ProductUpdateDTO
        +updateProduct(String id, ProductUpdateDTO dto) ProductResponseDTO
    }

    class ProductServiceImpl {
        -ProductRepository productRepository
        -CategoryRepository categoryRepository
        +getProductForEdit(String id) ProductUpdateDTO
        +updateProduct(String id, ProductUpdateDTO dto) ProductResponseDTO
    }

    class AdminProductController {
        -ProductService productService
        -CategoryService categoryService
        +showEditForm(String id, Model model, RedirectAttributes ra) String
        +updateProduct(String id, ProductUpdateDTO dto, BindingResult br, RedirectAttributes ra, Model model) String
    }

    Product "0..*" --> "1" Category : @ManyToOne category
    ProductUpdateDTO ..> Product : mutates entity fields in Service (except productCode)
    Product ..> ProductResponseDTO : mapped to response DTO
    ProductServiceImpl ..|> ProductService : implements
    ProductServiceImpl --> ProductRepository : uses
    AdminProductController --> ProductService : delegates to
    AdminProductController --> CategoryService : delegates to
```
