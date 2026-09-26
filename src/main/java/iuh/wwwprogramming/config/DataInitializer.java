package iuh.wwwprogramming.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.wwwprogramming.entity.Category;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderItem;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.CategoryRepository;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    // 1. Khai báo gộp tất cả các Repository của cả 2 nhánh
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) {
        // Gọi tuần tự các hàm nạp dữ liệu mồi
        seedProductsAndCategories();
        seedOrders();
    }

    // =========================================================================
    // LOGIC CỦA NHÁNH CORE: Nạp Danh Mục và Sản Phẩm (từ JSON)
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
                 java.io.Reader reader = new java.io.InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8)) {
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
                            .price(BigDecimal.valueOf(price)) // Đã giữ bản fix an toàn BigDecimal
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
    // LOGIC CỦA NHÁNH ORDER: Nạp Đơn hàng và Chi tiết đơn hàng mẫu
    // =========================================================================
    private void seedOrders() {
        if (orderRepository.count() == 0) {
            log.info("Initializing sample order data...");
            List<Order> sampleOrders = new ArrayList<>();

            // Order 1: PENDING
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

            // Order 2: PROCESSING
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

            // Order 3: SHIPPED
            Order o3 = Order.builder()
                    .orderCode("ORD26000003")
                    .customerName("Lê Hoàng Mai")
                    .customerPhone("0933221100")
                    .shippingAddress("78 Trần Phú, Hà Đông, Hà Nội")
                    .totalAmount(new BigDecimal("1450000.00"))
                    .status(OrderStatus.SHIPPED)
                    .paymentMethod("BANKING")
                    .paymentStatus("PAID")
                    .build();
            OrderItem i4 = OrderItem.builder()
                    .order(o3)
                    .productName("Serum phục hồi da La Roche-Posay B5")
                    .productCode("SP26000004")
                    .unitPrice(new BigDecimal("725000.00"))
                    .quantity(2)
                    .subtotal(new BigDecimal("1450000.00"))
                    .build();
            o3.setItems(new ArrayList<>(List.of(i4)));
            sampleOrders.add(o3);

            // Order 4: DELIVERED
            Order o4 = Order.builder()
                    .orderCode("ORD26000004")
                    .customerName("Phạm Minh Tuấn")
                    .customerPhone("0908776655")
                    .shippingAddress("102 Hai Bà Trưng, Đà Nẵng")
                    .totalAmount(new BigDecimal("590000.00"))
                    .status(OrderStatus.DELIVERED)
                    .paymentMethod("COD")
                    .paymentStatus("PAID")
                    .build();
            OrderItem i5 = OrderItem.builder()
                    .order(o4)
                    .productName("Kem chống nắng Anessa Perfect UV")
                    .productCode("SP26000005")
                    .unitPrice(new BigDecimal("590000.00"))
                    .quantity(1)
                    .subtotal(new BigDecimal("590000.00"))
                    .build();
            o4.setItems(new ArrayList<>(List.of(i5)));
            sampleOrders.add(o4);

            // Order 5: CANCELLED
            Order o5 = Order.builder()
                    .orderCode("ORD26000005")
                    .customerName("Đỗ Thu Hà")
                    .customerPhone("0971122334")
                    .shippingAddress("15 Quang Trung, Cần Thơ")
                    .totalAmount(new BigDecimal("210000.00"))
                    .status(OrderStatus.CANCELLED)
                    .paymentMethod("COD")
                    .paymentStatus("UNPAID")
                    .build();
            OrderItem i6 = OrderItem.builder()
                    .order(o5)
                    .productName("Tẩy trang Bioderma Sensibio H2O 250ml")
                    .productCode("SP26000006")
                    .unitPrice(new BigDecimal("210000.00"))
                    .quantity(1)
                    .subtotal(new BigDecimal("210000.00"))
                    .build();
            o5.setItems(new ArrayList<>(List.of(i6)));
            sampleOrders.add(o5);

            orderRepository.saveAll(sampleOrders);
            log.info("Initialized {} sample orders successfully.", sampleOrders.size());
        }
    }
}