# Mermaid Diagrams: admin-delete-product

Tài liệu thiết kế kiến trúc và luồng xử lý chi tiết cho Use Case **Quản trị viên xóa sản phẩm mỹ phẩm** (`uc002c-admin-delete-product`).

---

## 1. System Architecture

Biểu diễn luồng phân lớp Server-Side Rendering (SSR) trong quy trình xóa sản phẩm từ trình duyệt của Quản trị viên qua Hộp thoại xác nhận nguy hiểm (Danger Confirmation Modal), bộ lọc Spring Security, Spring MVC Controller, Service Layer (quản lý Transaction và kiểm tra sự tồn tại), Spring Data JPA Repository tới cơ sở dữ liệu Microsoft SQL Server.

```mermaid
flowchart TD
    Client["Browser (Admin Client)"]
    Modal["Danger Confirmation Modal<br>(Vanilla JS in product-list.html)"]
    Security["Spring Security Filter Chain<br>(Role: ROLE_ADMIN, CSRF Token Check)"]
    Controller["AdminProductController<br>(Spring MVC @Controller)"]
    ProdService["ProductService / ProductServiceImpl<br>(Service Layer @Transactional)"]
    ProdRepo["ProductRepository<br>(Spring Data JPA)"]
    Database[("Microsoft SQL Server<br>(Table: products)")]
    View["Thymeleaf Engine<br>(admin/product-list.html)"]

    Client -->|"1. Click Trash Icon (Open Modal)"| Modal
    Modal -->|"2. Click 'Xác nhận xóa' -> POST /admin/products/delete/{id} (CSRF)"| Security
    Security -->|"Authenticated (ROLE_ADMIN)"| Controller
    
    Controller -->|"deleteProduct(id)"| ProdService
    ProdService -->|"findById(id)"| ProdRepo
    ProdRepo -->|"SQL: SELECT * FROM products WHERE id = ?"| Database
    Database -->|"Product entity"| ProdRepo
    ProdRepo -->|"Product entity"| ProdService
    
    ProdService -->|"delete(Product entity)"| ProdRepo
    ProdRepo -->|"SQL: DELETE FROM products WHERE id = ?"| Database
    Database -->|"1 row deleted"| ProdRepo
    ProdRepo -->|"Deleted successfully"| ProdService
    ProdService -->|"Complete Transaction"| Controller
    
    Controller -->|"redirectAttributes.addFlashAttribute('successMessage')"| Controller
    Controller -->|"HTTP 302 Redirect to /admin/products (PRG Pattern)"| Client
    Client -->|"HTTP GET /admin/products"| Controller
    Controller -->|"Render view with Flash alert"| View
    View -->|"Render updated product table + Flash banner"| Client
```

---

## 2. Sequence Diagram (Comprehensive Academic Flow)

Mô tả chi tiết chu kỳ Request-Response theo chuẩn học thuật chuyên sâu, bao gồm Giai đoạn mở Modal xác nhận trên Client, Giai đoạn hủy bỏ (Alternate Flow), Giai đoạn gửi yêu cầu xóa qua phương thức `POST` có CSRF token, kiểm tra thực thể tại Service và các kịch bản ngoại lệ (Exception Flows).

