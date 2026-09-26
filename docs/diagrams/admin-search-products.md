# Mermaid Diagrams: admin-search-products

Tài liệu thiết kế kiến trúc và sơ đồ luồng xử lý chi tiết cho Use Case **Quản trị viên tìm kiếm sản phẩm mỹ phẩm theo tên hoặc mã SP** (`uc002e-admin-search-products`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) khi Admin thực hiện tìm kiếm sản phẩm mỹ phẩm qua thanh công cụ tìm kiếm trên trang danh sách sản phẩm. Kiến trúc đảm bảo truy vấn tối ưu qua cơ chế **JPQL JOIN FETCH kết hợp LIKE không phân biệt chữ hoa/thường** để triệt tiêu hoàn toàn vấn đề N+1 Query.

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

    Client -->|"1. HTTP GET /admin/products?keyword=Son&categoryId=&page=0&size=10"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    
    subgraph Spring_MVC_Controller_Layer ["Controller Layer"]
        Controller -->|"2. Parse & sanitize keyword (trim)"| Controller
        Controller -->|"3. Construct Pageable with Sort.by(DESC, 'createdAt')"| Controller
    end

    Controller -->|"4. getProducts(keyword, categoryId, pageable)"| ProdService
    Controller -->|"5. getActiveCategories()"| CatService
    
    subgraph Data_Access_Layer ["Data Access & N+1 Query Elimination"]
        ProdService -->|"6. searchProducts(keyword, categoryId, pageable)"| ProdRepo
        ProdRepo -->|"7a. JPQL: SELECT p FROM Product p JOIN FETCH p.category c WHERE LIKE %keyword%..."| Database
        ProdRepo -->|"7b. CountQuery: SELECT COUNT(p) FROM Product p WHERE LIKE %keyword%..."| Database
        Database -->|"8a. ResultSet (Matched Products with fetched Categories)"| ProdRepo
        Database -->|"8b. Total Count matching records"| ProdRepo
        ProdRepo -->|"9. Page<Product> (Fully initialized Category entities)"| ProdService
        
        CatService -->|"10. findByActiveTrueOrderByNameAsc()"| CatRepo
        CatRepo -->|"11. SELECT * FROM categories WHERE active = 1"| Database
        Database -->|"List<Category>"| CatRepo
        CatRepo -->|"List<Category>"| CatService
    end

    ProdService -->|"12. Convert to Page<ProductResponseDTO>"| Controller
    CatService -->|"13. Convert to List<CategoryResponseDTO>"| Controller
    
    Controller -->|"14. Populate Model (products, categories, keyword, categoryId, page, size)"| View
    View -->|"15. Render HTML table with search results, highlight keyword, and pagination links"| Client
