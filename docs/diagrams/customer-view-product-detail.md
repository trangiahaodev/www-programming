# Mermaid Diagrams: customer-view-product-detail

Tài liệu thiết kế kiến trúc, luồng xử lý và thuật toán phân loại biến thể động cho Use Case **Khách hàng xem thông tin chi tiết sản phẩm và chọn phân loại biến thể** (`uc003d-customer-view-product-detail`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp SSR và xử lý sinh biến thể động (**Dynamic Variant Engine**) tại Service Layer trước khi đưa sang Thymeleaf View Engine và tương tác thời gian thực tại Client.

```mermaid
flowchart TD
    Client["Browser (Customer Client)"]
    Security["Spring Security Filter Chain<br>(PermitAll)"]
    Controller["ProductController<br>(GET /san-pham/{id})"]
    
    subgraph ServiceLayer ["Service Layer (Spring Boot @Service)"]
        Service["ProductServiceImpl"]
        DVE["populateProductVariants Engine<br>(Quy tắc: Mặt nạ / Son / Skincare / Nước hoa)"]
    end
    
    subgraph Repositories ["Data Access Layer (JPA)"]
        ProdRepo["ProductRepository"]
    end
    
    Database[("Microsoft SQL Server<br>(products, categories)")]
    View["Thymeleaf Engine<br>(customer/product-detail.html)"]

    Client -->|"HTTP GET /san-pham/{id}"| Security
    Security --> Controller
    Controller -->|"getProductDetail(id)"| Service
    Service -->|"findByIdWithCategory(id)"| ProdRepo
    ProdRepo -->|"JPQL JOIN FETCH category"| Database
    Database -->|"Product Entity"| ProdRepo
    ProdRepo -->|"Product Entity"| Service

    Service -->|"Phân tích name & categoryName"| DVE
    DVE -->|"Tạo danh sách ProductVariantDTO & Tiêu đề nhóm"| Service
    Service -->|"ProductDetailDTO (kèm variants & defaultVariant)"| Controller

    Controller -->|"getRelatedProducts(catId, prodId, 4)"| Service
    Service -->|"List&lt;ProductCardDTO&gt;"| Controller

    Controller -->|"Model (product, relatedProducts)"| View
    View -->|"Render HTML hoàn chỉnh"| Client

    Client -.->|"User clicks variant pill (JS Client-side event)"| Client
```

---

## 2. Sequence Diagram

Mô tả chi tiết chu kỳ Request-Response khi xem chi tiết sản phẩm, thuật toán sinh biến thể động trên server, và tương tác cập nhật giá tức thì tại client.

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Khách hàng (Customer)
    participant Browser as Trình duyệt (Client)
    participant Controller as ProductController
    participant Service as ProductServiceImpl
    participant Repo as ProductRepository
    participant DB as SQL Server
    participant View as Thymeleaf (product-detail.html)

    Customer->>Browser: Click vào thẻ sản phẩm hoặc mở link /san-pham/{id}
    Browser->>Controller: HTTP GET /san-pham/{id}
    activate Controller

    Controller->>Service: getProductDetail(id)
    activate Service

    Service->>Repo: findByIdWithCategory(id) (hoặc findByProductCodeWithCategory)
    activate Repo
    Repo->>DB: SELECT p FROM Product p LEFT JOIN FETCH p.category WHERE p.id = ?
    activate DB
    DB-->>Repo: Product record kèm Category
    deactivate DB
    Repo-->>Service: Optional<Product>
    deactivate Repo

    alt 2.1 Không tìm thấy sản phẩm hoặc sản phẩm bị khóa (Exception Flow)
        Service-->>Controller: Throw IllegalArgumentException("Không tìm thấy sản phẩm")
        Controller-->>Browser: Redirect 302 /san-pham (kèm flash message lỗi)
        Browser-->>Customer: Hiển thị thông báo sản phẩm không tồn tại
    else Tìm thấy sản phẩm hợp lệ (Main Flow)
        Service->>Service: convertToDetailDTO(product)
        
        rect rgb(255, 245, 247)
            Note over Service: Thuật toán nhận diện & sinh biến thể động (Dynamic Variant Engine)
            alt Sản phẩm là Mặt nạ (Mask / Mặt Nạ)
                Service->>Service: Gán variantGroupTitle = "Quy cách đóng gói:"<br>variants = [1 Miếng, Hộp 5 miếng, Hộp 10 miếng, Combo 2 hộp]
            else Sản phẩm là Son môi (Lipstick / Son / Tint)
                Service->>Service: Gán variantGroupTitle = "Tone màu thời thượng:"<br>variants = [#01 Đỏ Cam, #02 Hồng Đất, #03 Đỏ Ruby, #04 Cam Cháy]<br>kèm Color Swatches HEX
            else Sản phẩm là Nước hoa (Perfume / Nước hoa)
                Service->>Service: Gán variantGroupTitle = "Dung tích nước hoa:"<br>variants = [10ml Chiết, 50ml Chuẩn Hãng, 100ml Fullbox]
            else Sản phẩm Skincare thông dụng (Serum, Chống nắng, Kem dưỡng...)
                Service->>Service: Gán variantGroupTitle = "Dung tích / Kích thước:"<br>variants = [30ml, 50ml, 100ml]
            end
        end

        Service-->>Controller: ProductDetailDTO (Đầy đủ thuộc tính, variants và defaultVariant)
        deactivate Service

        Controller->>Service: getRelatedProducts(catId, id, 4)
        activate Service
        Service-->>Controller: List<ProductCardDTO>
        deactivate Service

        Controller->>View: render("customer/product-detail", model)
        activate View
        View-->>Browser: Render HTML giao diện chi tiết, khối phân loại và đánh giá
        deactivate View
        deactivate Controller
        Browser-->>Customer: Hiển thị đầy đủ thông tin sản phẩm và phân loại phù hợp

        opt 3.1 Khách hàng chuyển đổi phân loại (Alternate Flow - Realtime Client Update)
            Customer->>Browser: Click chọn biến thể (ví dụ: Hộp 5 miếng hoặc #02 Hồng Đất)
            Browser->>Browser: JavaScript đọc data-price, data-multiplier, data-stock<br>Cập nhật số tiền hiển thị, gạch giá cũ, nhãn phân loại và tồn kho
            Browser-->>Customer: Giá và thông tin phân loại thay đổi tức thì không cần reload trang
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp chi tiết các Entity, DTOs và quan hệ phục vụ use case `customer-view-product-detail`.

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
        -String origin
        -String description
        -String ingredients
        -String usageInstructions
        -Integer stock
        -Boolean isHot
        -Double rating
        -Integer reviewCount
        -Integer soldCount
    }

    class ProductDetailDTO {
        <<DTO>>
        -String id
        -String productCode
        -String name
        -String brand
        -String categoryName
        -String image
        -Double price
        -Integer discount
        -Double originalPrice
        -String origin
        -String description
        -String ingredients
        -String usageInstructions
        -Integer stock
        -Double rating
        -Integer reviewCount
        -Integer soldCount
        -String variantType
        -String variantGroupTitle
        -String variantIcon
        -List~ProductVariantDTO~ variants
        +getFormattedPrice() String
        +getFormattedOriginalPrice() String
        +getDefaultVariant() ProductVariantDTO
    }

    class ProductVariantDTO {
        <<DTO>>
        -String id
        -String name
        -String subName
        -String fullLabel
        -Double priceMultiplier
        -Double price
        -String colorHex
        -String badge
        -Integer stock
        -Boolean isDefault
        +getFormattedPrice() String
    }

    class ProductController {
        <<controller>>
        +productDetail(id, model) String
    }

    class ProductServiceImpl {
        <<service>>
        +getProductDetail(idOrCode) ProductDetailDTO
        -populateProductVariants(dto, product) void
        -roundPrice(raw) double
    }

    ProductController --> ProductServiceImpl
    ProductServiceImpl --> ProductDetailDTO
    ProductDetailDTO *-- ProductVariantDTO
    ProductServiceImpl ..> Product : reads
```

---

## 4. Flowchart: Thuật Toán Quyết Định Nhóm Phân Loại (Dynamic Variant Decision Tree)

```mermaid
flowchart TD
    Start(["Nhận Product & Base Price"]) --> Combined["Tạo combined = LOWER(name) + ' ' + LOWER(categoryName)"]
    
    Combined --> IsMask{"Chứa 'mặt nạ' / 'mask'?"}
    IsMask -- Đúng --> SetMask["Gán Type: PACK<br>Title: 'Quy cách đóng gói:'<br>Icon: 'box'<br>Variants: 1 Miếng (1.0x), Hộp 5 miếng (4.8x), Hộp 10 miếng (8.8x), Combo 2 hộp (16.5x)"]
    
    IsMask -- Sai --> IsLip{"Chứa 'son' / 'lipstick' / 'tint'?"}
    IsLip -- Đúng --> SetLip["Gán Type: COLOR<br>Title: 'Tone màu thời thượng:'<br>Icon: 'palette'<br>Variants: #01 Đỏ Cam, #02 Hồng Đất, #03 Đỏ Ruby, #04 Cam Cháy<br>(kèm colorHex tương ứng)"]
    
    IsLip -- Sai --> IsTone{"Chứa 'cushion' / 'phấn' / 'kem nền'?"}
    IsTone -- Đúng --> SetTone["Gán Type: TONE<br>Title: 'Chọn tone da & Phiên bản:'<br>Icon: 'palette'<br>Variants: Tone 21 Sáng, Tone 23 Tự Nhiên, Set + Lõi Refill (1.55x)"]
    
    IsTone -- Sai --> IsPerfume{"Chứa 'nước hoa' / 'perfume'?"}
    IsPerfume -- Đúng --> SetPerfume["Gán Type: CAPACITY<br>Title: 'Dung tích nước hoa:'<br>Icon: 'droplet'<br>Variants: 10ml Chiết (0.28x), 50ml Chuẩn Hãng (1.0x), 100ml Chai Lớn (1.68x)"]
    
    IsPerfume -- Sai --> IsBig{"Chứa 'tẩy trang' / 'dầu gội' / 'sữa tắm'?"}
    IsBig -- Đúng --> SetBig["Gán Type: CAPACITY<br>Title: 'Dung tích sản phẩm:'<br>Icon: 'droplet'<br>Variants: 150ml (0.65x), 250ml/300ml (1.0x), 500ml (1.6x)"]
    
    IsBig -- Sai --> IsDevice{"Chứa 'thiết bị' / 'máy' / 'massage'?"}
    IsDevice -- Đúng --> SetDevice["Gán Type: DEVICE<br>Title: 'Phiên bản màu sắc:'<br>Icon: 'sparkles'<br>Variants: Hồng Pastel, Tím Lilac, Xanh Mint"]
    
    IsDevice -- Sai --> SetDefault["Gán Type: CAPACITY (Mặc định Skincare)<br>Title: 'Dung tích / Kích thước:'<br>Icon: 'droplet'<br>Variants: 30ml (0.7x), 50ml (1.0x), 100ml (1.65x)"]

    SetMask --> Complete["Gán dto.setVariants(variants)<br>Làm tròn tiền: roundPrice()"]
    SetLip --> Complete
    SetTone --> Complete
    SetPerfume --> Complete
    SetBig --> Complete
    SetDevice --> Complete
    SetDefault --> Complete
    Complete --> End(["Trả về ProductDetailDTO hoàn chỉnh"])
```
