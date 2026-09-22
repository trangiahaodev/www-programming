# Mermaid Diagrams: customer-view-products

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Khách hàng duyệt danh sách sản phẩm, tìm kiếm và lọc danh mục** (`uc002d-customer-view-products`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) từ trình duyệt người dùng qua tầng bảo mật Spring Security, Spring MVC Controller, Service Layer, Spring Data JPA Repository đến cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Customer Client)"]
    Security["Spring Security Filter Chain<br>(Public Access: PermitAll)"]
    Controller["ProductController<br>(Spring MVC @Controller: /san-pham)"]
    Service["ProductService / ProductServiceImpl<br>(Service Layer @Transactional)"]
    CatService["CategoryService / CategoryServiceImpl<br>(Service Layer @Transactional)"]
    ProdRepo["ProductRepository<br>(Spring Data JPA / Hibernate)"]
    CatRepo["CategoryRepository<br>(Spring Data JPA / Hibernate)"]
    Database[("Microsoft SQL Server<br>(Tables: products, categories)")]
    View["Thymeleaf Engine<br>(customer/product-list.html)"]

    Client -->|"HTTP GET /san-pham?keyword=&category=&sort=&page=0&size=12"| Security
    Security -->|"Permit All (Anonymous / Authenticated)"| Controller
    Controller -->|"searchProducts(keyword, category, sort, pageable)"| Service
    Controller -->|"getCategories(null, PageRequest(0, 50))"| CatService
    Service -->|"searchProducts(keyword, category, pageable)"| ProdRepo
    CatService -->|"findAllCategoriesActive()"| CatRepo
    ProdRepo -->|"JPQL: SELECT p FROM Product p JOIN p.category c WHERE ..."| Database
    CatRepo -->|"JPQL: SELECT c FROM Category c WHERE c.active = true"| Database
    Database -->|"ResultSet (products, totalCount, categories)"| ProdRepo
    ProdRepo -->|"Page&lt;Product&gt;"| Service
    Service -->|"map(convertToCardDTO) -> Page&lt;ProductCardDTO&gt;"| Controller
    CatService -->|"Page&lt;CategoryResponseDTO&gt;"| Controller
    Controller -->|"Model attributes (products, categories, selectedCategory, sort, pagination)"| View
    View -->|"Rendered HTML Response (Sticky Glassmorphism Bar, Product Grid)"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response, bao gồm Main Flow (duyệt sản phẩm), Alternate Flows (tìm kiếm theo từ khóa, lọc theo danh mục, sắp xếp giá/bán chạy/mới nhất, chuyển trang) và Exception Flows (lỗi kết nối cơ sở dữ liệu, không tìm thấy sản phẩm).

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Khách hàng (Customer)
    participant Browser as Trình duyệt (Client)
    participant Security as Spring Security
    participant Controller as ProductController
    participant Service as ProductServiceImpl
    participant CatService as CategoryServiceImpl
    participant ProdRepo as ProductRepository
    participant CatRepo as CategoryRepository
    participant DB as SQL Server (products & categories)
    participant View as Thymeleaf (product-list.html)

    Customer->>Browser: Truy cập menu "Sản phẩm" hoặc nhập URL /san-pham
    Browser->>Security: HTTP GET /san-pham?keyword={kw}&category={cat}&sort={sort}&page={page}&size=12
    Security->>Controller: Forward request tới listProducts(keyword, category, sort, page, size, model)
    activate Controller

    Controller->>Controller: Chuẩn hóa pageNumber = max(0, page), pageSize = 12
    Controller->>CatService: getCategories(null, PageRequest.of(0, 50))
    activate CatService
    CatService->>CatRepo: findAllActiveCategories()
    activate CatRepo
    CatRepo->>DB: SELECT c FROM Category c WHERE c.active = 1
    DB-->>CatRepo: Danh sách 50 danh mục hoạt động
    CatRepo-->>CatService: List<Category>
    deactivate CatRepo
    CatService-->>Controller: List<CategoryResponseDTO>
    deactivate CatService

    Controller->>Service: searchProducts(keyword, category, sort, pageable)
    activate Service

    Service->>Service: Phân tích tham số sort (popular, hot, price-asc, price-desc, newest)
    Service->>ProdRepo: searchProducts(searchKeyword, categoryParam, pageableWithSort)
    activate ProdRepo

    alt 2.1 Lỗi kết nối Database (Exception Flow)
        ProdRepo->>DB: SELECT p FROM Product p JOIN p.category c ...
        DB-->>ProdRepo: DataAccessException / Connection Timeout
        ProdRepo-->>Service: Throw DataAccessException
        Service-->>Controller: Throw Exception
        Controller->>View: render("error/500" hoặc redirect)
        View-->>Browser: Hiển thị thông báo "Không thể tải danh sách sản phẩm"
    else Truy vấn Database thành công (Main Flow)
        ProdRepo->>DB: SELECT p FROM Product p JOIN p.category c WHERE (is_active=1 AND (:kw IS NULL OR LOWER(p.name) LIKE ...) AND (:cat IS NULL OR c.name = ...))
        activate DB
        DB-->>ProdRepo: ResultSet (Product records & Total Count)
        deactivate DB

        ProdRepo-->>Service: Page<Product>
        deactivate ProdRepo

        Service->>Service: Map Product sang ProductCardDTO (tính formattedPrice, discount, badge)
        Service-->>Controller: Page<ProductCardDTO>
        deactivate Service

        Controller->>Controller: Tính startItem, endItem, totalElements<br>model.addAttribute("products", page.getContent())<br>model.addAttribute("categories", categories)<br>model.addAttribute("selectedCategory", category)<br>model.addAttribute("sortBy", sort)<br>model.addAttribute("keyword", keyword)

        alt 4.1 Danh sách rỗng không có sản phẩm khớp từ khóa (Alternate Flow)
            Controller->>View: render("customer/product-list", model với products = [])
            activate View
            View-->>Browser: Render HTML kèm Empty State ("Không tìm thấy sản phẩm nào")
            deactivate View
        else Tìm thấy sản phẩm (Main Flow / Filter / Sort)
            Controller->>View: render("customer/product-list", model)
            activate View
            View-->>Browser: Render HTML lưới sản phẩm, sticky filter bar, nút cuộn & modal A-Z
            deactivate View
        end

        deactivate Controller
        Browser-->>Customer: Hiển thị giao diện danh sách sản phẩm sống động
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp chi tiết giữa Entity, DTO, Repository, Service và Controller tham gia trong use case `customer-view-products`.