```

---

## 2. Sequence Diagram (Academic Detailed Flow)

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu:
1. Tiếp nhận tham số `keyword` từ thanh tìm kiếm.
2. Xử lý logic tại Service và câu lệnh truy vấn JPQL `JOIN FETCH` trong Repository.
3. Cơ chế bảo toàn tham số tìm kiếm trên các nút liên kết phân trang.
4. Xử lý kịch bản kết quả tìm kiếm rỗng (Empty State).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Browser)
    participant Controller as AdminProductController
    participant ProdService as ProductServiceImpl
    participant ProdRepo as ProductRepository
    participant CatService as CategoryServiceImpl
    participant CatRepo as CategoryRepository
    participant DB as Microsoft SQL Server
    participant Thymeleaf as Thymeleaf Engine

    Admin->>Browser: Nhập từ khóa 'Son dưỡng' vào ô tìm kiếm & nhấn Enter / Lọc
    Browser->>Controller: GET /admin/products?keyword=Son+d%C6%B0%E1%BB%A1ng&categoryId=&page=0&size=10
    
    activate Controller
    Note over Controller: Chuẩn hóa tham số:<br/>- pageIndex = Math.max(0, page)<br/>- pageSize = 10, 20 hoặc 50<br/>- keyword = keyword.trim()
    
    Controller->>ProdService: getProducts("Son dưỡng", null, PageRequest.of(0, 10, Sort.DESC))
    activate ProdService
    
    ProdService->>ProdRepo: searchProducts("Son dưỡng", null, pageable)
    activate ProdRepo
    
    ProdRepo->>DB: 1. SELECT p.*, c.* FROM products p INNER JOIN categories c ON p.category_id = c.id<br/>WHERE (LOWER(p.name) LIKE '%son dưỡng%' OR LOWER(p.product_code) LIKE '%son dưỡng%')<br/>ORDER BY p.created_at DESC OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY
    ProdRepo->>DB: 2. SELECT COUNT(p.id) FROM products p WHERE (LOWER(p.name) LIKE '%son dưỡng%' OR LOWER(p.product_code) LIKE '%son dưỡng%')
    
    DB-->>ProdRepo: Trả về Danh sách 10 sản phẩm (kèm dữ liệu Category) & Tổng số bản ghi (Total Elements)
    deactivate DB
    
    ProdRepo-->>ProdService: Page<Product> (Chứa đầy đủ Category, KHÔNG phát sinh query phụ)
    deactivate ProdRepo
    
    Note over ProdService: Chuyển đổi Entity sang Page<ProductResponseDTO>
    ProdService-->>Controller: Page<ProductResponseDTO>
    deactivate ProdService
    
    Controller->>CatService: getActiveCategories()
    activate CatService
    CatService->>CatRepo: findByActiveTrueOrderByNameAsc()
    activate CatRepo
    CatRepo->>DB: SELECT * FROM categories WHERE active = 1 ORDER BY name ASC
    DB-->>CatRepo: List<Category>
    CatRepo-->>CatService: List<Category>
    deactivate CatRepo
    CatService-->>Controller: List<CategoryResponseDTO>
    deactivate CatService

    Note over Controller: Đưa dữ liệu vào Model:<br/>- 'products': Page<ProductResponseDTO><br/>- 'categories': List<CategoryResponseDTO><br/>- 'keyword': 'Son dưỡng'<br/>- 'currentPage': 0, 'pageSize': 10

    Controller->>Thymeleaf: Render "admin/product-list" (Model attributes)
    activate Thymeleaf
    
    alt Có sản phẩm phù hợp
        Thymeleaf-->>Browser: Trả về HTML chứa bảng danh sách sản phẩm tìm thấy,<br/>giữ nguyên từ khóa trong ô input & bảo toàn keyword trong URL phân trang
    else Không tìm thấy sản phẩm nào
        Thymeleaf-->>Browser: Trả về HTML hiển thị Empty State "Không tìm thấy sản phẩm nào phù hợp với từ khóa"<br/>kèm nút "Xóa bộ lọc"
    end
    deactivate Thymeleaf
    deactivate Controller

    Browser-->>Admin: Hiển thị kết quả tìm kiếm trực quan trên giao diện
```

---

## 3. Class Diagram (Data Structure & Component Architecture)

Mô tả cấu trúc lớp, phương thức tìm kiếm và mối quan hệ giữa các thành phần trong phân hệ.

```mermaid
classDiagram
    class AdminProductController {
        -ProductService productService
        -CategoryService categoryService
        +listProducts(String keyword, String categoryId, int page, int size, Model model) String
        +showCreateForm(Model model) String
        +createProduct(ProductCreateDTO productDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) String
        +showEditForm(String id, Model model, RedirectAttributes redirectAttributes) String
        +updateProduct(String id, ProductUpdateDTO productDTO, BindingResult bindingResult, Model model, RedirectAttributes redirectAttributes) String
        +deleteProduct(String id, RedirectAttributes redirectAttributes) String
    }

    class ProductService {
        <<interface>>
        +getProducts(String keyword, String categoryId, Pageable pageable) Page~ProductResponseDTO~
        +createProduct(ProductCreateDTO dto) ProductResponseDTO
        +getProductForEdit(String id) ProductUpdateDTO
        +updateProduct(String id, ProductUpdateDTO dto) ProductResponseDTO
        +deleteProduct(String id) void
    }

    class ProductServiceImpl {
        -ProductRepository productRepository
        -CategoryRepository categoryRepository
        +getProducts(String keyword, String categoryId, Pageable pageable) Page~ProductResponseDTO~
        +createProduct(ProductCreateDTO dto) ProductResponseDTO
        +getProductForEdit(String id) ProductUpdateDTO
        +updateProduct(String id, ProductUpdateDTO dto) ProductResponseDTO
        +deleteProduct(String id) void
        -convertToResponseDTO(Product product) ProductResponseDTO
    }

    class ProductRepository {
        <<interface>>
        +searchProducts(String keyword, String categoryId, Pageable pageable) Page~Product~
        +findByIdWithCategory(String id) Optional~Product~
        +existsByProductCode(String productCode) boolean
        +existsByName(String name) boolean
        +existsByNameAndIdNot(String name, String id) boolean
        +countByCategoryId(String categoryId) long
    }

    class Product {
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
    }

    class Category {
        -String id
        -String code
        -String name
        -String description
        -Boolean active
        -List~Product~ products
    }

    class ProductResponseDTO {
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
    }

    AdminProductController --> ProductService : delegates
    ProductService <|.. ProductServiceImpl : implements
    ProductServiceImpl --> ProductRepository : uses
    ProductRepository --> Product : queries
    Product --> Category : belongs to (Many-to-One)
    ProductServiceImpl ..> ProductResponseDTO : produces
```
