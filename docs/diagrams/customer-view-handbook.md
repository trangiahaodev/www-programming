# Mermaid Diagrams: customer-view-handbook

Tài liệu thiết kế kiến trúc và sơ đồ nghiệp vụ cho Use Case **Khách hàng tra cứu Cẩm nang Skincare 7 Bước Chuẩn Hàn & Test Chẩn đoán loại da** (`uc006d-customer-view-handbook`).

---

## 1. System Architecture

Biểu diễn luồng tương tác giữa Cẩm nang làm đẹp (Handbook Module), hệ thống gợi ý sản phẩm phù hợp cho từng bước Skincare và bài kiểm tra chẩn đoán loại da cá nhân hóa.

```mermaid
flowchart TD
    Client["Trình duyệt Khách hàng (Client Browser)"]
    Security["Spring Security Filter Chain<br>(PermitAll: /cam-nang)"]
    Controller["NewsController<br>(@Controller: /cam-nang)"]

    subgraph ServiceLayer ["Service Layer (@Service)"]
        NewsService["NewsServiceImpl<br>(getHandbookData, getLinkedProductsForStep)"]
    end

    subgraph Repositories ["Data Access Layer (JPA)"]
        ProdRepo["ProductRepository<br>(findTopProductsByCategory)"]
    end

    Database[("Microsoft SQL Server<br>(products, categories)")]
    ViewHandbook["Thymeleaf View<br>(customer/news-list.html - handbook mode)"]

    Client -->|"HTTP GET /cam-nang"| Security
    Security --> Controller

    Controller -->|"getHandbookData()<br>(7 Chương Skincare, Quiz Chẩn đoán da, Bảng thành phần)"| NewsService
    NewsService -->|"findAllById(stepProductIds)"| ProdRepo
    ProdRepo -->|"JPQL SELECT"| Database
    Database -->|"Product Records"| ProdRepo
    ProdRepo -->|"List&lt;ProductCardDTO&gt;"| NewsService

    NewsService -->|"Handbook Content & Linked Step Products"| Controller
    Controller -->|"Model attributes (isHandbook=true, handbookChapters, steps)"| ViewHandbook

    ViewHandbook -->|"Render HTML Cẩm nang Skincare & Interactive Quiz"| Client
```

---

## 2. Sequence Diagram: Tra cứu Cẩm nang & Chẩn đoán loại da

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Khách hàng
    participant Browser as Trình duyệt (Client)
    participant Controller as NewsController
    participant Service as NewsServiceImpl
    participant ProdRepo as ProductRepository
    participant DB as SQL Server
    participant View as Thymeleaf (news-list.html)

    Customer->>Browser: Truy cập /cam-nang từ Menu Navigation
    Browser->>Controller: HTTP GET /cam-nang
    activate Controller

    Controller->>Service: Lấy cấu trúc Cẩm nang (7 Bước Skincare)
    activate Service
    Service->>ProdRepo: Lấy danh sách mỹ phẩm chuyên dụng theo bước
    activate ProdRepo
    ProdRepo->>DB: Truy vấn sản phẩm theo Category & Brand uy tín
    DB-->>ProdRepo: Trả về Product entities
    ProdRepo-->>Service: List<Product>
    deactivate ProdRepo
    Service-->>Controller: Dữ liệu Cẩm nang kèm sản phẩm liên kết
    deactivate Service

    Controller->>View: Trả về Model (isHandbook=true, chapters, steps)
    activate View
    View-->>Browser: Render giao diện Cẩm nang Skincare
    deactivate View
    deactivate Controller

    Browser-->>Customer: Hiển thị 7 bước dưỡng da chuẩn Hàn & Quiz chẩn đoán da
    
    Customer->>Browser: Thực hiện bài trắc nghiệm Chẩn đoán da (Quiz)
    Browser->>Browser: Tính toán điểm số & Phân loại da (Da Dầu / Da Khô / Da Nhạy Cảm)
    Browser-->>Customer: Hiển thị kết quả loại da & Gợi ý routine sản phẩm phù hợp
```

---

## 3. Activity Diagram: Quy trình tra cứu Cẩm nang 7 Bước Skincare

```mermaid
flowchart TD
    Start(["Khách hàng truy cập Cẩm nang Skincare"]) --> MainMenu["Xem mục lục 7 Bước Dưỡng da chuẩn Hàn"]

    MainMenu --> UserAction{"Khách hàng chọn thao tác"}

    UserAction -->|"Làm trắc nghiệm da"| TakeQuiz["Bắt đầu bài kiểm tra chẩn đoán loại da"]
    TakeQuiz --> AnswerQuestions["Trả lời 4 câu hỏi về tình trạng đổ dầu, lỗ chân lông, độ căng rát"]
    AnswerQuestions --> CalcResult["Hệ thống phân tích điểm và trả về loại da"]
    CalcResult --> RecommendRoutine["Đưa ra gợi ý chu trình dưỡng da cá nhân hóa"]
    RecommendRoutine --> MainMenu

    UserAction -->|"Xem chi tiết bước dưỡng"| SelectStep["Chọn bước cụ thể (VD: Bước 1 Tẩy trang, Bước 4 Serum, Bước 7 Kem chống nắng)"]
    SelectStep --> ReadStepDetail["Đọc nguyên lý hoạt động, thành phần nên dùng & mẹo chuyên gia"]
    
    ReadStepDetail --> ViewStepProducts["Xem các sản phẩm tiêu biểu được gợi ý cho bước đó"]
    ViewStepProducts --> ProductDecision{"Khách hàng muốn mua thử?"}
    
    ProductDecision -->|"Có"| ClickProduct["Nhấp vào sản phẩm gợi ý"]
    ClickProduct --> GoProductDetail["Chuyển hướng đến /san-pham/{id}"]
    GoProductDetail --> EndBuy(["Tiến hành đặt mua sản phẩm"])

    ProductDecision -->|"Không"| ContinueReading["Tiếp tục tra cứu các bước skincare kế tiếp"]
    ContinueReading --> MainMenu
```