```mermaid
sequenceDiagram
    autonumber
    actor Admin as Quản trị viên (Admin)
    participant Browser as Trình duyệt (Client)
    participant Modal as Modal Xác nhận (Neo-Brutalism)
    participant Security as Spring Security
    participant Controller as AdminProductController
    participant ProdService as ProductServiceImpl
    participant ProdRepo as ProductRepository
    participant DB as SQL Server (products)
    participant View as Thymeleaf (product-list.html)

    %% GIAI ĐOẠN 1: MỞ MODAL XÁC NHẬN
    Note over Admin, Modal: Giai đoạn 1: Mở Hộp thoại xác nhận nguy hiểm
    Admin->>Browser: Nhấn biểu tượng thùng rác màu đỏ (btn-icon-danger) tại dòng sản phẩm
    Browser->>Modal: Kích hoạt openDeleteModal(id, productCode, productName)
    Modal-->>Admin: Hiển thị Hộp thoại xác nhận nguy hiểm (Mã SP, Tên SP và Cảnh báo không thể hoàn tác)

    %% GIAI ĐOẠN 2: PHẢN HỒI HÀNH ĐỘNG
    Note over Admin, DB: Giai đoạn 2: Quản trị viên phản hồi & Xử lý Backend
    alt 3.1 Quản trị viên nhấn nút "Hủy bỏ" hoặc nhấp ngoài Backdrop (Alternate Flow)
        Admin->>Modal: Nhấn "Hủy bỏ" / Nhấp Backdrop
        Modal->>Modal: Đóng hộp thoại (closeDeleteModal)
        Modal-->>Admin: Giữ nguyên trạng thái bảng danh sách sản phẩm (Không gửi request)
    else 3. Quản trị viên nhấn "Xác nhận xóa" (Main Flow)
        Admin->>Modal: Nhấn nút "Xác nhận xóa" (btn-danger)
        Modal->>Security: HTTP POST /admin/products/delete/{id} (kèm _csrf token)
        Security->>Controller: Forward request tới deleteProduct(id, redirectAttributes)
        activate Controller
        
        Controller->>ProdService: deleteProduct(id)
        activate ProdService
        
        ProdService->>ProdRepo: findById(id)
        activate ProdRepo
        ProdRepo->>DB: SELECT * FROM products WHERE id = ?
        
        alt 5.1.1 Sản phẩm không tồn tại trong CSDL (Exception Flow)
            DB-->>ProdRepo: Optional.empty()
            ProdRepo-->>ProdService: Optional.empty()
            deactivate ProdRepo
            ProdService-->>Controller: throw IllegalArgumentException("Không tìm thấy sản phẩm mỹ phẩm cần xóa!")
            deactivate ProdService
            Controller->>Controller: redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy sản phẩm mỹ phẩm cần xóa!")
            Controller-->>Browser: HTTP 302 Redirect to /admin/products (PRG Pattern)
            Browser->>Controller: HTTP GET /admin/products
            Controller->>View: render("admin/product-list", model với Flash errorMessage)
            View-->>Browser: Render bảng danh sách kèm Flash Alert đỏ báo lỗi
            Browser-->>Admin: Hiển thị thông báo không tìm thấy sản phẩm
        else 5. Tìm thấy sản phẩm hợp lệ
            DB-->>ProdRepo: Product entity
            activate ProdRepo
            ProdRepo-->>ProdService: Product entity
            deactivate ProdRepo
            activate ProdService
            
            ProdService->>ProdRepo: delete(Product entity)
            activate ProdRepo
            ProdRepo->>DB: DELETE FROM products WHERE id = ?
            DB-->>ProdRepo: 1 row affected
            ProdRepo-->>ProdService: Deletion complete
            deactivate ProdRepo
            
            ProdService-->>Controller: Transaction committed successfully
            deactivate ProdService
            
            Controller->>Controller: redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm mỹ phẩm thành công!")
            Controller-->>Browser: HTTP 302 Redirect to /admin/products (PRG Pattern)
            deactivate Controller
            
            Browser->>Controller: HTTP GET /admin/products
            activate Controller
            Controller->>View: render("admin/product-list", model với Flash successMessage)
            deactivate Controller
            activate View
            View-->>Browser: Render bảng danh sách đã cập nhật kèm Flash Alert xanh lá
            deactivate View
            Browser-->>Admin: Hiển thị thông báo "Xóa sản phẩm mỹ phẩm thành công!"
        end
    end
```

---

## 3. Class Diagram

Mô hình cấu trúc lớp thể hiện mối quan hệ giữa các thành phần liên quan trong use case `admin-delete-product`.

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
    }

    class ProductRepository {
        <<interface>>
        +findById(String id) Optional~Product~
        +delete(Product product) void
    }

    class ProductService {
        <<interface>>
        +deleteProduct(String id) void
    }

    class ProductServiceImpl {
        -ProductRepository productRepository
        -CategoryRepository categoryRepository
        +deleteProduct(String id) void
    }

    class AdminProductController {
        -ProductService productService
        -CategoryService categoryService
        +deleteProduct(String id, RedirectAttributes ra) String
    }

    ProductServiceImpl ..|> ProductService : implements
    ProductServiceImpl --> ProductRepository : uses
    AdminProductController --> ProductService : delegates to
    ProductRepository ..> Product : manages entity
```
