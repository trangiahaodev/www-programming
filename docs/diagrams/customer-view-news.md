# Mermaid Diagrams: customer-view-news

Tài liệu thiết kế kiến trúc và sơ đồ nghiệp vụ cho Use Case **Khách hàng khám phá Tạp chí làm đẹp & Tin tức xu hướng** (`uc005d-customer-view-news`).

---

## 1. System Architecture

Biểu diễn luồng Server-Side Rendering (SSR) và AJAX API từ trình duyệt khách hàng qua Spring Security Filter, `NewsController`, tầng `NewsService`, kết hợp dữ liệu sản phẩm đính kèm từ `ProductRepository` đến tầng giao diện Thymeleaf.

```mermaid
flowchart TD
    Client["Trình duyệt Khách hàng (Client Browser)"]
    Security["Spring Security Filter Chain<br>(PermitAll: /tin-tuc/**)"]
    Controller["NewsController<br>(@Controller: /tin-tuc, /news)"]

    subgraph ServiceLayer ["Service Layer (@Service)"]
        NewsService["NewsServiceImpl<br>(getAllNews, getNewsBySlug, getRelatedNews)"]
    end

    subgraph Repositories ["Data Access Layer (JPA)"]
        ProdRepo["ProductRepository<br>(findAllById: Linked Products)"]
    end

    Database[("Microsoft SQL Server<br>(products, categories)")]
    ViewList["Thymeleaf View<br>(customer/news-list.html)"]
    ViewDetail["Thymeleaf View<br>(customer/news-detail.html)"]

    Client -->|"HTTP GET /tin-tuc (Lọc chuyên mục, từ khóa)"| Security
    Client -->|"HTTP GET /tin-tuc/{slug} (Đọc bài chi tiết)"| Security
    Client -->|"HTTP POST /tin-tuc/subscribe (Đăng ký nhận tin)"| Security

    Security --> Controller

    Controller -->|"getAllNews(category, keyword)"| NewsService
    Controller -->|"getNewsBySlug(slug)"| NewsService
    Controller -->|"getRelatedNews(articleId, category, 3)"| NewsService

    NewsService -->|"productRepository.findAllById(productIds)"| ProdRepo
    ProdRepo -->|"SELECT p FROM Product p WHERE p.id IN (...)"| Database
    Database -->|"Product Records"| ProdRepo
    ProdRepo -->|"List&lt;ProductCardDTO&gt;"| NewsService

    NewsService -->|"NewsDTO (kèm linkedProducts & metadata)"| Controller

    Controller -->|"Model attributes (newsList, featuredArticle, categories)"| ViewList
    Controller -->|"Model attributes (news, relatedNews, allNews)"| ViewDetail

    ViewList -->|"Render HTML danh sách bài viết & Teaser cards"| Client
    ViewDetail -->|"Render HTML bài viết Editorial & Series Tracker"| Client
```

---

## 2. Sequence Diagram: Xem chi tiết bài viết & Sản phẩm liên kết

Mô tả chu kỳ tương tác khi người dùng chọn đọc một bài viết, hệ thống truy xuất nội dung, ánh xạ sản phẩm chính hãng liên quan để hỗ trợ mua hàng trực tiếp từ nội dung bài viết.

```mermaid
sequenceDiagram
    autonumber
    actor Customer as Khách hàng
    participant Browser as Trình duyệt (Client)
    participant Controller as NewsController
    participant Service as NewsServiceImpl
    participant ProdRepo as ProductRepository
    participant DB as SQL Server
    participant View as Thymeleaf (news-detail.html)

    Customer->>Browser: Nhấp vào bài viết từ danh sách hoặc Banner Hero (/tin-tuc/{slug})
    Browser->>Controller: HTTP GET /tin-tuc/{slug}
    activate Controller

    Controller->>Service: getNewsBySlug(slug)
    activate Service

    Service->>Service: Tìm bài viết theo slug
    alt Không tìm thấy bài viết
        Service-->>Controller: return null
        Controller-->>Browser: Redirect 302 -> /tin-tuc (Flash: Không tìm thấy bài viết)
    else Tìm thấy bài viết
        Service->>ProdRepo: findAllById(linkedProductIds)
        activate ProdRepo
        ProdRepo->>DB: Truy vấn sản phẩm được liên kết
        DB-->>ProdRepo: Kết quả Product entities
        ProdRepo-->>Service: List<Product>
        deactivate ProdRepo

        Service->>Service: Chuyển đổi thành ProductCardDTO (giá, giảm giá, ảnh)
        Service-->>Controller: NewsDTO (đầy đủ nội dung, tác giả & sản phẩm liên quan)
    end
    deactivate Service

    Controller->>Service: getRelatedNews(id, category, 3)
    Service-->>Controller: List<NewsDTO> (3 bài viết tương tự)

    Controller->>Service: getAllNews(null, null)
    Service-->>Controller: List<NewsDTO> (Tuyển tập Series Tracker)

    Controller->>View: Trả về Model (news, relatedNews, allNews)
    activate View
    View-->>Browser: Render trang chi tiết (Hero, Key Takeaways, Series Tracker, Shop In Article)
    deactivate View
    deactivate Controller

    Browser-->>Customer: Hiển thị giao diện bài viết sang trọng chuẩn Luxury Editorial
```

---

## 3. Activity Diagram: Luồng nghiệp vụ Tạp chí làm đẹp

```mermaid
flowchart TD
    Start(["Khách hàng vào Chuyên mục Tin tức"]) --> BrowseList["Xem danh sách bài viết & Hero Featured"]
    
    BrowseList --> FilterDecision{"Khách hàng muốn làm gì?"}
    
    FilterDecision -->|"Lọc theo chủ đề"| SelectCat["Chọn tab chuyên mục (Chăm sóc da, Trang điểm, v.v.)"]
    SelectCat --> UpdateList["Cập nhật danh sách bài viết tương ứng"]
    UpdateList --> BrowseList

    FilterDecision -->|"Tìm kiếm"| SearchKey["Nhập từ khóa tìm kiếm bài viết"]
    SearchKey --> UpdateList

    FilterDecision -->|"Đọc bài viết"| ClickArticle["Nhấp chọn bài viết mong muốn"]
    ClickArticle --> LoadDetail["Tải trang chi tiết bài viết (/tin-tuc/{slug})"]
    
    LoadDetail --> ReadFlow["Đọc nội dung bài viết & Điểm nhấn chính"]
    
    ReadFlow --> ActionDecision{"Tương tác tiếp theo?"}
    
    ActionDecision -->|"Chuyển bài nhanh"| SwitchSeries["Bấm chọn bài viết khác trong Series Tracker"]
    SwitchSeries --> LoadDetail

    ActionDecision -->|"Mua mỹ phẩm trong bài"| ClickProduct["Xem danh mục 'Sản phẩm gợi ý trong bài'"]
    ClickProduct --> NavigateProduct["Điều hướng sang trang Chi tiết sản phẩm (/san-pham/{id})"]
    NavigateProduct --> EndStorefront(["Mua sắm sản phẩm"])

    ActionDecision -->|"Nhận tin mới"| InputEmail["Nhập Email tại form Nhận bản tin làm đẹp"]
    InputEmail --> SubmitSub["Gửi POST /tin-tuc/subscribe"]
    SubmitSub --> ShowToast["Hiển thị Floating Toast: Đăng ký thành công!"]
    ShowToast --> EndFlow(["Kết thúc tương tác"])
```
