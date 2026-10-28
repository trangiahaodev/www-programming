package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.AiTrendAnalysisDTO;
import iuh.wwwprogramming.dto.TopProductStatDTO;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.entity.UserActivityLog;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.repository.UserActivityLogRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class PinkyAiService {

    private final UserActivityLogRepository activityRepository;
    private final OrderRepository orderRepository;
    private final ObjectProvider<ChatClient.Builder> chatClientBuilder;

    public PinkyAiService(UserActivityLogRepository activityRepository,
                          OrderRepository orderRepository,
                          ObjectProvider<ChatClient.Builder> chatClientBuilder) {
        this.activityRepository = activityRepository;
        this.orderRepository = orderRepository;
        this.chatClientBuilder = chatClientBuilder;
    }

    @Transactional
    public void recordActivity(String sessionId, UserActivityLog activity) {
        activity.setSessionId(sessionId);
        activityRepository.save(activity);
    }

    public String analyzeSession(String sessionId, List<TopProductStatDTO> topProducts) {
        List<UserActivityLog> activities = activityRepository.findBySessionIdOrderByOccurredAtAsc(sessionId);
        if (activities.isEmpty()) {
            return "Phiên hiện tại chưa có dữ liệu tương tác để phân tích.";
        }

        ChatClient.Builder builder = chatClientBuilder.getIfAvailable();
        if (builder == null) {
            return "AI chưa được cấu hình. Hãy thiết lập biến môi trường OPENAI_API_KEY để bật tư vấn.";
        }

        long clicks = activities.stream().mapToLong(a -> value(a.getClickCount())).sum();
        long dwell = activities.stream().mapToLong(a -> value(a.getDwellSeconds())).sum();
        long addToCart = activities.stream().mapToLong(a -> value(a.getAddToCartCount())).sum();
        String viewedProducts = activities.stream()
                .map(UserActivityLog::getProductName)
                .filter(name -> name != null && !name.isBlank())
                .distinct()
                .limit(10)
                .collect(Collectors.joining(", "));
        String popularProducts = topProducts.stream()
                .map(product -> product.getProductName() + " (" + product.getOrderedQuantity() + " lượt đặt)")
                .collect(Collectors.joining(", "));

        PromptTemplate template = new PromptTemplate("""
                Bạn là Pinky, trợ lý mỹ phẩm của PinkyCloud. Hãy phân tích thói quen mua sắm
                dựa trên đúng dữ liệu được cung cấp, không bịa thông tin y khoa và không khẳng định
                sản phẩm chữa bệnh. Trả lời bằng tiếng Việt, ngắn gọn 2-3 đoạn, gồm nhận định,
                sản phẩm phù hợp và một lời khuyên thực tế.

                Dữ liệu phiên:
                - Số lượt click: {clicks}
                - Tổng thời gian xem (giây): {dwell}
                - Số lần thêm giỏ: {addToCart}
                - Sản phẩm đã xem: {viewedProducts}
                Top sản phẩm được đặt nhiều trong hệ thống:
                {popularProducts}
                """);
        Prompt prompt = template.create(Map.of(
                "clicks", clicks,
                "dwell", dwell,
                "addToCart", addToCart,
                "viewedProducts", viewedProducts.isBlank() ? "Chưa xác định" : viewedProducts,
                "popularProducts", popularProducts.isBlank() ? "Chưa có dữ liệu" : popularProducts));

        return builder.build().prompt(prompt).call().content();
    }

    public AiTrendAnalysisDTO analyzeTrendSummary(LocalDate fromDate, LocalDate toDate) {
        LocalDateTime from = fromDate.atStartOfDay();
        LocalDateTime to = toDate.plusDays(1).atStartOfDay();

        long totalClicks = activityRepository.sumClickCountBetween(from, to);
        long addToCartCount = activityRepository.sumAddToCartCountBetween(from, to);
        double averageDwellSeconds = Optional.ofNullable(activityRepository.averageDwellSecondsBetween(from, to)).orElse(0d);
        long orderCount = orderRepository.countByCreatedAtBetweenAndStatusNot(from, to, OrderStatus.CANCELLED);

        String topAttentionProduct = activityRepository.findTopProductSignalsBetween(from, to).stream()
                .findFirst()
                .map(row -> String.valueOf(row[0]))
                .orElse("Chưa có dữ liệu");

        String trendObservation = topAttentionProduct.equals("Chưa có dữ liệu")
                ? "Hệ thống chưa ghi nhận hành vi mua sắm đáng chú ý trong khoảng thời gian này."
                : "Sản phẩm đang nhận được nhiều sự chú ý nhưng chưa chuyển đổi mạnh, phù hợp với chiến lược tăng tỉ lệ chuyển đổi bằng khuyến mại và ưu tiên hiển thị.";

        String summary = buildSystemTrendSummary(fromDate, toDate, totalClicks, addToCartCount, averageDwellSeconds, orderCount, topAttentionProduct);
        return new AiTrendAnalysisDTO(summary, totalClicks, averageDwellSeconds, addToCartCount, orderCount, topAttentionProduct, trendObservation);
    }

    private String buildSystemTrendSummary(LocalDate fromDate,
                                          LocalDate toDate,
                                          long totalClicks,
                                          long addToCartCount,
                                          double averageDwellSeconds,
                                          long orderCount,
                                          String topAttentionProduct) {
        ChatClient.Builder builder = chatClientBuilder.getIfAvailable();
        if (builder == null) {
            return "Trong khoảng thời gian " + formatRange(fromDate, toDate)
                    + ", hệ thống ghi nhận " + totalClicks + " lượt click, thời gian xem trung bình " + formatSeconds(averageDwellSeconds)
                    + ", " + addToCartCount + " lần thêm vào giỏ hàng và " + orderCount + " đơn hàng thực tế. Sản phẩm đáng chú ý nhất là "
                    + topAttentionProduct + ". Nên ưu tiên làm nổi bật sản phẩm này trên trang chủ và kiểm soát tồn kho để nâng tỷ lệ chuyển đổi.";
        }

        List<Object[]> topSignals = activityRepository.findTopProductSignalsBetween(fromDate.atStartOfDay(), toDate.plusDays(1).atStartOfDay());
        String signalText = topSignals.stream()
                .limit(5)
                .map(row -> {
                    String name = String.valueOf(row[0]);
                    long clicks = row[1] == null ? 0L : ((Number) row[1]).longValue();
                    long addToCart = row[2] == null ? 0L : ((Number) row[2]).longValue();
                    return name + " (click " + clicks + ", add-to-cart " + addToCart + ")";
                })
                .collect(Collectors.joining("; "));

        PromptTemplate template = new PromptTemplate("""
                Bạn là quản lý bán hàng / chuyên gia phân tích hành vi người dùng của PinkyCloud. Dựa trên dữ liệu thực tế dưới đây, viết một đoạn tóm tắt tối đa 3-4 câu bằng tiếng Việt, chuyên nghiệp, với 3 phần rõ ràng: (1) sản phẩm đang thu hút nhiều sự chú ý nhưng chưa chuyển đổi, (2) xu hướng tìm kiếm mỹ phẩm trong khoảng thời gian chọn, (3) gợi ý kinh doanh / nhập hàng hợp lý cho cửa hàng.

                Dữ liệu:
                - Khoảng thời gian: {range}
                - Tổng lượt click: {totalClicks}
                - Thời gian xem trung bình: {avgDwell}
                - Số lần thêm giỏ: {addToCart}
                - Số đơn hàng thực tế: {orderCount}
                - Sản phẩm có tín hiệu tương tác cao: {signals}
                """);

        Prompt prompt = template.create(Map.of(
                "range", formatRange(fromDate, toDate),
                "totalClicks", totalClicks,
                "avgDwell", formatSeconds(averageDwellSeconds),
                "addToCart", addToCartCount,
                "orderCount", orderCount,
                "signals", signalText.isBlank() ? "Chưa có dữ liệu" : signalText
        ));

        return builder.build().prompt(prompt).call().content();
    }

    private String formatRange(LocalDate fromDate, LocalDate toDate) {
        return fromDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                + " - " + toDate.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private String formatSeconds(double seconds) {
        if (seconds <= 0) {
            return "0 giây";
        }
        if (seconds < 60) {
            return Math.round(seconds) + " giây";
        }
        long minutes = Math.round(seconds / 60);
        return minutes + " phút";
    }

    private long value(Integer value) {
        return value == null ? 0 : value;
    }
}
