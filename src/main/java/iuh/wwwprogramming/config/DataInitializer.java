package iuh.wwwprogramming.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import iuh.wwwprogramming.entity.*;
import iuh.wwwprogramming.repository.*;
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

    // Khai báo các Repository
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final NewsArticleRepository newsArticleRepository;
    private final VoucherRepository voucherRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @Transactional
    public void run(String... args) {
        // Gọi tuần tự các hàm nạp dữ liệu mồi
        seedProductsAndCategories();
        seedOrders();
        seedNewsArticles();
        seedVouchers();
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

    // =========================================================================
    // Nạp Bài Viết Tin Tức & Cẩm Nang vào Database Thật
    // =========================================================================
    private void seedNewsArticles() {
        if (newsArticleRepository.count() > 0) {
            log.info("Dữ liệu bài viết tin tức đã tồn tại ({}), bỏ qua seed.", newsArticleRepository.count());
            return;
        }

        List<NewsArticle> articles = new ArrayList<>();

        articles.add(NewsArticle.builder()
                .slug("xu-huong-lam-dep-2026-thien-nhien-va-cham-soc-da")
                .title("Xu hướng làm đẹp 2026: Thiên nhiên và chăm sóc da")
                .excerpt("Sản phẩm thiên nhiên đang trở thành xu hướng hàng đầu trong chăm sóc da mặt và cơ thể với độ an toàn và lành tính cao.")
                .content("Năm 2026 đánh dấu sự lên ngôi mạnh mẽ của xu hướng làm đẹp bền vững, trong đó các sản phẩm có nguồn gốc thiên nhiên và quy trình chăm sóc da tối giản đang trở thành lựa chọn ưu tiên của nhiều người tiêu dùng. Không chỉ dừng lại ở yếu tố hiệu quả, người dùng hiện nay còn quan tâm đến độ an toàn, độ lành tính và sự thân thiện với môi trường trong từng sản phẩm.\n\nMột trong những thay đổi nổi bật là việc người tiêu dùng có xu hướng lựa chọn các sản phẩm chứa chiết xuất thực vật như tràm trà, rau má, lô hội, hoa cúc và các loại dầu thiên nhiên. Những thành phần này không chỉ giúp làm dịu da mà còn hỗ trợ phục hồi và nuôi dưỡng làn da một cách bền vững hơn.\n\nBên cạnh đó, quy trình skincare nhiều bước đang dần được thay thế bằng phương pháp chăm sóc da thông minh và tinh gọn (Skinimalism). Thay vì sử dụng quá nhiều sản phẩm trong một lần dưỡng, xu hướng mới tập trung vào việc chọn đúng sản phẩm phù hợp với nhu cầu thực tế của làn da.\n\nNgoài ra, bao bì tối giản, có thể tái chế và các thương hiệu hướng đến phát triển xanh cũng đang chiếm được nhiều cảm tình hơn từ người tiêu dùng trẻ. Đây là tín hiệu cho thấy làm đẹp không còn chỉ là câu chuyện bên ngoài, mà còn gắn liền với phong cách sống và nhận thức bền vững.\n\nTrong thời gian tới, xu hướng làm đẹp thiên nhiên được dự đoán sẽ tiếp tục phát triển mạnh, đặc biệt ở nhóm khách hàng yêu thích lối sống lành mạnh và ưu tiên sự cân bằng sinh học cho làn da.")
                .publishedDate("12/04/2026")
                .category("Xu hướng làm đẹp")
                .author("PinkyCloud Editorial")
                .authorRole("Ban Biên Tập Chuyên Môn")
                .readTime("6 phút đọc")
                .image("/IMG/news01.png")
                .tags("Skincare 2026,Clean Beauty,Rau Má,Skinimalism")
                .viewsCount(2480L)
                .isFeatured(true)
                .active(true)
                .linkedProductIds("pc-016,pc-009")
                .build());

        articles.add(NewsArticle.builder()
                .slug("bi-quyet-chon-my-pham-phu-hop-cho-da-nhay-cam")
                .title("Bí quyết chọn mỹ phẩm phù hợp cho da nhạy cảm")
                .excerpt("Hướng dẫn cách đọc thành phần, tránh các hoạt chất dễ gây kích ứng và lựa chọn sản phẩm an toàn cho làn da nhạy cảm.")
                .content("Da nhạy cảm là một trong những loại da cần được chăm sóc cẩn thận nhất vì rất dễ phản ứng với mỹ phẩm, thời tiết hoặc môi trường xung quanh. Việc lựa chọn mỹ phẩm phù hợp không chỉ giúp bảo vệ da mà còn hạn chế tình trạng kích ứng, mẩn đỏ và khô rát kéo dài.\n\nKhi chọn mỹ phẩm cho da nhạy cảm, điều quan trọng đầu tiên là phải đọc kỹ bảng thành phần. Người dùng nên ưu tiên các sản phẩm không chứa cồn khô, hương liệu tổng hợp, paraben mạnh hoặc các chất tẩy rửa có độ làm sạch quá cao (Sulfates). Những thành phần này có thể khiến hàng rào bảo vệ da bị tổn thương và làm da trở nên yếu hơn.\n\nNgoài ra, nên ưu tiên các sản phẩm có thành phần phục hồi như Panthenol (B5), Ceramide, Allantoin, chiết xuất rau má (Centella Asiatica) hoặc Hyaluronic Acid. Đây là những hoạt chất lành tính, hỗ trợ cấp ẩm và củng cố hàng rào lipid bảo vệ da hiệu quả.\n\nMột lưu ý quan trọng khác là luôn thử sản phẩm ở vùng da nhỏ (Patch test tại quai hàm) trong 24 - 48 giờ trước khi dùng cho toàn bộ khuôn mặt. Đây là bước cần thiết để kiểm tra phản ứng của da, đặc biệt với những làn da đang treatment hoặc mới peel.\n\nBên cạnh việc chọn sản phẩm, người có làn da nhạy cảm cũng nên xây dựng quy trình chăm sóc da tối giản, tránh kết hợp quá nhiều hoạt chất mạnh cùng một thời điểm. Sự kiên trì và lựa chọn đúng sản phẩm sẽ giúp làn da khỏe hơn rõ rệt theo thời gian.")
                .publishedDate("05/04/2026")
                .category("Chăm sóc da")
                .author("Dr. Hoàng Lan")
                .authorRole("Cố Vấn Da Liễu PinkyCloud")
                .readTime("5 phút đọc")
                .image("/IMG/news02.png")
                .tags("Da Nhạy Cảm,Dịu Lành,Chống Nắng,Bảo Vệ Hàng Rào Da")
                .viewsCount(3120L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-001,pc-012")
                .build());

        articles.add(NewsArticle.builder()
                .slug("cach-su-dung-serum-dung-chuan-de-da-sang-khoe")
                .title("Cách sử dụng serum đúng chuẩn để da sáng khỏe")
                .excerpt("Tìm hiểu quy trình dưỡng da hiệu quả với serum, toner và kem chống nắng để tối ưu hóa khả năng thẩm thấu dưỡng chất.")
                .content("Serum là một trong những sản phẩm chăm sóc da được nhiều người yêu thích nhờ khả năng chứa nồng độ hoạt chất cao, thẩm thấu nhanh và tập trung giải quyết từng vấn đề cụ thể của làn da. Tuy nhiên, để serum phát huy hiệu quả tối đa, cách sử dụng đúng là điều tối quan trọng.\n\nTrước tiên, làn da cần được làm sạch hoàn toàn bằng sữa rửa mặt dịu nhẹ. Sau đó, hãy dùng toner để cân bằng độ pH tự nhiên và tạo độ ẩm đệm cho serum hấp thụ sâu hơn. Khi thoa serum, chỉ cần lấy một lượng vừa đủ (thường từ 2 đến 3 giọt), rồi nhẹ nhàng vỗ đều lên da thay vì chà xát hay miết mạnh.\n\nNguyên tắc tiếp theo là chọn serum phù hợp với nhu cầu da: Nếu da khô mất nước, hãy ưu tiên serum cấp ẩm chứa Hyaluronic Acid đa phân tử hoặc Glycerin. Nếu da xỉn màu thâm mụn, có thể chọn Vitamin C hoặc Niacinamide. Nếu da có dấu hiệu lão hóa sớm, các serum chứa Peptide hoặc Retinol vi nang sẽ là lựa chọn phù hợp hơn.\n\nThời điểm sử dụng serum cũng ảnh hưởng lớn đến kết quả: Một số serum cấp ẩm có thể dùng cả sáng và tối, nhưng các hoạt chất mạnh như Retinol hay AHA/BHA thường chỉ nên dùng vào ban đêm. Vào ban ngày, sau bước serum và dưỡng ẩm mỏng, nhất định phải sử dụng kem chống nắng phổ rộng để bảo vệ da khỏi tác hại của tia UV.\n\nViệc sử dụng serum đúng cách không chỉ giúp da hấp thụ dưỡng chất tốt hơn mà còn tối ưu hóa chi phí chăm sóc da, mang lại làn da căng bóng ngậm nước và đều màu rõ rệt.")
                .publishedDate("28/03/2026")
                .category("Hướng dẫn sử dụng")
                .author("Ngọc Trâm")
                .authorRole("Beauty Specialist")
                .readTime("4 phút đọc")
                .image("/IMG/news03.png")
                .tags("Serum,Hyaluronic Acid,Cấp Ẩm,Skincare Routine")
                .viewsCount(1890L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-013,pc-050")
                .build());

        articles.add(NewsArticle.builder()
                .slug("cach-duong-da-ban-dem-de-phuc-hoi-lan-da")
                .title("Cách dưỡng da ban đêm để phục hồi làn da hiệu quả")
                .excerpt("Ban đêm là thời điểm vàng để tái tạo tế bào, hãy tận dụng chu trình skincare ban đêm đúng cách để thức dậy với làn da rạng rỡ.")
                .content("Ban đêm là khoảng thời gian các tế bào da bước vào quá trình tự phục hồi, sửa chữa tổn thương và tái tạo mạnh mẽ nhất gấp nhiều lần so với ban ngày. Vì vậy, việc xây dựng một chu trình dưỡng da ban đêm hợp lý sẽ giúp tối ưu hóa khả năng hấp thu dưỡng chất của làn da.\n\nBước đầu tiên luôn là làm sạch sâu hai bước (Double Cleansing) bằng dầu/nước tẩy trang và sữa rửa mặt dịu nhẹ. Thao tác này giúp loại bỏ triệt để bụi mịn, dầu thừa tích tụ và cặn kem chống nắng sau một ngày dài năng động.\n\nSau khi cân bằng da bằng toner cấp ẩm, serum và kem dưỡng đêm sẽ là hai nhân tố chủ chốt giúp cung cấp dưỡng chất chuyên sâu và khóa chặt màng ẩm. Vào ban đêm, bạn có thể bổ sung các hoạt chất tái tạo bề mặt như AHA-BHA-PHA hoặc mặt nạ ngủ để tăng cường dưỡng ẩm cho da.\n\nBên cạnh mỹ phẩm, giấc ngủ sâu từ 7 - 8 tiếng mỗi ngày và việc uống đủ nước cũng đóng vai trò then chốt giúp quá trình thanh lọc da diễn ra trơn tru nhất.\n\nMột chu trình chăm sóc da ban đêm chuẩn xác sẽ giúp bạn thức dậy mỗi sáng với làn da mềm mịn, căng mướt và tràn đầy sức sống.")
                .publishedDate("20/03/2026")
                .category("Chăm sóc da")
                .author("PinkyCloud Editorial")
                .authorRole("Ban Biên Tập Chuyên Môn")
                .readTime("5 phút đọc")
                .image("/IMG/news04.png")
                .tags("Dưỡng Ban Đêm,Phục Hồi,Sleeping Mask,Màng Ẩm")
                .viewsCount(2740L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-040,pc-046")
                .build());

        articles.add(NewsArticle.builder()
                .slug("top-thanh-phan-duong-da-nen-co-trong-my-pham")
                .title("Top những thành phần dưỡng da nên có trong mỹ phẩm")
                .excerpt("Những hoạt chất vàng được khoa học chứng minh hiệu quả giúp cải thiện kết cấu và duy trì thanh xuân cho làn da.")
                .content("Khi lựa chọn mỹ phẩm, việc thấu hiểu các thành phần hoạt tính (Active Ingredients) sẽ giúp bạn đưa ra quyết định sáng suốt và chuẩn xác nhất cho tình trạng da của mình mà không bị chi phối bởi quảng cáo hoa mỹ.\n\nThành phần đầu tiên không thể thiếu là Hyaluronic Acid (HA) - hoạt chất ngậm nước huyền thoại có khả năng giữ trọng lượng nước gấp 1000 lần phân tử của nó, giúp tế bào da luôn căng mọng và đàn hồi.\n\nNiacinamide (Vitamin B3) là thành phần đa nhiệm được ưa chuộng bậc nhất hiện nay nhờ công dụng điều tiết dầu thừa, thu nhỏ lỗ chân lông, làm đều màu da và hỗ trợ tăng sinh collagen tự nhiên.\n\nVitamin C tinh khiết (L-Ascorbic Acid) mang lại hiệu quả chống oxy hóa vượt trội, vô hiệu hóa các gốc tự do gây hại và làm mờ các đốm thâm sạm nám một cách bền vững.\n\nTrong khi đó, Ceramide và Panthenol đóng vai trò như 'xi măng sinh học' gắn kết các tế bào sừng, vá lành các vết rạn nứt trên hàng rào bảo vệ da do ô nhiễm hoặc do treatment quá đà.\n\nNắm vững các thành phần này sẽ giúp bạn dễ dàng tự xây dựng một routine chăm sóc da khoa học, tối ưu chi phí và đạt hiệu quả lâu dài.")
                .publishedDate("15/03/2026")
                .category("Kiến thức làm đẹp")
                .author("Dr. Hoàng Lan")
                .authorRole("Cố Vấn Da Liễu PinkyCloud")
                .readTime("6 phút đọc")
                .image("/IMG/news05.png")
                .tags("Thành Phần Vàng,Niacinamide,Vitamin C,Ceramide")
                .viewsCount(4190L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-049,pc-052")
                .build());

        newsArticleRepository.saveAll(articles);
        log.info("Initialized {} sample news articles into SQL Server successfully.", articles.size());
    }

    // =========================================================================
    // Nạp Mã Giảm Giá (Vouchers) vào Database Thật
    // =========================================================================
    private void seedVouchers() {
        if (voucherRepository.count() > 0) {
            log.info("Dữ liệu voucher đã tồn tại ({}), bỏ qua seed.", voucherRepository.count());
            return;
        }

        List<Voucher> vouchers = List.of(
                Voucher.builder()
                        .code("PINKY15")
                        .title("Giảm 15% toàn bộ đơn hàng")
                        .detail("Áp dụng cho đơn từ 499.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fff1f5 0%, #ffd6e3 100%)")
                        .discountPercent(15)
                        .minOrderAmount(new BigDecimal("499000.00"))
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("FREESHIP")
                        .title("Freeship toàn quốc")
                        .detail("Cho đơn từ 299.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fff8df 0%, #ffe38a 100%)")
                        .discountAmount(new BigDecimal("30000.00"))
                        .minOrderAmount(new BigDecimal("299000.00"))
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("HOTDEAL")
                        .title("Giảm 50.000₫ makeup")
                        .detail("Số lượng voucher có hạn mỗi ngày")
                        .status("active")
                        .accent("linear-gradient(135deg, #eef7ff 0%, #cde8ff 100%)")
                        .discountAmount(new BigDecimal("50000.00"))
                        .minOrderAmount(new BigDecimal("350000.00"))
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("SKINCARE10")
                        .title("Giảm 10% dòng dưỡng da")
                        .detail("Áp dụng cho mọi khách hàng mới")
                        .status("active")
                        .accent("linear-gradient(135deg, #f3e5f5 0%, #e1bee7 100%)")
                        .discountPercent(10)
                        .minOrderAmount(new BigDecimal("200000.00"))
                        .active(true)
                        .build()
        );

        voucherRepository.saveAll(vouchers);
        log.info("Initialized {} sample vouchers into SQL Server successfully.", vouchers.size());
    }
}