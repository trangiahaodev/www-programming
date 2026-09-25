# Mermaid Diagrams: customer-view-home

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Khách hàng trải nghiệm trang chủ PinkyCloud** (`uc004d-customer-view-home`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) từ trình duyệt người dùng qua tầng bảo mật Spring Security, `HomeController`, các Services nghiệp vụ (`ProductService`, `HomeContentService`), Repositories đến cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Customer Client)"]
    Security["Spring Security Filter Chain<br>(PermitAll: /, /trang-chu)"]
    Controller["HomeController<br>(Spring MVC @Controller: /)"]
    
    subgraph ServiceLayer ["Service Layer (@Service)"]
        ProdService["ProductServiceImpl<br>(getRandomFeaturedProducts, getHotProducts)"]
        HomeService["HomeContentServiceImpl<br>(getOffices, getNews, getVouchers)"]
    end
    
    subgraph Repositories ["Data Access Layer (JPA)"]
        ProdRepo["ProductRepository"]
    end
    
    Database[("Microsoft SQL Server<br>(products, categories)")]
    View["Thymeleaf Engine<br>(customer/home.html)"]

    Client -->|"HTTP GET / hoặc /trang-chu"| Security
    Security --> Controller
    
    Controller -->|"getRandomFeaturedProducts(4)<br>(Random 4 sản phẩm Bento Grid)"| ProdService
    Controller -->|"getHotProducts(6)<br>(6 sản phẩm Flash Deals)"| ProdService
    Controller -->|"getOffices()<br>(Danh sách showroom chi nhánh)"| HomeService
    Controller -->|"getNews()<br>(Tin tức & xu hướng làm đẹp)"| HomeService
    Controller -->|"getVouchers()<br>(Mã giảm giá độc quyền)"| HomeService

    ProdService -->|"findAll(is_active=1) & Collections.shuffle()"| ProdRepo
    ProdRepo -->|"SELECT p FROM Product p WHERE p.active = 1"| Database
    Database -->|"Products records"| ProdRepo
    ProdRepo -->|"List&lt;Product&gt;"| ProdService

    ProdService -->|"4 Random Products & 6 Flash Deals"| Controller
    HomeService -->|"Offices, News & Vouchers DTOs"| Controller

    Controller -->|"Model attributes (featuredProducts, flashDeals, offices, news, vouchers)"| View
    View -->|"Render HTML hoàn chỉnh (Banner Full-width, Bento Grid, Brand Carousel, Showroom)"| Client
