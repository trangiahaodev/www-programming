# Mermaid Diagrams: admin-create-product

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên thêm mới sản phẩm mỹ phẩm** (`uc002a-admin-create-product`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình tạo mới sản phẩm mỹ phẩm từ trình duyệt của Quản trị viên qua bộ lọc Spring Security, Spring MVC Controller, Bean Validation (JSR-380), Service Layer (kiểm tra nghiệp vụ và quan hệ danh mục), Spring Data JPA Repository tới cơ sở dữ liệu Microsoft SQL Server.

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
    View["Thymeleaf Engine<br>(admin/product-create.html)"]

    Client -->|"1. HTTP GET /admin/products/create"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    Controller -->|"getActiveCategories()"| CatService
    CatService -->|"findByActiveTrueOrderByNameAsc()"| CatRepo
    CatRepo -->|"Category entities"| CatService
    CatService -->|"List<CategoryResponseDTO>"| Controller
    Controller -->|"Render create form with categories & DTO"| View
    View -->|"Render HTML form"| Client

    Client -->|"2. HTTP POST /admin/products/create (FormData)"| Security
    Security -->|"Authenticated"| Controller
    Controller -->|"Validate ProductCreateDTO"| Validation
    Validation --"Has Errors (BindingResult)"--> Controller
    Controller --"Return View with Field Errors & categories"--> View
    
    Validation --"Valid"--> Controller
    Controller -->|"createProduct(ProductCreateDTO)"| ProdService
    ProdService -->|"existsByProductCode(code)"| ProdRepo
    ProdService -->|"existsByName(name)"| ProdRepo
    ProdService -->|"findById(categoryId)"| CatRepo
    CatRepo -->|"Category entity"| ProdService
    ProdService -->|"save(Product entity)"| ProdRepo
    ProdRepo -->|"SQL: INSERT INTO products (id, product_code, name, price, stock_quantity, category_id, ...)"| Database
    Database -->|"Saved record"| ProdRepo
    ProdRepo -->|"Product entity"| ProdService
    ProdService -->|"ProductResponseDTO"| Controller
    Controller -->|"Redirect: GET /admin/products (PRG Pattern)"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, bao quát toàn bộ quy trình tải danh mục lên form, kiểm tra quyền hạn, Alternate Flow (hủy bỏ) và các Exception Flows (lỗi JSR-380, trùng mã SP, trùng tên SP, danh mục không tồn tại, lỗi CSDL).

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
    participant View as Thymeleaf (product-create.html)

    %% Giai đoạn 1: Hiển thị Form tạo mới
    Note over Admin, View: Giai đoạn 1: Lấy danh sách danh mục & Hiển thị Form tạo mới
    Admin->>Browser: Nhấn nút "Thêm mới sản phẩm"
    Browser->>Security: HTTP GET /admin/products/create
    Security->>Controller: Forward request tới showCreateForm(model)
    activate Controller
    Controller->>CatService: getActiveCategories()
    activate CatService
    CatService->>CatRepo: findByActiveTrueOrderByNameAsc()
    activate CatRepo
    CatRepo->>DB: SELECT * FROM categories WHERE active = 1 ORDER BY name ASC
    DB-->>CatRepo: List<Category>
    deactivate CatRepo
    CatService-->>Controller: List<CategoryResponseDTO>
    deactivate CatService
    Controller->>Controller: model.addAttribute("categories", list)<br>model.addAttribute("productDTO", new ProductCreateDTO())
    Controller->>View: render("admin/product-create", model)
    deactivate Controller
    activate View
    View-->>Browser: Render HTML Form tạo mới sản phẩm (kèm dropdown danh mục)
    deactivate View
    Browser-->>Admin: Hiển thị màn hình nhập thông tin sản phẩm

    %% Giai đoạn 2: Submit Form & Xử lý
    Note over Admin, DB: Giai đoạn 2: Gửi dữ liệu & Xử lý nghiệp vụ Backend
    alt 3.1 Quản trị viên nhấn nút "Hủy bỏ" (Alternate Flow)
        Admin->>Browser: Nhấn "Hủy bỏ"
        Browser->>Controller: HTTP GET /admin/products
        Controller-->>Browser: Chuyển hướng về trang danh sách sản phẩm
        Browser-->>Admin: Hiển thị trang danh sách (Không lưu dữ liệu)
    else 3. Quản trị viên nhập thông tin và nhấn "Lưu sản phẩm" (Main Flow)
        Admin->>Browser: Nhập payload (productCode, name, categoryId, price, stockQuantity, description, imageUrl, active) và nhấn Lưu
        Browser->>Security: HTTP POST /admin/products/create (FormData kèm _csrf)
        Security->>Controller: Forward request tới createProduct(dto, bindingResult, ...)
        activate Controller
        Controller->>Controller: Kiểm tra validation JSR-380 (@Valid)
        
        alt 4.1.1 Lỗi validation JSR-380 (Giá <= 0, Tồn kho < 0, Tên rỗng, Chưa chọn DM)
            Controller->>CatService: getActiveCategories()
            CatService-->>Controller: List<CategoryResponseDTO>
            Controller->>Controller: model.addAttribute("categories", list)
            Controller->>View: render("admin/product-create", model kèm BindingResult errors)
            activate View
            View-->>Browser: Render HTML hiển thị thông báo lỗi đỏ inline dưới từng input
            deactivate View
            Browser-->>Admin: Báo lỗi nhập liệu và giữ nguyên dữ liệu đã điền
        else Dữ liệu hợp lệ theo JSR-380
            Controller->>ProdService: createProduct(ProductCreateDTO)
            activate ProdService
            
            %% Kiểm tra trùng mã SP
            ProdService->>ProdRepo: existsByProductCode(productCode)
            activate ProdRepo
            ProdRepo->>DB: SELECT COUNT(*) FROM products WHERE product_code = ?
            DB-->>ProdRepo: boolean exists
            deactivate ProdRepo
            
            alt 5.1.1 Mã sản phẩm đã tồn tại (Exception Flow)
                ProdService-->>Controller: throw IllegalArgumentException("Mã sản phẩm '...' đã tồn tại!")
                Controller->>CatService: getActiveCategories()
                CatService-->>Controller: List<CategoryResponseDTO>
                Controller->>Controller: model.addAttribute("errorMessage", e.getMessage())
                Controller->>View: render("admin/product-create", model)
                activate View
                View-->>Browser: Render HTML kèm Alert đỏ báo trùng mã sản phẩm
                deactivate View
                Browser-->>Admin: Hiển thị thông báo trùng mã sản phẩm
            else Mã sản phẩm hợp lệ
                %% Kiểm tra trùng tên SP
                ProdService->>ProdRepo: existsByName(name)
                activate ProdRepo
                ProdRepo->>DB: SELECT COUNT(*) FROM products WHERE name = ?
                DB-->>ProdRepo: boolean exists
                deactivate ProdRepo
                
                alt 5.2.1 Tên sản phẩm đã tồn tại (Exception Flow)
                    ProdService-->>Controller: throw IllegalArgumentException("Tên sản phẩm '...' đã tồn tại!")
                    Controller->>CatService: getActiveCategories()
                    CatService-->>Controller: List<CategoryResponseDTO>
                    Controller->>Controller: model.addAttribute("errorMessage", e.getMessage())
                    Controller->>View: render("admin/product-create", model)
                    activate View
                    View-->>Browser: Render HTML kèm Alert đỏ báo trùng tên sản phẩm
                    deactivate View
                    Browser-->>Admin: Hiển thị thông báo trùng tên sản phẩm
                else Tên sản phẩm hợp lệ
                    %% Kiểm tra categoryId thực tế trong DB
                    ProdService->>CatRepo: findById(categoryId)
                    activate CatRepo
                    CatRepo->>DB: SELECT * FROM categories WHERE id = ?
                    DB-->>CatRepo: Category record (hoặc Optional.empty())
                    deactivate CatRepo
                    
                    alt 6.1.1 Danh mục không tồn tại trong CSDL (Exception Flow)
                        ProdService-->>Controller: throw IllegalArgumentException("Danh mục mỹ phẩm được chọn không tồn tại trong hệ thống!")
                        Controller->>CatService: getActiveCategories()
                        CatService-->>Controller: List<CategoryResponseDTO>
                        Controller->>Controller: model.addAttribute("errorMessage", e.getMessage())
                        Controller->>View: render("admin/product-create", model)
                        activate View
                        View-->>Browser: Render HTML kèm Alert đỏ báo danh mục không hợp lệ
                        deactivate View
                        Browser-->>Admin: Hiển thị thông báo lỗi danh mục
                    else Danh mục tồn tại hợp lệ
                        ProdService->>ProdService: Map DTO to Product Entity (gán Category, imageUrl)
                        ProdService->>ProdRepo: save(Product entity)
                        activate ProdRepo
                        ProdRepo->>DB: INSERT INTO products (id, product_code, name, price, stock_quantity, description, image_url, active, category_id, created_at, updated_at) VALUES (...)
                        DB-->>ProdRepo: 1 row inserted
                        ProdRepo-->>ProdService: Product saved entity
                        deactivate ProdRepo
                        ProdService->>ProdService: Map Product to ProductResponseDTO
                        ProdService-->>Controller: ProductResponseDTO
                        deactivate ProdService
                        
                        Controller->>Controller: redirectAttributes.addFlashAttribute("successMessage", "Thêm mới sản phẩm mỹ phẩm thành công!")
                        Controller-->>Browser: HTTP 302 Redirect to /admin/products (PRG Pattern)
                        deactivate Controller
                        
                        Browser->>Controller: HTTP GET /admin/products
                        activate Controller
                        Controller->>View: render("admin/product-list", model với flash successMessage)
                        deactivate Controller
                        activate View
                        View-->>Browser: Render HTML danh sách sản phẩm kèm Flash Alert màu xanh thành công
                        deactivate View
                        Browser-->>Admin: Hiển thị thông báo tạo mới sản phẩm thành công
                    end
                end
            end
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp thể hiện mối quan hệ giữa thực thể CSDL `Product`, `Category` và các DTO `ProductCreateDTO`, `ProductResponseDTO` trong use case `admin-create-product`.

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
        +getCreatedAt() LocalDateTime
        +getUpdatedAt() LocalDateTime
        +setProductCode(String code) void
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
        -String description
        -Boolean active
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        +getId() String
        +getCategoryCode() String
        +getName() String
        +getActive() Boolean
    }

    class ProductCreateDTO {
        <<DTO>>
        -String productCode
        -String name
        -String categoryId
        -BigDecimal price
        -Integer stockQuantity
        -String description
        -String imageUrl
        -Boolean active
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
        -String description
        -String imageUrl
        -Boolean active
        -String categoryId
        -String categoryName
        -LocalDateTime createdAt
        +getId() String
        +getProductCode() String
        +getName() String
        +getPrice() BigDecimal
        +getStockQuantity() Integer
        +getCategoryId() String
        +getCategoryName() String
    }

    Product "0..*" --> "1" Category : belongs to (category_id)
    ProductCreateDTO ..> Product : mapped to Entity via Lombok Builder in Service
    Product ..> ProductResponseDTO : mapped to ResponseDTO after persistence
```
