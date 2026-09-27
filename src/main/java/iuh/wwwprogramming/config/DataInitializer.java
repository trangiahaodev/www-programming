package iuh.wwwprogramming.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.wwwprogramming.entity.*;
import iuh.wwwprogramming.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@ConditionalOnProperty(name = "app.seed-products", havingValue = "true", matchIfMissing = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    // GỘP V1 & V2: Khai báo toàn bộ các Repository của cả 2 nhánh
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // TỪ V2: Thêm các Repository cho tính năng mới
    private final NewsArticleRepository newsArticleRepository;
    private final VoucherRepository voucherRepository;
    private final OfficeRepository officeRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) {
        // GỘP: Chạy tuần tự tất cả các hàm mồi dữ liệu
        seedUsers();
        seedProductsAndCategories();
        seedOrders();
        seedNewsArticles();
        seedVouchers();
        seedOffices();
    }

    // =========================================================================
    // 1. TỪ V1: Nạp Tài khoản Người dùng (Admin & Customer)
    // =========================================================================
    private void seedUsers() {
        if (!userRepository.existsByEmail("khachhang@pinkycloud.com")) {
            User customer = User.builder()
                    .userCode("KH26000001")
                    .email("khachhang@pinkycloud.com")
                    .password(passwordEncoder.encode("123456"))
                    .fullName("Nguyễn Thu Hà")
                    .phone("0912345678")
                    .address("12 Nguyễn Văn Bảo, Phường 4, Quận Gò Vấp, TP. Hồ Chí Minh")
                    .role("ROLE_CUSTOMER")
                    .active(true)
                    .build();
            userRepository.save(customer);
            log.info("Đã khởi tạo tài khoản Customer mẫu.");
        }

        if (!userRepository.existsByEmail("admin@pinkycloud.com")) {
            User admin = User.builder()
                    .userCode("AD26000001")
                    .email("admin@pinkycloud.com")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Quản Trị Viên Pinky")
                    .phone("0988888888")
                    .address("Hệ Thống Pinky Cloud")
                    .role("ROLE_ADMIN")
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.info("Đã khởi tạo tài khoản Admin mẫu.");
        }
    }

    // =========================================================================
    // 2. GỘP V1 & V2: Nạp Danh Mục và Sản Phẩm (từ JSON)
    // =========================================================================
    private void seedProductsAndCategories() {
        try {
            if (productRepository.count() > 0) {
                log.info("Dữ liệu sản phẩm đã tồn tại ({}), bỏ qua bước seed.", productRepository.count());
                return;
            }

            ClassPathResource resource = new ClassPathResource("data/products.json");
            if (!resource.exists()) {
                log.warn("Không tìm thấy file data/products.json trên classpath.");
                return;
            }

            log.info("Bắt đầu nạp dữ liệu mẫu từ data/products.json...");
            try (InputStream is = resource.getInputStream();
                 java.io.Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonNode root = objectMapper.readTree(reader);
                JsonNode productsNode = root.has("products") ? root.get("products") : root;

                if (!productsNode.isArray()) {
                    log.warn("Cấu trúc products.json không hợp lệ, không phải mảng.");
                    return;
                }

                Map<String, Category> categoryMap = new HashMap<>();
                categoryRepository.findAll().forEach(c -> categoryMap.put(c.getName().trim().toLowerCase(), c));

                int count = 0;
                int catIndex = categoryMap.size() + 1;

                for (JsonNode item : productsNode) {
                    String catName = item.has("category") ? item.get("category").asText().trim() : "Khác";
                    String catKey = catName.toLowerCase();

                    Category category = categoryMap.get(catKey);
                    if (category == null) {
                        String code = "CAT" + String.format("%03d", catIndex++);
                        category = Category.builder()
                                .categoryCode(code)
                                .name(catName)
                                .description("Danh mục các sản phẩm " + catName)
                                .active(true)
                                .build();
                        category = categoryRepository.save(category);
                        categoryMap.put(catKey, category);
                    }

                    String rawId = item.has("id") ? item.get("id").asText().trim() : null;
                    String productCode = (rawId != null && !rawId.isEmpty()) ? rawId.toUpperCase() : ("SP" + String.format("%06d", count + 1));
                    String name = item.has("name") ? item.get("name").asText().trim() : "Sản phẩm " + (count + 1);
                    String brand = item.has("brand") ? item.get("brand").asText().trim() : "PinkyCloud";
                    double price = item.has("price") ? item.get("price").asDouble() : 100000.0;
                    int discount = item.has("discount") ? item.get("discount").asInt() : 0;
                    String image = item.has("image") ? item.get("image").asText().trim() : "/IMG/anh1.png";
                    String origin = item.has("origin") ? item.get("origin").asText().trim() : "Chính hãng";
                    String description = item.has("description") ? item.get("description").asText().trim() : "";
                    String ingredients = item.has("ingredients") ? item.get("ingredients").asText().trim() : "";
                    String usage = item.has("usage") ? item.get("usage").asText().trim() : "";
                    int stock = item.has("stock") ? item.get("stock").asInt() : 50;

                    Product product = Product.builder()
                            .productCode(productCode)
                            .name(name)
                            .brand(brand)
                            .price(BigDecimal.valueOf(price))
                            .discount(discount)
                            .image(image)
                            .origin(origin)
                            .description(description)
                            .ingredients(ingredients)
                            .usageInstructions(usage)
                            .stockQuantity(stock)
                            .active(true)
                            .isHot(discount >= 15 || count % 4 == 0)
                            .isNew(count % 3 == 0)
                            .rating(4.5 + (count % 6) * 0.1)
                            .reviewCount(10 + (count * 7) % 80)
                            .soldCount(25 + (count * 13) % 200)
                            .category(category)
                            .build();

                    productRepository.save(product);
                    count++;
                }

                log.info("Nạp dữ liệu mẫu thành công: {} sản phẩm và {} danh mục.", count, categoryMap.size());
            }
        } catch (Exception e) {
            log.error("Lỗi khi seed dữ liệu sản phẩm: ", e);
        }
    }

    // =========================================================================
    // 3. TỪ V1: Nạp Đơn hàng và Chi tiết đơn hàng mẫu (Giữ hardcode để đảm bảo mapping chuẩn)
    // =========================================================================
    private void seedOrders() {
        if (orderRepository.count() == 0) {
            log.info("Initializing sample order data...");
            List<Order> sampleOrders = new ArrayList<>();

            Order o1 = Order.builder()
                    .orderCode("ORD26000001")
                    .customerName("Nguyễn Thị Lan")
                    .customerPhone("0987654321")
                    .shippingAddress("12 Nguyễn Văn Bảo, P.4, Q.Gò Vấp, TP.HCM")
                    .totalAmount(new BigDecimal("850000.00"))
                    .status(OrderStatus.PENDING)
                    .paymentMethod("COD")
                    .paymentStatus("UNPAID")
                    .build();
            OrderItem i1 = OrderItem.builder()
                    .order(o1)
                    .productName("Kem dưỡng ẩm Laneige Water Bank")
                    .productCode("SP26000001")
                    .unitPrice(new BigDecimal("450000.00"))
                    .quantity(1)
                    .subtotal(new BigDecimal("450000.00"))
                    .build();
            OrderItem i2 = OrderItem.builder()
                    .order(o1)
                    .productName("Sữa rửa mặt Cetaphil Gentle Cleanser")
                    .productCode("SP26000002")
                    .unitPrice(new BigDecimal("200000.00"))
                    .quantity(2)
                    .subtotal(new BigDecimal("400000.00"))
                    .build();
            o1.setItems(new ArrayList<>(List.of(i1, i2)));
            sampleOrders.add(o1);

            Order o2 = Order.builder()
                    .orderCode("ORD26000002")
                    .customerName("Trần Văn An")
                    .customerPhone("0912345678")
                    .shippingAddress("45 Lê Lợi, Q.1, TP.HCM")
                    .totalAmount(new BigDecimal("320000.00"))
                    .status(OrderStatus.PROCESSING)
                    .paymentMethod("VNPAY")
                    .paymentStatus("PAID")
                    .build();
            OrderItem i3 = OrderItem.builder()
                    .order(o2)
                    .productName("Son dưỡng môi DHC Lip Cream")
                    .productCode("SP26000003")
                    .unitPrice(new BigDecimal("160000.00"))
                    .quantity(2)
                    .subtotal(new BigDecimal("320000.00"))
                    .build();
            o2.setItems(new ArrayList<>(List.of(i3)));
            sampleOrders.add(o2);

            orderRepository.saveAll(sampleOrders);
            log.info("Initialized {} sample orders successfully.", sampleOrders.size());
        }
    }

    // =========================================================================
    // 4. TỪ V2: Nạp Bài Viết Tin Tức & Cẩm Nang (từ data/news_articles.json)
    // =========================================================================
    private void seedNewsArticles() {
        try {
            ClassPathResource resource = new ClassPathResource("data/news_articles.json");
            if (!resource.exists()) {
                log.warn("Không tìm thấy file data/news_articles.json trên classpath.");
                return;
            }

            try (InputStream is = resource.getInputStream();
                 java.io.Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonNode root = objectMapper.readTree(reader);
                if (root.isArray()) {
                    int seededCount = 0;
                    for (JsonNode node : root) {
                        String slug = node.has("slug") ? node.get("slug").asText().trim() : null;
                        if (slug == null || slug.isEmpty()) continue;

                        if (!newsArticleRepository.existsBySlug(slug)) {
                            NewsArticle article = NewsArticle.builder()
                                    .slug(slug)
                                    .title(node.has("title") ? node.get("title").asText() : "")
                                    .excerpt(node.has("excerpt") ? node.get("excerpt").asText() : "")
                                    .content(node.has("content") ? node.get("content").asText() : "")
                                    .publishedDate(node.has("publishedDate") ? node.get("publishedDate").asText() : "01/01/2026")
                                    .category(node.has("category") ? node.get("category").asText() : "Tin tức")
                                    .author(node.has("author") ? node.get("author").asText() : "PinkyCloud")
                                    .authorRole(node.has("authorRole") ? node.get("authorRole").asText() : "Biên tập viên")
                                    .readTime(node.has("readTime") ? node.get("readTime").asText() : "5 phút đọc")
                                    .image(node.has("image") ? node.get("image").asText() : "/IMG/news01.png")
                                    .tags(node.has("tags") ? node.get("tags").asText() : "")
                                    .viewsCount(node.has("viewsCount") ? node.get("viewsCount").asLong() : 1000L)
                                    .isFeatured(node.has("isFeatured") && node.get("isFeatured").asBoolean())
                                    .active(node.has("active") ? node.get("active").asBoolean() : true)
                                    .linkedProductIds(node.has("linkedProductIds") ? node.get("linkedProductIds").asText() : "")
                                    .build();
                            newsArticleRepository.save(article);
                            seededCount++;
                        }
                    }
                    log.info("Initialized {} new news articles from JSON into SQL Server.", seededCount);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi nạp tin tức từ data/news_articles.json: ", e);
        }
    }

    // =========================================================================
    // 5. TỪ V2: Nạp Mã Giảm Giá Vouchers (từ data/vouchers.json)
    // =========================================================================
    private void seedVouchers() {
        try {
            ClassPathResource resource = new ClassPathResource("data/vouchers.json");
            if (!resource.exists()) {
                log.warn("Không tìm thấy file data/vouchers.json trên classpath.");
                return;
            }

            try (InputStream is = resource.getInputStream();
                 java.io.Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonNode root = objectMapper.readTree(reader);
                if (root.isArray()) {
                    int seededVoucherCount = 0;
                    for (JsonNode node : root) {
                        String code = node.has("code") ? node.get("code").asText().trim().toUpperCase() : null;
                        if (code == null) continue;

                        Optional<Voucher> existing = voucherRepository.findByCodeIgnoreCaseAndActiveTrue(code);
                        Voucher voucher = existing.orElse(new Voucher());
                        voucher.setCode(code);
                        voucher.setTitle(node.has("title") ? node.get("title").asText() : "");
                        voucher.setDetail(node.has("detail") ? node.get("detail").asText() : "");
                        voucher.setStatus(node.has("status") ? node.get("status").asText() : "active");
                        voucher.setAccent(node.has("accent") ? node.get("accent").asText() : "linear-gradient(135deg, #fff1f5 0%, #ffd6e3 100%)");
                        voucher.setDiscountAmount(node.has("discountAmount") && !node.get("discountAmount").isNull() ? new BigDecimal(node.get("discountAmount").asText()) : null);
                        voucher.setDiscountPercent(node.has("discountPercent") && !node.get("discountPercent").isNull() ? node.get("discountPercent").asInt() : null);
                        voucher.setMinOrderAmount(node.has("minOrderAmount") && !node.get("minOrderAmount").isNull() ? new BigDecimal(node.get("minOrderAmount").asText()) : BigDecimal.ZERO);
                        voucher.setTargetAudience(node.has("targetAudience") ? node.get("targetAudience").asText() : "ALL");
                        voucher.setBadgeText(node.has("badgeText") ? node.get("badgeText").asText() : "VOUCHER HOT");
                        voucher.setPriority(node.has("priority") ? node.get("priority").asInt() : 50);
                        voucher.setActive(node.has("active") ? node.get("active").asBoolean() : true);

                        voucherRepository.save(voucher);
                        if (existing.isEmpty()) seededVoucherCount++;
                    }
                    log.info("Initialized/Updated vouchers from JSON into SQL Server (new: {}).", seededVoucherCount);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi nạp voucher từ data/vouchers.json: ", e);
        }
    }

    // =========================================================================
    // 6. TỪ V2: Nạp Hệ thống Chi Nhánh / Showrooms (từ data/offices.json)
    // =========================================================================
    private void seedOffices() {
        try {
            ClassPathResource resource = new ClassPathResource("data/offices.json");
            if (!resource.exists()) {
                log.warn("Không tìm thấy file data/offices.json trên classpath.");
                return;
            }

            try (InputStream is = resource.getInputStream();
                 java.io.Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                JsonNode root = objectMapper.readTree(reader);
                if (root.isArray()) {
                    int seededOfficeCount = 0;
                    for (JsonNode node : root) {
                        String id = node.has("id") ? node.get("id").asText().trim() : null;
                        if (id == null) continue;

                        Optional<Office> existing = officeRepository.findById(id);
                        Office office = existing.orElse(new Office());
                        office.setId(id);
                        office.setTitle(node.has("title") ? node.get("title").asText() : "");
                        office.setAddress(node.has("address") ? node.get("address").asText() : "");
                        office.setDescription(node.has("description") ? node.get("description").asText() : "");
                        office.setCity(node.has("city") ? node.get("city").asText() : "");
                        office.setPhone(node.has("phone") ? node.get("phone").asText() : "");
                        office.setEmail(node.has("email") ? node.get("email").asText() : "");
                        office.setWorkingHours(node.has("workingHours") ? node.get("workingHours").asText() : "");
                        office.setSupportType(node.has("supportType") ? node.get("supportType").asText() : "");
                        office.setCoupon(node.has("coupon") ? node.get("coupon").asText() : "");
                        office.setImage(node.has("image") ? node.get("image").asText() : "");
                        office.setActive(node.has("active") ? node.get("active").asBoolean() : true);

                        List<String> perks = new ArrayList<>();
                        if (node.has("perks") && node.get("perks").isArray()) {
                            for (JsonNode p : node.get("perks")) {
                                perks.add(p.asText());
                            }
                        }
                        office.setPerks(perks);

                        officeRepository.save(office);
                        if (existing.isEmpty()) seededOfficeCount++;
                    }
                    log.info("Initialized/Updated {} showrooms/offices from JSON into SQL Server.", seededOfficeCount);
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi nạp dữ liệu chi nhánh từ data/offices.json: ", e);
        }
    }
}