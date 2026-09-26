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
import java.util.Optional;

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

        // Bài viết 6: Sử dụng news06.png
        articles.add(NewsArticle.builder()
                .slug("nghe-thuat-trang-diem-ca-nhan-bo-co-va-bang-mau")
                .title("Nghệ thuật trang điểm cá nhân: Bộ cọ & Bảng màu dành cho người mới bắt đầu")
                .excerpt("Hướng dẫn chọn bộ cọ chuyên nghiệp và phối màu mắt, má hồng tự nhiên giúp người mới bắt đầu tự tin biến hóa phong cách trang điểm hàng ngày.")
                .content("Trang điểm cá nhân không chỉ là cách để tôn lên những nét đẹp tự nhiên trên khuôn mặt mà còn là một nghệ thuật giúp bạn tự tin hơn mỗi ngày. Tuy nhiên, đối với người mới bắt đầu, việc đối mặt với hàng chục loại cọ khác nhau và vô số bảng màu mắt, phấn má thường mang lại cảm giác bối rối.\n\nĐể bắt đầu một cách hiệu quả, bạn không cần phải sở hữu trọn bộ cọ chuyên nghiệp 24 cây. Một bộ cọ cơ bản gồm 5 cây thiết yếu là đã đủ: cọ tán kem nền hoặc mút trang điểm hình giọt nước, cọ phấn phủ đầu tròn to xốp, cọ vát xéo dành cho má hồng và tạo khối, cọ tán màu mắt bản dẹt và một cây cọ blending đầu tròn để làm mềm các đường ranh giới phấn mắt. Vệ sinh cọ định kỳ mỗi tuần cũng là nguyên tắc vàng để ngăn ngừa mụn và giữ sợi lông cọ luôn mềm mại.\n\nVề bảng màu trang điểm, người mới nên bắt đầu với các tone màu trung tính ấm (Warm Neutral) như cam đất, hồng đào nude, nâu ấm hoặc be san hô. Những gam màu này có ưu điểm lớn là rất tôn da phụ nữ Á Đông, dễ phối hợp với mọi trang phục công sở hay dạo phố, và hạn chế tối đa nguy cơ bị lỗi trang điểm quá đậm hay lệch tone.\n\nBên cạnh cọ và phấn màu, một lớp nền mỏng mịn tệp màu da và một thỏi son lì chuẩn sắc chính là điểm tựa hoàn hảo. Hãy nhớ quy tắc cân bằng: nếu bạn chọn nhấn vào đôi mắt sâu cuốn hút, hãy tiết chế màu son môi bằng các tone nude dịu dàng; ngược lại, một đôi môi đỏ quyến rũ sẽ đẹp nhất khi đi kèm bầu mắt trong trẻo nhẹ nhàng.")
                .publishedDate("18/04/2026")
                .category("Trang điểm")
                .author("Minh Thư")
                .authorRole("Makeup Artist")
                .readTime("5 phút đọc")
                .image("/IMG/news06.png")
                .tags("Trang Điểm,Cọ Trang Điểm,Bảng Màu,Makeup Cơ Bản")
                .viewsCount(3250L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-028,pc-032")
                .build());

        // Bài viết 7: Sử dụng news07.png
        articles.add(NewsArticle.builder()
                .slug("xu-huong-son-moi-tone-hoa-anh-dao-va-phan-ma-bat-sang")
                .title("Xu hướng son môi Tone Hoa Anh Đào & Phấn má bắt sáng Mùa Xuân")
                .excerpt("Khám phá vẻ đẹp ngọt ngào của sắc son cánh hoa anh đào kết hợp phấn má bắt sáng căng mọng, định hình diện mạo tươi mới và trẻ trung.")
                .content("Mùa xuân và đầu hè luôn là thời điểm lên ngôi của phong cách trang điểm ngọt ngào, rạng rỡ lấy cảm hứng từ những cánh hoa anh đào (Sakura Blossom) mong manh và trong trẻo. Xu hướng này hướng đến sự tươi mới, tràn đầy sức sống của làn da với hai điểm nhấn chủ đạo: màu son cánh hoa và đôi gò má ửng hồng phủ ánh ngọc trai.\n\nSắc son hoa anh đào là sự hòa quyện tinh tế giữa sắc hồng baby dịu mát và ánh san hô ấm áp, mang lại hiệu ứng đôi môi căng mọng như được ngậm nước. Các công thức son tint bóng nhẹ (Juicy Dewy Lip) hoặc son thỏi lì hiệu ứng nhung mờ (Velvet Blurred) đang là hai kết cấu được săn đón nhiều nhất, giúp làm đầy rãnh môi và tạo cảm giác đôi môi tròn đầy tự nhiên.\n\nĐể kết hợp hoàn hảo cùng son môi Sakura, kỹ thuật đánh phấn má bắt sáng (Highlighter & Blush Drape) là chìa khóa không thể thiếu. Thay vì chỉ đánh tròn trên gò má, chuyên gia khuyên bạn nên tán phấn má theo hình chữ C nối liền từ xương gò má lên thái dương, sau đó chấm một chút phấn bắt sáng dạng lỏng lên sống mũi và nhân trung để bắt trọn ánh sáng tự nhiên.\n\nĐừng quên dưỡng ẩm môi thật kỹ vào ban đêm với mặt nạ ủ môi giàu dưỡng chất từ quả mọng để đôi môi luôn mềm mịn, sẵn sàng khoác lên sắc son mùa xuân rực rỡ nhất mà không lo lộ vân môi hay bong tróc.")
                .publishedDate("22/04/2026")
                .category("Xu hướng làm đẹp")
                .author("PinkyCloud Editorial")
                .authorRole("Ban Biên Tập Chuyên Môn")
                .readTime("4 phút đọc")
                .image("/IMG/news07.png")
                .tags("Son Môi,Sakura,Tone Hồng,Makeup Mùa Xuân,Phấn Má")
                .viewsCount(2890L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-040,pc-028")
                .build());

        // Bài viết 8: Sử dụng news08.png
        articles.add(NewsArticle.builder()
                .slug("quy-trinh-duong-da-buoi-sang-3-buoc-toi-gian")
                .title("Quy trình dưỡng da buổi sáng (Morning Routine) 3 bước tối giản")
                .excerpt("Khởi đầu ngày mới với chu trình skincare 3 bước nhanh gọn nhưng đầy đủ bảo vệ, giúp bạn tiết kiệm thời gian mà da vẫn căng mướt suốt ngày dài.")
                .content("Trong nhịp sống bận rộn hiện đại, việc duy trì một chu trình dưỡng da buổi sáng gồm 7 đến 10 bước phức tạp thường khiến nhiều người cảm thấy quá tải và khó kiên trì. May mắn thay, các bác sĩ da liễu đều đồng thuận rằng: Buổi sáng là thời điểm bảo vệ (Protect), khác với ban đêm là thời điểm phục hồi (Repair). Vì vậy, một chu trình Morning Routine 3 bước chuẩn y khoa là hoàn toàn đủ để làn da tỏa sáng và an toàn trước các tác nhân môi trường.\n\nBước 1: Làm sạch nhẹ nhàng (Gentle Cleansing). Sau một đêm dài ngủ trong phòng máy lạnh, da chỉ tích tụ một lớp dầu tự nhiên và bụi từ chăn gối. Hãy ưu tiên các dòng sữa rửa mặt dịu nhẹ có độ pH chuẩn 5.0 - 5.5, không chứa xà phòng tạo bọt quá mạnh. Việc làm sạch nhẹ nhàng giúp thông thoáng lỗ chân lông mà không làm tổn hại đến màng acid sinh học bảo vệ da.\n\nBước 2: Cấp ẩm và chống oxy hóa (Hydrate & Antioxidant). Serum chứa Hyaluronic Acid đa phân tử hoặc Niacinamide nồng độ vừa phải sẽ nhanh chóng bù đắp lượng nước thiếu hụt, giúp da căng mướt và đàn hồi ngay tức thì. Hoạt chất này cũng tạo lớp màng ẩm vững chắc giúp các bước trang điểm tiếp theo tệp đều và không bị mốc phấn.\n\nBước 3: Chống nắng toàn diện (Sun Protection). Đây là bước quan trọng nhất quyết định đến 90% thành công của chu trình chăm sóc da. Một loại kem chống nắng phổ rộng SPF50+ PA++++ với kết cấu mỏng nhẹ, thoáng mịn không bết rít sẽ tạo thành tấm khiên kiên cố bảo vệ tế bào da khỏi tia UVA, UVB, ánh sáng xanh và bụi mịn PM2.5. Thoa đủ 2 lóng ngón tay kem chống nắng trước khi ra ngoài 20 phút để da được bảo vệ tối ưu.")
                .publishedDate("25/04/2026")
                .category("Hướng dẫn sử dụng")
                .author("Dr. Hoàng Lan")
                .authorRole("Cố Vấn Da Liễu PinkyCloud")
                .readTime("5 phút đọc")
                .image("/IMG/news08.png")
                .tags("Morning Routine,Chống Nắng,Dưỡng Ẩm,Tối Giản,Skincare Buổi Sáng")
                .viewsCount(3760L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-001,pc-013,pc-011")
                .build());

        // Bài viết 9: Sử dụng news09.png
        articles.add(NewsArticle.builder()
                .slug("chiet-xuat-tra-xanh-va-thao-moc-tu-nhien-cho-da-dau-mun")
                .title("Chiết xuất Trà xanh & Thảo mộc tự nhiên: Khắc tinh của làn da dầu mụn")
                .excerpt("Sức mạnh kháng viêm và chống oxy hóa vượt trội từ lá trà xanh và các loài thảo mộc phương Đông trong việc thanh lọc và làm dịu làn da dầu mụn.")
                .content("Đối với những ai sở hữu làn da dầu nhờn và dễ nổi mụn, việc tìm kiếm một giải pháp kiểm soát dầu thừa mà không làm khô căng hay kích ứng da luôn là bài toán nan giải. Trong số các thành phần thiên nhiên được khoa học hiện đại kiểm chứng, chiết xuất lá trà xanh (Camellia Sinensis) cùng các loại thảo mộc hữu cơ đang nổi lên như một cứu tinh toàn diện cho làn da nhiệt đới.\n\nLá trà xanh chứa nồng độ EGCG (Epigallocatechin Gallate) vô cùng đậm đặc - một hợp chất chống oxy hóa tự nhiên mạnh mẽ gấp 100 lần Vitamin C và 25 lần Vitamin E. EGCG có khả năng ức chế enzym sản sinh bã nhờn quá mức, làm dịu tức thì các ổ viêm sưng đỏ của mụn và ngăn ngừa vi khuẩn P.acnes sinh sôi phát triển trên bề mặt da.\n\nBên cạnh trà xanh, sự kết hợp với tinh dầu tràm trà (Tea Tree) và rau má (Centella Asiatica) tạo nên bộ ba thảo mộc hoàn hảo. Tràm trà đóng vai trò kháng khuẩn tự nhiên làm se cồi mụn nhanh chóng, trong khi Madecassoside từ rau má tăng tốc quá trình tái tạo mô liên kết, ngăn ngừa sẹo thâm và sẹo lõm hình thành sau mụn.\n\nKhi tích hợp các sản phẩm chiết xuất trà xanh và thảo mộc vào chu trình chăm sóc da hàng ngày - từ gel rửa mặt cân bằng pH, toner thanh lọc đến serum phục hồi - bạn sẽ cảm nhận được sự dịu mát, nền da thông thoáng nhẹ tênh và tình trạng bóng dầu giảm rõ rệt chỉ sau 2 đến 3 tuần sử dụng đều đặn.")
                .publishedDate("27/04/2026")
                .category("Kiến thức làm đẹp")
                .author("Ngọc Trâm")
                .authorRole("Beauty Specialist")
                .readTime("6 phút đọc")
                .image("/IMG/news09.png")
                .tags("Trà Xanh,Thảo Mộc,Da Dầu Mụn,Thanh Lọc Da,EGCG,Tràm Trà")
                .viewsCount(4120L)
                .isFeatured(false)
                .active(true)
                .linkedProductIds("pc-009,pc-016,pc-046")
                .build());

        int seededCount = 0;
        for (NewsArticle article : articles) {
            if (!newsArticleRepository.existsBySlug(article.getSlug())) {
                newsArticleRepository.save(article);
                seededCount++;
            }
        }
        log.info("Initialized {} new news articles into SQL Server (total available: {}).", seededCount, articles.size());
    }

    // =========================================================================
    // Nạp Mã Giảm Giá (Vouchers) vào Database Thật
    // =========================================================================
    private void seedVouchers() {
        List<Voucher> vouchers = List.of(
                Voucher.builder()
                        .code("PINKYNEW")
                        .title("Chào bạn mới: Giảm 20.000₫")
                        .detail("Áp dụng cho đơn hàng đầu tiên từ 199.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #e8f5e9 0%, #c8e6c9 100%)")
                        .discountAmount(new BigDecimal("20000.00"))
                        .minOrderAmount(new BigDecimal("199000.00"))
                        .targetAudience("NEW_CUSTOMER")
                        .badgeText("🌟 CHÀO BẠN MỚI")
                        .priority(100)
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("WEEKEND")
                        .title("Flash Voucher Cuối Tuần 70.000₫")
                        .detail("Dành riêng cho đơn hàng cuối tuần từ 599.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fce4ec 0%, #f8bbd0 100%)")
                        .discountAmount(new BigDecimal("70000.00"))
                        .minOrderAmount(new BigDecimal("599000.00"))
                        .targetAudience("WEEKEND_ONLY")
                        .badgeText("⚡ FLASH DEAL CUỐI TUẦN")
                        .priority(90)
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("VIPBEAUTY")
                        .title("Đặc quyền VIP Pinky: Giảm 20%")
                        .detail("Tối đa 200.000₫ cho đơn từ 1.200.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fff3e0 0%, #ffe0b2 100%)")
                        .discountPercent(20)
                        .minOrderAmount(new BigDecimal("1200000.00"))
                        .targetAudience("VIP_ONLY")
                        .badgeText("👑 ĐẶC QUYỀN VIP")
                        .priority(80)
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("PINKY15")
                        .title("Giảm 15% toàn bộ đơn hàng")
                        .detail("Áp dụng cho đơn từ 499.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #fff1f5 0%, #ffd6e3 100%)")
                        .discountPercent(15)
                        .minOrderAmount(new BigDecimal("499000.00"))
                        .targetAudience("ALL")
                        .badgeText("🔥 GIẢM 15%")
                        .priority(70)
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
                        .targetAudience("ALL")
                        .badgeText("🚚 FREESHIP")
                        .priority(60)
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
                        .targetAudience("ALL")
                        .badgeText("💄 MAKEUP")
                        .priority(50)
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
                        .targetAudience("ALL")
                        .badgeText("🌿 DƯỠNG DA")
                        .priority(40)
                        .active(true)
                        .build(),
                Voucher.builder()
                        .code("COMBO3")
                        .title("Combo Tiết Kiệm: Giảm 100.000₫")
                        .detail("Áp dụng cho đơn hàng mỹ phẩm từ 899.000₫")
                        .status("active")
                        .accent("linear-gradient(135deg, #ede7f6 0%, #d1c4e9 100%)")
                        .discountAmount(new BigDecimal("100000.00"))
                        .minOrderAmount(new BigDecimal("899000.00"))
                        .targetAudience("ALL")
                        .badgeText("🎁 COMBO 3 MÓN")
                        .priority(30)
                        .active(true)
                        .build()
        );

        int seededVoucherCount = 0;
        for (Voucher voucher : vouchers) {
            Optional<Voucher> existing = voucherRepository.findByCodeIgnoreCaseAndActiveTrue(voucher.getCode());
            if (existing.isPresent()) {
                Voucher ev = existing.get();
                ev.setTargetAudience(voucher.getTargetAudience());
                ev.setBadgeText(voucher.getBadgeText());
                ev.setPriority(voucher.getPriority());
                ev.setDiscountPercent(voucher.getDiscountPercent());
                ev.setDiscountAmount(voucher.getDiscountAmount());
                ev.setMinOrderAmount(voucher.getMinOrderAmount());
                voucherRepository.save(ev);
            } else {
                voucherRepository.save(voucher);
                seededVoucherCount++;
            }
        }
        log.info("Initialized/Updated {} vouchers into SQL Server (total available: {}).", seededVoucherCount, vouchers.size());
    }
}