```

---

## 2. Sequence Diagram

Mô tả chu kỳ Request-Response khi người dùng truy cập trang chủ, quy trình random Bento Grid tự động mỗi lần tải trang, tải flash deals, showroom và bài viết nổi bật.

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Khách hàng (Customer)
    participant Browser as Trình duyệt (Client)
    participant Controller as HomeController
    participant ProdService as ProductServiceImpl
    participant HomeService as HomeContentServiceImpl
    participant ProdRepo as ProductRepository
    participant DB as SQL Server
    participant View as Thymeleaf (home.html)

    Customer->>Browser: Truy cập trang chủ PinkyCloud (URL / hoặc /trang-chu)
    Browser->>Controller: HTTP GET /
    activate Controller

    rect rgb(255, 240, 245)
        Note over Controller, ProdService: 1. Lấy 4 sản phẩm Bento Grid tự động Random mỗi lần tải trang
        Controller->>ProdService: getRandomFeaturedProducts(4)
        activate ProdService
        ProdService->>ProdRepo: findAllActiveProducts()
        activate ProdRepo
        ProdRepo->>DB: SELECT p FROM Product p WHERE p.active = 1
        activate DB
        DB-->>ProdRepo: Danh sách tất cả sản phẩm đang hoạt động
        deactivate DB
        ProdRepo-->>ProdService: List<Product>
        deactivate ProdRepo
        ProdService->>ProdService: Collections.shuffle(copyList)<br>Lấy top 4 sản phẩm ngẫu nhiên & map sang ProductCardDTO
        ProdService-->>Controller: List<ProductCardDTO> (4 sản phẩm Bento ngẫu nhiên)
        deactivate ProdService
    end

    rect rgb(255, 248, 240)
        Note over Controller, ProdService: 2. Lấy 6 sản phẩm Flash Deals ưu đãi cao nhất
        Controller->>ProdService: getHotProducts(6)
        activate ProdService
        ProdService->>ProdRepo: findHotProducts(PageRequest.of(0, 6, Sort DESC discount, soldCount))
        activate ProdRepo
        ProdRepo->>DB: SELECT TOP 6 p FROM Product p WHERE p.active = 1 ORDER BY discount DESC, soldCount DESC
        activate DB
        DB-->>ProdRepo: 6 sản phẩm khuyến mãi cao
        deactivate DB
        ProdRepo-->>ProdService: List<Product>
        deactivate ProdRepo
        ProdService-->>Controller: List<ProductCardDTO> (6 sản phẩm Flash Deals)
        deactivate ProdService
    end

    rect rgb(240, 255, 245)
        Note over Controller, HomeService: 3. Lấy hệ thống chi nhánh, tin tức làm đẹp & voucher
        Controller->>HomeService: getOffices()
        activate HomeService
        HomeService-->>Controller: List<OfficeDTO> (Hà Nội, TP.HCM, Đà Nẵng, Cần Thơ)
        deactivate HomeService

        Controller->>HomeService: getNews()
        activate HomeService
        HomeService-->>Controller: List<NewsDTO> (Bài viết chăm sóc da, xu hướng makeup)
        deactivate HomeService

        Controller->>HomeService: getVouchers()
        activate HomeService
        HomeService-->>Controller: List<VoucherDTO> (PINKY50K, FREESHIP, BEAUTY100K)
        deactivate HomeService
    end

    Controller->>View: render("customer/home", model)
    activate View
    View-->>Browser: Render HTML trang chủ (Banner tràn viền, Bento Grid, Flash Deal 6 items, Brand Carousel, Showrooms, Tin tức)
    deactivate View
    deactivate Controller
    Browser-->>Customer: Trải nghiệm giao diện trang chủ chuyên nghiệp và hiện đại
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp chi tiết giữa Controller, Services, Repositories và DTOs phục vụ trang chủ `customer-view-home`.

```mermaid
classDiagram
    class HomeController {
        <<controller>>
        -ProductService productService
        -HomeContentService homeContentService
        +home(model) String
    }

    class ProductService {
        <<interface>>
        +getRandomFeaturedProducts(limit) List~ProductCardDTO~
        +getHotProducts(limit) List~ProductCardDTO~
    }

    class HomeContentService {
        <<interface>>
        +getOffices() List~OfficeDTO~
        +getNews() List~NewsDTO~
        +getVouchers() List~VoucherDTO~
    }

    class ProductCardDTO {
        <<DTO>>
        -String id
        -String name
        -String brand
        -Double price
        -Integer discount
        -Double originalPrice
        -String image
        -Boolean isHot
        -Double rating
        +getFormattedPrice() String
    }

    class OfficeDTO {
        <<DTO>>
        -String id
        -String name
        -String address
        -String phone
        -String openingHours
        -String image
    }

    class NewsDTO {
        <<DTO>>
        -String id
        -String title
        -String excerpt
        -String author
        -String publishedDate
        -String image
        -String tag
    }

    class VoucherDTO {
        <<DTO>>
        -String code
        -String title
        -String discountText
        -String minOrderText
        -String expiryDate
    }

    HomeController --> ProductService
    HomeController --> HomeContentService
    ProductService ..> ProductCardDTO : returns
    HomeContentService ..> OfficeDTO : returns
    HomeContentService ..> NewsDTO : returns
    HomeContentService ..> VoucherDTO : returns
```
