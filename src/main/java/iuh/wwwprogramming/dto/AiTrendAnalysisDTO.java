package iuh.wwwprogramming.dto;

public record AiTrendAnalysisDTO(
        String summary,
        long totalClicks,
        double averageDwellSeconds,
        long addToCartCount,
        long orderCount,
        String topAttentionProduct,
        String trendObservation
) {
}
