# Mermaid Diagrams: cart-checkout

Tài liệu thiết kế kiến trúc và sơ đồ tương tác chi tiết cho Module **Giỏ hàng & Thanh toán (Cart & Checkout)** (`uc002-shopping-cart` và `uc003-checkout`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) từ trình duyệt người dùng qua tầng bảo mật Spring Security, Spring MVC Controller, Service Layer, Session Storage (`HttpSession`), Spring Data JPA Repository đến cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Khách hàng: Guest / Customer)"]
    Security["Spring Security Filter Chain<br>(/cart/** permitAll, /checkout/** hasRole CUSTOMER)"]
    CartCtrl["CartController<br>(Spring MVC @Controller)"]
    CheckoutCtrl["CheckoutController<br>(Spring MVC @Controller)"]
    CartSvc["CartServiceImpl<br>(Quản lý giỏ hàng trên Session)"]
    OrderSvc["OrderServiceImpl<br>(@Transactional Place Order & Trừ kho)"]
    Session[("HttpSession<br>(Key: SESSION_CART)")]
    ProductRepo["ProductRepository<br>(Spring Data JPA)"]
    OrderRepo["OrderRepository<br>(Spring Data JPA - JOIN FETCH)"]
    UserRepo["UserRepository<br>(Spring Data JPA)"]
    Database[("Microsoft SQL Server<br>(products, orders, order_details, users)")]
    CartView["Thymeleaf: cart/cart-view.html"]
    CheckoutView["Thymeleaf: checkout/checkout-form.html"]
    SuccessView["Thymeleaf: checkout/order-success.html"]

    Client -->|"HTTP GET/POST /cart/**"| Security
    Client -->|"HTTP GET/POST /checkout/**"| Security
    Security -->|"Public Access"| CartCtrl
    Security -->|"Authenticated (ROLE_CUSTOMER)"| CheckoutCtrl
    Security -.->|"Unauthenticated -> Redirect /login"| Client

    CartCtrl --> CartSvc
    CartSvc <-->|"Đọc / Ghi CartDTO"| Session
    CartSvc -->|"Kiểm tra tồn kho"| ProductRepo

    CheckoutCtrl --> OrderSvc
    OrderSvc <-->|"Lấy & Xóa sạch giỏ"| CartSvc
    OrderSvc -->|"Trừ tồn kho"| ProductRepo
    OrderSvc -->|"Lấy user"| UserRepo
    OrderSvc -->|"Lưu Order + OrderDetail"| OrderRepo

    ProductRepo <--> Database
    OrderRepo <--> Database
    UserRepo <--> Database

    CartCtrl -->|"Model: cart"| CartView
    CheckoutCtrl -->|"Model: cart, checkoutRequest"| CheckoutView
    CheckoutCtrl -->|"Model: order"| SuccessView
