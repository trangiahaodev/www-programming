# Mermaid Diagrams: uc002d-admin-order-detail

Tài liệu thiết kế kiến trúc và mô hình luồng tương tác chi tiết cho Use Case **Quản trị viên xem chi tiết một đơn hàng trực tuyến** (`admin-order-detail-d` / `uc002d-admin-order-detail`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp kiến trúc Server-Side Rendering (SSR) từ Trình duyệt (Admin Client) qua bộ lọc phân quyền Spring Security, Spring MVC Controller, Service Layer, Spring Data JPA Repository tới Microsoft SQL Server và hiển thị qua Thymeleaf Engine.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Security["Spring Security Filter Chain<br>(Role Check: ROLE_ADMIN)"]
    Controller["AdminOrderController<br>(Spring MVC @Controller)"]
    Service["OrderService / OrderServiceImpl<br>(Service Layer @Transactional readOnly)"]
    Repository["OrderRepository<br>(Spring Data JPA / Hibernate)"]
    Database[("Microsoft SQL Server<br>(Tables: orders, order_items)")]
    View["Thymeleaf Template Engine<br>(admin/order-detail.html)"]

    Client -->|"HTTP GET /admin/orders/{id}"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    Security -.->|"Unauthorized / Expired Session (HTTP 302 /login)"| Client
    Controller -->|"Validate ID (blank & UUID regex)"| Controller
    Controller -->|"getOrderDetailById(trimmedId)"| Service
    Service -->|"findByIdWithItems(id)"| Repository
    Repository -->|"JPQL: SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id"| Database
    Database -->|"ResultSet (order record & order_items records)"| Repository
    Repository -->|"Optional[Order]"| Service
    Service -->|"Map Entity to OrderDetailResponseDTO + List[OrderItemResponseDTO]"| Controller
    Controller -->|"Model attribute 'order' (OrderDetailResponseDTO)"| View
    View -->|"Rendered HTML Response"| Client
```

---

## 2. Sequence Diagram

Mô tả chu kỳ vòng đời của một Request-Response từ lúc Quản trị viên nhấp xem chi tiết một đơn hàng, biểu diễn đầy đủ Main Flow, Alternate Flows (truy cập trực tiếp URL, quay lại danh sách qua nút/breadcrumb) và Exception Flows (hết hạn phiên/thiếu quyền, mã đơn rỗng, mã đơn sai định dạng UUID, đơn hàng không tồn tại, đơn hàng không có mặt hàng, lỗi kết nối CSDL).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Client)
    participant Security as Spring Security Filter Chain
    participant Controller as AdminOrderController
    participant Service as OrderServiceImpl
    participant Repository as OrderRepository
    participant DB as SQL Server (orders, order_items)
    participant View as Thymeleaf View (admin/order-detail)

    Admin->>Browser: Nhấp vào mã đơn hàng hoặc truy cập URL /admin/orders/{id}
    Browser->>Security: HTTP GET /admin/orders/{id}

    alt 2.1.1 Hết hạn phiên đăng nhập hoặc không có quyền ROLE_ADMIN (Exception Flow)
        Security-->>Browser: HTTP 302 Redirect /login (thông báo hết hạn quyền truy cập)
        Browser-->>Admin: Hiển thị trang đăng nhập
    else Quản trị viên có quyền ROLE_ADMIN hợp lệ (Main Flow)
        Security->>Controller: Forward request tới viewOrderDetail(id, redirectAttributes, model)

        Controller->>Controller: Kiểm tra id rỗng: (id == null || id.trim().isEmpty())

        alt 2.2.1 Mã đơn hàng bị để trống hoặc rỗng (Exception Flow)
            Controller-->>Browser: HTTP 302 Redirect /admin/orders (flash: "Mã đơn hàng không được để trống.")
            Browser-->>Admin: Quay lại danh sách đơn hàng kèm cảnh báo lỗi
        else Mã đơn hàng không rỗng
            Controller->>Controller: Kiểm tra định dạng UUID: UUID_PATTERN.matcher(trimmedId).matches()

            alt 2.3.1 Mã đơn hàng không đúng định dạng UUID (Exception Flow)
                Controller-->>Browser: HTTP 302 Redirect /admin/orders (flash: "Mã đơn hàng không đúng định dạng.")
                Browser-->>Admin: Quay lại danh sách đơn hàng kèm cảnh báo lỗi định dạng
            else Mã đơn hàng đúng định dạng UUID (Main Flow)
                Controller->>Service: getOrderDetailById(trimmedId)

                Service->>Repository: findByIdWithItems(trimmedId)

                Repository->>DB: SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id

                alt 2.6.1 Lỗi kết nối CSDL hoặc lỗi truy vấn hệ thống (Exception Flow)
                    DB-->>Repository: Ném DataAccessException / SQLServerException
                    Repository-->>Service: Ném DataAccessException
                    Service-->>Controller: Lan truyền Exception
                    Controller-->>Browser: Forward error/500 ("Không thể truy vấn dữ liệu chi tiết đơn hàng")
                    Browser-->>Admin: Hiển thị trang báo lỗi máy chủ nội bộ (HTTP 500)
                else Truy vấn Database thành công (Main Flow)
                    DB-->>Repository: Trả về bản ghi Order và danh sách OrderItem liên quan
                    Repository-->>Service: return Optional[Order]

                    alt 2.4.1 Đơn hàng không tồn tại trong CSDL (Exception Flow)
                        Service-->>Controller: throw OrderNotFoundException("Đơn hàng yêu cầu không tồn tại...")
                        Controller-->>Browser: HTTP 302 Redirect /admin/orders (flash errorMessage)
                        Browser-->>Admin: Quay lại danh sách đơn hàng kèm banner thông báo lỗi
                    else Đơn hàng tồn tại (Main Flow)
                        alt 2.5.1 Đơn hàng không có mặt hàng nào (order.items rỗng hoặc null)
                            Service->>Service: items = Collections.emptyList(), totalItems = 0
                        else Đơn hàng có các mặt hàng (Main Flow)
                            Service->>Service: Map List[OrderItem] sang List[OrderItemResponseDTO], tính sum(quantity) = totalItems
                        end

                        Service->>Service: Build OrderDetailResponseDTO (@Builder pattern)
                        Service-->>Controller: return OrderDetailResponseDTO

                        Controller->>View: render("admin/order-detail", model: { order: detailDTO })
                        View-->>Browser: Trả về mã HTML hoàn chỉnh của trang chi tiết đơn hàng
                        Browser-->>Admin: Hiển thị giao diện thông tin người nhận, thanh toán và bảng sản phẩm

                        opt 4.1 Quản trị viên nhấp nút "Quay lại danh sách" hoặc breadcrumb (Alternate Flow)
                            Admin->>Browser: Nhấp "Quay lại danh sách"
                            Browser->>Controller: HTTP GET /admin/orders
                            Controller-->>Browser: Điều hướng về màn hình danh sách đơn hàng
                            Browser-->>Admin: Hiển thị bảng danh sách đơn hàng
                        end
                    end
                end
            end
        end
    end
```

---

## 3. Class Diagram

Mô hình hóa các thực thể JPA (`<<entity>>`), kiểu liệt kê trạng thái (`<<enumeration>>`) và các lớp truyền tải dữ liệu (`<<DTO>>`) tham gia trực tiếp vào Use Case xem chi tiết đơn hàng.

```mermaid
classDiagram
    class Order {
        <<entity>>
        -String id
        -String orderCode
        -String customerName
        -String customerPhone
        -String shippingAddress
        -String note
        -BigDecimal totalAmount
        -String paymentMethod
        -String paymentStatus
        -OrderStatus status
        -LocalDateTime createdAt
        -LocalDateTime updatedAt
        -List~OrderItem~ items
        +getId() String
        +getOrderCode() String
        +getCustomerName() String
        +getCustomerPhone() String
        +getShippingAddress() String
        +getNote() String
        +getTotalAmount() BigDecimal
        +getPaymentMethod() String
        +getPaymentStatus() String
        +getStatus() OrderStatus
        +getCreatedAt() LocalDateTime
        +getUpdatedAt() LocalDateTime
        +getItems() List~OrderItem~
    }

    class OrderItem {
        <<entity>>
        -String id
        -Order order
        -String productName
        -String productCode
        -BigDecimal unitPrice
        -Integer quantity
        -BigDecimal subtotal
        +getId() String
        +getOrder() Order
        +getProductName() String
        +getProductCode() String
        +getUnitPrice() BigDecimal
        +getQuantity() Integer
        +getSubtotal() BigDecimal
    }

    class OrderStatus {
        <<enumeration>>
        PENDING
        PROCESSING
        SHIPPED
        DELIVERED
        CANCELLED
        -String displayName
        +getDisplayName() String
    }

    class OrderDetailResponseDTO {
        <<DTO>>
        -String id
        -String orderCode
        -String customerName
        -String customerPhone
        -String shippingAddress
        -String note
        -LocalDateTime orderDate
        -LocalDateTime updatedAt
        -OrderStatus status
        -String statusDisplay
        -Integer totalItems
        -BigDecimal totalAmount
        -String paymentMethod
        -String paymentStatus
        -List~OrderItemResponseDTO~ items
        +getId() String
        +getOrderCode() String
        +getCustomerName() String
        +getCustomerPhone() String
        +getShippingAddress() String
        +getNote() String
        +getOrderDate() LocalDateTime
        +getUpdatedAt() LocalDateTime
        +getStatus() OrderStatus
        +getStatusDisplay() String
        +getTotalItems() Integer
        +getTotalAmount() BigDecimal
        +getPaymentMethod() String
        +getPaymentStatus() String
        +getItems() List~OrderItemResponseDTO~
    }

    class OrderItemResponseDTO {
        <<DTO>>
        -String id
        -String productCode
        -String productName
        -BigDecimal unitPrice
        -Integer quantity
        -BigDecimal subtotal
        +getId() String
        +getProductCode() String
        +getProductName() String
        +getUnitPrice() BigDecimal
        +getQuantity() Integer
        +getSubtotal() BigDecimal
    }

    Order "1" *-- "0..*" OrderItem : contains (items)
    Order --> OrderStatus : has status
    OrderDetailResponseDTO "1" *-- "0..*" OrderItemResponseDTO : contains (items)
    OrderDetailResponseDTO --> OrderStatus : has status
    Order ..> OrderDetailResponseDTO : mapped to in Service Layer via @Builder
    OrderItem ..> OrderItemResponseDTO : mapped to in Service Layer via @Builder
```