```mermaid
classDiagram
    class Product {
        <<entity>>
        -String id
        -String productCode
        -String name
        -String brand
        -Double price
        -Integer discount
        -String image
        -Category category
        -Integer stock
        -Boolean active
        -Boolean isHot
        -Boolean isNew
        -Double rating
        -Integer reviewCount
        -Integer soldCount
    }

    class Category {
        <<entity>>
        -String id
        -String categoryCode
        -String name
        -Boolean active
    }

    class ProductCardDTO {
        <<DTO>>
        -String id
        -String name
        -String brand
        -String categoryName
        -String image
        -Double price
        -Integer discount
        -Double originalPrice
        -Boolean isOutOfStock
        -Boolean isHot
        -Boolean isNew
        -Double rating
        -Integer reviewCount
        -Integer soldCount
        +getFormattedPrice() String
        +getFormattedOriginalPrice() String
    }

    class CategoryResponseDTO {
        <<DTO>>
        -String id
        -String name
        -String categoryCode
    }

    class ProductController {
        <<controller>>
        -ProductService productService
        -CategoryService categoryService
        +listProducts(keyword, category, sort, page, size, model) String
    }

    class ProductService {
        <<interface>>
        +searchProducts(keyword, category, sort, pageable) Page~ProductCardDTO~
    }

    class ProductServiceImpl {
        <<service>>
        -ProductRepository productRepository
        +searchProducts(keyword, category, sort, pageable) Page~ProductCardDTO~
        -convertToCardDTO(product) ProductCardDTO
    }

    class ProductRepository {
        <<repository>>
        +searchProducts(keyword, category, pageable) Page~Product~
    }

    ProductController --> ProductService
    ProductController --> CategoryService
    ProductServiceImpl ..|> ProductService
    ProductServiceImpl --> ProductRepository
    ProductRepository --> Product
    Product --> Category
    ProductServiceImpl ..> ProductCardDTO : transforms to
    CategoryService ..> CategoryResponseDTO : transforms to
```