```

---

## 2. Sequence Diagram (Academic Standard)

Mô tả chi tiết chu kỳ Request-Response cho nghiệp vụ **Đặt hàng & Thanh toán (Checkout)** bao gồm kiểm tra bảo mật, validate dữ liệu JSR-380, kiểm tra tồn kho, trừ tồn kho, quản lý transaction, và làm sạch giỏ hàng trong Session.

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Khách hàng (Customer)
    participant Browser as Trình duyệt (Client)
    participant Security as Spring Security
    participant Controller as CheckoutController
    participant OrderSvc as OrderServiceImpl
    participant CartSvc as CartServiceImpl
    participant Session as HttpSession
    participant ProductRepo as ProductRepository
    participant OrderRepo as OrderRepository
    participant DB as SQL Server
    participant View as Thymeleaf (order-success)

    Customer->>Browser: Nhấn nút "Xác nhận đặt hàng" trên Form Checkout
    Browser->>Security: HTTP POST /checkout (Payload: recipientName, phone, address, paymentMethod)
    
    alt Chưa đăng nhập (Exception Flow 2.1)
        Security-->>Browser: Redirect 302 /login (SavedRequest: /checkout)
        Browser-->>Customer: Chuyển hướng tới trang Đăng nhập
    else Đã đăng nhập ROLE_CUSTOMER (Main Flow)
        Security->>Controller: processCheckout(checkoutRequest, bindingResult, principal, session)
        activate Controller

        Controller->>CartSvc: getCart(session)
        CartSvc->>Session: getAttribute("SESSION_CART")
        Session-->>CartSvc: return CartDTO
        CartSvc-->>Controller: return CartDTO

        alt Giỏ hàng rỗng (Exception Flow 2.2)
            Controller-->>Browser: Redirect 302 /cart (Flash: "Giỏ hàng rỗng!")
            Browser-->>Customer: Hiển thị lại giỏ hàng kèm cảnh báo
        else Giỏ hàng có sản phẩm
            Controller->>Controller: Validate CheckoutRequestDTO (JSR-380 @Valid)
            
            alt Vi phạm quy tắc Validate Form (Exception Flow 5.1)
                Controller-->>Browser: Trả về view "checkout/checkout-form" (kèm lỗi BindingResult)
                Browser-->>Customer: Hiển thị lỗi đỏ dưới input field
            else Dữ liệu Form hợp lệ
                Controller->>OrderSvc: placeOrder(session, userEmail, checkoutRequest)
                activate OrderSvc

                loop Kiểm tra tồn kho từng sản phẩm trong giỏ
                    OrderSvc->>ProductRepo: findByIdAndActiveTrue(productId)
                    ProductRepo->>DB: SELECT p FROM Product p WHERE id = ?
                    DB-->>ProductRepo: return Product
                    ProductRepo-->>OrderSvc: return Product
                    
                    opt Số lượng mua > Tồn kho (Exception Flow 6.1)
                        OrderSvc-->>Controller: throw IllegalArgumentException("Tồn kho không đủ")
                        Controller-->>Browser: Trả về view "checkout/checkout-form" (Flash error)
                        Browser-->>Customer: Hiển thị thông báo sản phẩm hết hàng
                    end
                end

                OrderSvc->>OrderSvc: Trừ stockQuantity và save(Product)
                OrderSvc->>ProductRepo: save(product)
                ProductRepo->>DB: UPDATE products SET stock_quantity = ? WHERE id = ?
                
                OrderSvc->>OrderSvc: generateUniqueOrderCode() -> "DH26001234"
                OrderSvc->>OrderSvc: Build Entity Order + OrderDetail
                OrderSvc->>OrderRepo: save(order)
                OrderRepo->>DB: INSERT INTO orders ...; INSERT INTO order_details ...
                DB-->>OrderRepo: return Saved Order

                OrderSvc->>CartSvc: clearCart(session)
                CartSvc->>Session: remove/clear cart
                
                OrderSvc->>OrderSvc: Map Saved Order sang OrderResponseDTO
                OrderSvc-->>Controller: return OrderResponseDTO
                deactivate OrderSvc

                Controller-->>Browser: Redirect 302 /checkout/success (Flash: orderCode, order)
                Browser->>Controller: HTTP GET /checkout/success
                Controller->>View: Render "checkout/order-success"
                View-->>Browser: HTML kết quả đơn hàng
                Browser-->>Customer: Hiển thị mã đơn hàng, số tiền, cảm ơn quý khách!
            end
        end
        deactivate Controller
    end
```

---

## 3. Class Diagram

Biểu diễn cấu trúc phân lớp giữa các Thực thể CSDL (`<<entity>>`) và các Đối tượng Chuyển giao Dữ liệu (`<<DTO>>`).

```mermaid
classDiagram
    class Product {
        <<entity>>
        -String id
        -String productCode
        -String name
        -BigDecimal price
        -Integer stockQuantity
        -String imageUrl
        -String description
        -Boolean active
        -Category category
    }

    class User {
        <<entity>>
        -String id
        -String userCode
        -String email
        -String password
        -String fullName
        -String phone
        -String address
        -String role
        -Boolean active
    }

    class Order {
        <<entity>>
        -String id
        -String orderCode
        -User user
        -String recipientName
        -String recipientPhone
        -String shippingAddress
        -String note
        -BigDecimal totalAmount
        -String paymentMethod
        -String status
        -List~OrderDetail~ orderDetails
    }

    class OrderDetail {
        <<entity>>
        -String id
        -Order order
        -Product product
        -BigDecimal unitPrice
        -Integer quantity
        -BigDecimal subtotal
    }

    class CartDTO {
        <<DTO>>
        -Map~String, CartItemDTO~ items
        +addItem(item: CartItemDTO)
        +updateQuantity(productId: String, qty: int)
        +removeItem(productId: String)
        +clear()
        +getTotalQuantity(): int
        +getTotalAmount(): BigDecimal
        +isEmpty(): boolean
    }

    class CartItemDTO {
        <<DTO>>
        -String productId
        -String productCode
        -String productName
        -String imageUrl
        -BigDecimal unitPrice
        -Integer quantity
        -Integer stockQuantity
        -BigDecimal subtotal
    }

    class CheckoutRequestDTO {
        <<DTO>>
        -String recipientName
        -String recipientPhone
        -String shippingAddress
        -String paymentMethod
        -String note
    }

    class OrderResponseDTO {
        <<DTO>>
        -String id
        -String orderCode
        -String recipientName
        -String recipientPhone
        -String shippingAddress
        -BigDecimal totalAmount
        -String paymentMethod
        -String status
        -LocalDateTime createdAt
        -List~OrderDetailResponseDTO~ items
    }

    class OrderDetailResponseDTO {
        <<DTO>>
        -String id
        -String productId
        -String productCode
        -String productName
        -BigDecimal unitPrice
        -Integer quantity
        -BigDecimal subtotal
    }

    User "1" --> "*" Order : places
    Order "1" *-- "*" OrderDetail : contains
    Product "1" <-- "*" OrderDetail : references
    CartDTO "1" *-- "*" CartItemDTO : holds
    OrderResponseDTO "1" *-- "*" OrderDetailResponseDTO : presents
```
