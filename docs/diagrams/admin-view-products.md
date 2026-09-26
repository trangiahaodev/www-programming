# Mermaid Diagrams: admin-view-products

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên xem danh sách sản phẩm mỹ phẩm có phân trang, lọc và tìm kiếm** (`uc002d-admin-view-products`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình tải danh sách sản phẩm từ trình duyệt của Quản trị viên qua bộ lọc Spring Security, Spring MVC Controller, Service Layer (phân trang, sắp xếp và chuyển đổi DTO), Spring Data JPA Repository với cơ chế **JPQL JOIN FETCH** triệt tiêu N+1 Query tới cơ sở dữ liệu Microsoft SQL Server, kết xuất qua Thymeleaf Engine.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Security["Spring Security Filter Chain<br>(Role: ROLE_ADMIN)"]
    Controller["AdminProductController<br>(Spring MVC @Controller)"]
    ProdService["ProductService / ProductServiceImpl<br>(@Transactional(readOnly = true))"]
    CatService["CategoryService / CategoryServiceImpl<br>(@Transactional(readOnly = true))"]
    ProdRepo["ProductRepository<br>(Spring Data JPA / JPQL JOIN FETCH)"]
    CatRepo["CategoryRepository<br>(Spring Data JPA)"]
    Database[("Microsoft SQL Server<br>(Tables: products, categories)")]
    View["Thymeleaf Engine<br>(admin/product-list.html)"]

    Client -->|"1. HTTP GET /admin/products?keyword=&categoryId=&page=0&size=10"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    
    subgraph Spring_MVC_Controller_Layer ["Controller Layer"]
        Controller -->|"Parse & validate pagination params (page, size)"| Controller
        Controller -->|"Construct Pageable with Sort.by(DESC, 'createdAt')"| Controller
    end

    Controller -->|"getProducts(keyword, categoryId, pageable)"| ProdService
    Controller -->|"getActiveCategories()"| CatService
    
    subgraph Data_Access_Layer ["Data Access & N+1 Elimination"]
        ProdService -->|"searchProducts(keyword, categoryId, pageable)"| ProdRepo
        ProdRepo -->|"SQL 1: SELECT p.*, c.* FROM products p JOIN categories c ... (JOIN FETCH)"| Database
        ProdRepo -->|"SQL 2: SELECT COUNT(p.id) FROM products p ... (countQuery)"| Database
        Database -->|"ResultSet (Products + Categories in 1 single query)"| ProdRepo
        Database -->|"Total Count record"| ProdRepo
        ProdRepo -->|"Page<Product> (with initialized Categories)"| ProdService
        
        CatService -->|"findByActiveTrueOrderByNameAsc()"| CatRepo
        CatRepo -->|"SQL 3: SELECT * FROM categories WHERE active = 1 ORDER BY name ASC"| Database
        Database -->|"List<Category>"| CatRepo
        CatRepo -->|"List<Category>"| CatService
    end

    ProdService -->|"Map Page<Product> to Page<ProductResponseDTO>"| Controller
    CatService -->|"List<CategoryResponseDTO>"| Controller
    
    Controller -->|"Set Model attributes (products, categories, keyword, categoryId, ...)"| View
    View -->|"Render HTML table with pagination, thumbnails, stock badges"| Client
```

---

## 2. Sequence Diagram (Academic Detailed Flow)

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, thể hiện rõ cách thức truy vấn CSDL bằng `JOIN FETCH` để loại trừ hoàn toàn hiện tượng N+1 Query, các kịch bản tìm kiếm, phân trang và hiển thị trạng thái rỗng (Empty State).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Client)
    participant Security as Spring Security
    participant Controller as AdminProductController
    participant ProdService as ProductServiceImpl
    participant CatService as CategoryServiceImpl
    participant ProdRepo as ProductRepository
    participant CatRepo as CategoryRepository
    participant DB as SQL Server (products, categories)
    participant View as Thymeleaf (product-list.html)

    Note over Admin, View: Giai đoạn 1: Gửi yêu cầu xem danh sách (Tìm kiếm / Phân trang / Mặc định)
    Admin->>Browser: Truy cập /admin/products (hoặc submit form tìm kiếm, chọn trang)
    Browser->>Security: HTTP GET /admin/products?keyword={kw}&categoryId={catId}&page={p}&size={s}
    Security->>Controller: Forward request tới listProducts(keyword, categoryId, page, size, model)
    activate Controller
    
    Controller->>Controller: Chuẩn hóa tham số:<br>- pageIndex = max(0, page)<br>- pageSize = (10, 20, 50)<br>- pageable = PageRequest.of(pageIndex, pageSize, Sort.by(DESC, "createdAt"))

    %% Gọi ProductService
    Note over Controller, DB: Giai đoạn 2: Truy vấn dữ liệu Sản phẩm tối ưu chống N+1 Query
    Controller->>ProdService: getProducts(keyword, categoryId, pageable)
    activate ProdService
    ProdService->>ProdService: Chuẩn hóa cleanKeyword, cleanCategoryId (trim hoặc null)
    ProdService->>ProdRepo: searchProducts(cleanKeyword, cleanCategoryId, pageable)
    activate ProdRepo
    
    %% SQL JOIN FETCH
    Note over ProdRepo, DB: [QUY TẮC SỐNG CÒN] Dùng JPQL JOIN FETCH lấy trọn Category trong 1 câu SQL
    ProdRepo->>DB: SQL (Data Query): SELECT p.*, c.* FROM products p INNER JOIN categories c ON p.category_id = c.id WHERE (:kw IS NULL OR ...) AND (:catId IS NULL OR ...) ORDER BY p.created_at DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
    DB-->>ProdRepo: ResultSet (Toàn bộ dữ liệu Product kèm Category tương ứng)
    
    ProdRepo->>DB: SQL (Count Query): SELECT COUNT(p.id) FROM products p WHERE (:kw IS NULL OR ...) AND (:catId IS NULL OR ...)
    DB-->>ProdRepo: Total Count (e.g. 42 records)
    
    ProdRepo-->>ProdService: Page<Product> (Chứa danh sách Entity đã eager load Category)
    deactivate ProdRepo
    
    ProdService->>ProdService: Map Page<Product> sang Page<ProductResponseDTO> (Không phát sinh thêm SQL nào)
    ProdService-->>Controller: Page<ProductResponseDTO>
    deactivate ProdService

    %% Gọi CategoryService lấy danh mục cho dropdown filter
    Note over Controller, DB: Giai đoạn 3: Lấy danh sách danh mục hoạt động cho dropdown lọc
    Controller->>CatService: getActiveCategories()
    activate CatService
    CatService->>CatRepo: findByActiveTrueOrderByNameAsc()
    activate CatRepo
    CatRepo->>DB: SELECT * FROM categories WHERE active = 1 ORDER BY name ASC
    DB-->>CatRepo: List<Category>
    deactivate CatRepo
    CatService-->>Controller: List<CategoryResponseDTO>
    deactivate CatService

    %% Đóng gói Model và Render View
    Note over Controller, View: Giai đoạn 4: Đóng gói Model & Render giao diện Thymeleaf SSR
    Controller->>Controller: model.addAttribute("products", productPage)<br>model.addAttribute("categories", categories)<br>model.addAttribute("keyword", keyword)<br>model.addAttribute("categoryId", categoryId)<br>model.addAttribute("currentPage", pageIndex)<br>model.addAttribute("pageSize", pageSize)
    
    Controller->>View: render("admin/product-list", model)
    deactivate Controller
    activate View
    
    alt Trường hợp 1: Có dữ liệu sản phẩm (productPage.hasContent() == true)
        View-->>Browser: Render bảng HTML (Hình ảnh tĩnh, Mã SP, Tên SP, Danh mục, Giá bán, Tồn kho, Trạng thái, Cột thao tác) + Phân trang (Pagination Bar)
    else Trường hợp 2: Không có kết quả (productPage.isEmpty() == true)
        View-->>Browser: Render Empty State (Biểu tượng trống, thông báo "Không tìm thấy sản phẩm nào phù hợp", nút Xóa bộ lọc)
    end
    deactivate View

    Browser-->>Admin: Hiển thị giao diện danh sách sản phẩm chuẩn Minimalism & Subtle Neo-brutalism
```

---

## 3. N+1 Query Elimination Breakdown (So sánh Cơ chế)

Sơ đồ đối sánh sự khác biệt then chốt giữa truy vấn thông thường gây lỗi N+1 Query và truy vấn tối ưu học thuật bằng `JOIN FETCH`:

```mermaid
flowchart TD
    subgraph NG_Approach ["❌ Lỗi N+1 Query (Truy vấn ngây thơ không có JOIN FETCH)"]
        NG_Q1["1. SQL Lấy 10 sản phẩm:<br>SELECT * FROM products ORDER BY created_at DESC LIMIT 10"]
        NG_P1["Product #1 -> Gọi getCategory().getName() -> SQL #2: SELECT * FROM categories WHERE id = ?"]
        NG_P2["Product #2 -> Gọi getCategory().getName() -> SQL #3: SELECT * FROM categories WHERE id = ?"]
        NG_PN["Product #10 -> Gọi getCategory().getName() -> SQL #11: SELECT * FROM categories WHERE id = ?"]
        NG_Sum["Tổng cộng: 1 + 10 = 11 truy vấn SQL độc lập (N+1 Problem)!"]
        
        NG_Q1 --> NG_P1
        NG_Q1 --> NG_P2
        NG_Q1 --> NG_PN
        NG_P1 & NG_P2 & NG_PN --> NG_Sum
    end

    subgraph OK_Approach ["✅ Giải pháp Tối ưu (JPQL JOIN FETCH p.category)"]
        OK_Q1["1. SQL Lấy 10 sản phẩm kèm danh mục (1 câu SQL duy nhất):<br>SELECT p.*, c.* FROM products p JOIN categories c ON p.category_id = c.id ..."]
        OK_Q2["2. SQL Đếm tổng số bản ghi (Count Query):<br>SELECT COUNT(p.id) FROM products p WHERE ..."]
        OK_Map["Map Entity sang ProductResponseDTO:<br>Category đã có sẵn trong Persistence Context, không gửi thêm SQL nào!"]
        OK_Sum["Tổng cộng: Duy nhất 2 câu SQL độc lập (1 Data + 1 Count) dù hiển thị 10, 50 hay 100 sản phẩm!"]
        
        OK_Q1 --> OK_Map
        OK_Q2 --> OK_Map
        OK_Map --> OK_Sum
    end
```

---

## 4. Class Diagram

Mô hình cấu trúc lớp thể hiện mối quan hệ giữa thực thể CSDL `Product`, `Category`, `ProductResponseDTO`, tầng Repository và Controller trong use case `admin-view-products`.

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
        +getActive() Boolean
        +getCreatedAt() LocalDateTime
    }

    class CategoryResponseDTO {
        <<DTO>>
        -String id
        -String categoryCode
        -String name
        -Boolean active
        +getId() String
        +getCategoryCode() String
        +getName() String
        +getActive() Boolean
    }

    class ProductRepository {
        <<interface>>
        +searchProducts(String keyword, String categoryId, Pageable pageable) Page~Product~
        +existsByProductCode(String productCode) boolean
        +existsByName(String name) boolean
        +countByCategoryId(String categoryId) long
    }

    class ProductService {
        <<interface>>
        +createProduct(ProductCreateDTO dto) ProductResponseDTO
        +getProducts(String keyword, String categoryId, Pageable pageable) Page~ProductResponseDTO~
    }

    class ProductServiceImpl {
        -ProductRepository productRepository
        -CategoryRepository categoryRepository
        +getProducts(String keyword, String categoryId, Pageable pageable) Page~ProductResponseDTO~
        -convertToResponseDTO(Product product) ProductResponseDTO
    }

    class AdminProductController {
        -ProductService productService
        -CategoryService categoryService
        +listProducts(String keyword, String categoryId, int page, int size, Model model) String
        +showCreateForm(Model model) String
        +createProduct(...) String
    }

    Product "0..*" --> "1" Category : @ManyToOne(fetch = FetchType.LAZY) category
    Product ..> ProductResponseDTO : mapped to DTO via Stream/Map
    Category ..> CategoryResponseDTO : mapped to DTO
    ProductServiceImpl ..|> ProductService : implements
    ProductServiceImpl --> ProductRepository : uses
    AdminProductController --> ProductService : delegates to
    AdminProductController --> CategoryService : delegates to
```
