# Technical Findings & Architecture Notes: admin-search-products

## Phân tích hiện trạng & Cơ hội nâng cấp
1. **Repository Hiện tại:**
   - Đang có `searchProducts(@Param("keyword") String keyword, @Param("categoryId") String categoryId, Pageable pageable)` trong `ProductRepository`.
   - Cần mở rộng hoặc bổ sung truy vấn tìm kiếm nâng cao (hỗ trợ khoảng giá `minPrice`/`maxPrice`, trạng thái tồn kho, trạng thái `active`).
2. **DTO & Service Hiện tại:**
   - Chưa có `ProductSearchCriteriaDTO` (hoặc `ProductFilterDTO`) để đóng gói các tham số lọc đa dạng khi người dùng submit form GET.
   - Có thể đóng gói `ProductSearchCriteriaDTO` với các trường: `keyword`, `categoryId`, `minPrice`, `maxPrice`, `stockStatus`, `active`, `sortBy`.
3. **Frontend [`product-list.html`](file:///E:/java-www/www-programming/src/main/resources/templates/admin/product-list.html):**
   - Đang có form tìm kiếm cơ bản gồm `keyword` + `categoryId`.
   - Có thể nâng cấp thêm khu vực "Bộ lọc nâng cao" (Advanced Filters) gập mở hoặc mở rộng inline, cho phép Admin lọc theo khoảng giá, trạng thái kho (Còn hàng / Sắp hết / Hết hàng), trạng thái hiển thị, sắp xếp theo giá / tồn kho / tên / ngày tạo, và giữ nguyên trạng thái các tham số khi phân trang.
