package iuh.wwwprogramming.controller.api;

import iuh.wwwprogramming.dto.VoucherDTO;
import iuh.wwwprogramming.entity.Voucher;
import iuh.wwwprogramming.repository.VoucherRepository;
import iuh.wwwprogramming.service.HomeContentService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@RestController
@RequestMapping("/api/vouchers")
@RequiredArgsConstructor
public class VoucherApiController {

    private final VoucherRepository voucherRepository;
    private final HomeContentService homeContentService;

    // DTO cho yêu cầu kiểm tra/áp dụng voucher
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VoucherApplyRequest {
        private String code;
        private BigDecimal orderAmount;
        private String customerTier; // NEW, STANDARD, VIP
        private Boolean hasUsedNewCustomerVoucher;
        private Boolean isWeekendOverride;
    }

    // DTO cho kết quả trả về
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VoucherApplyResponse {
        private boolean valid;
        private String code;
        private String title;
        private String message;
        private String errorType; // NONE, NOT_FOUND, VIP_ONLY, WEEKEND_ONLY, NEW_CUSTOMER_ONLY, MIN_ORDER_AMOUNT, EXPIRED
        private BigDecimal discountAmount;
        private BigDecimal finalAmount;
        private String formattedDiscount;
    }

    /**
     * API Lấy danh sách Voucher linh hoạt (Client-side / Simulation fetch)
     */
    @GetMapping
    public ResponseEntity<List<VoucherDTO>> getVouchers(
            @RequestParam(name = "weekend", required = false) Boolean weekend,
            @RequestParam(name = "tier", required = false) String tier,
            @RequestParam(name = "usedNew", required = false, defaultValue = "false") boolean usedNew
    ) {
        List<VoucherDTO> vouchers = homeContentService.getActiveVouchers(weekend, tier, usedNew);
        return ResponseEntity.ok(vouchers);
    }

    /**
     * API Áp dụng và Kiểm tra Điều kiện Nghiệp vụ Voucher
     */
    @PostMapping("/apply")
    public ResponseEntity<VoucherApplyResponse> applyVoucher(@RequestBody VoucherApplyRequest request) {
        if (request.getCode() == null || request.getCode().trim().isEmpty()) {
            return ResponseEntity.ok(VoucherApplyResponse.builder()
                    .valid(false)
                    .errorType("NOT_FOUND")
                    .message("Vui lòng nhập mã giảm giá hợp lệ.")
                    .build());
        }

        String cleanCode = request.getCode().trim().toUpperCase();
        Optional<Voucher> voucherOpt = voucherRepository.findByCodeIgnoreCaseAndActiveTrue(cleanCode);

        if (voucherOpt.isEmpty()) {
            return ResponseEntity.ok(VoucherApplyResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .errorType("NOT_FOUND")
                    .message("Mã giảm giá \"" + cleanCode + "\" không tồn tại hoặc đã hết hạn sử dụng.")
                    .build());
        }

        Voucher voucher = voucherOpt.get();
        BigDecimal orderAmount = (request.getOrderAmount() != null && request.getOrderAmount().compareTo(BigDecimal.ZERO) > 0)
                ? request.getOrderAmount()
                : new BigDecimal("500000.00"); // Giá trị đơn mặc định nếu client chưa truyền

        String tier = (request.getCustomerTier() != null && !request.getCustomerTier().trim().isEmpty())
                ? request.getCustomerTier().trim().toUpperCase()
                : "NEW";

        // 1. Kiểm tra điều kiện VIP:
        if ("VIP_ONLY".equalsIgnoreCase(voucher.getTargetAudience()) || "VIPBEAUTY".equalsIgnoreCase(cleanCode)) {
            if (!"VIP".equalsIgnoreCase(tier)) {
                String currentTierLabel = "NEW".equalsIgnoreCase(tier) ? "Khách hàng Mới" : "Thành viên Tiêu chuẩn";
                return ResponseEntity.ok(VoucherApplyResponse.builder()
                        .valid(false)
                        .code(cleanCode)
                        .title(voucher.getTitle())
                        .errorType("VIP_ONLY")
                        .message("Mã " + cleanCode + " là đặc quyền dành riêng cho khách hàng hạng VIP (Tổng tích lũy chi tiêu từ 5.000.000₫). Bạn hiện đang là " + currentTierLabel + " nên chưa đủ điều kiện áp dụng mã này.")
                        .build());
            }
        }

        // 2. Kiểm tra điều kiện Khách hàng mới:
        if ("NEW_CUSTOMER".equalsIgnoreCase(voucher.getTargetAudience()) || "PINKYNEW".equalsIgnoreCase(cleanCode)) {
            if (Boolean.TRUE.equals(request.getHasUsedNewCustomerVoucher())) {
                return ResponseEntity.ok(VoucherApplyResponse.builder()
                        .valid(false)
                        .code(cleanCode)
                        .title(voucher.getTitle())
                        .errorType("NEW_CUSTOMER_ONLY")
                        .message("Mã " + cleanCode + " chỉ áp dụng cho đơn hàng đầu tiên của khách hàng mới. Bạn đã từng sử dụng mã này trước đó.")
                        .build());
            }
        }

        // 3. Kiểm tra điều kiện Cuối tuần:
        if ("WEEKEND_ONLY".equalsIgnoreCase(voucher.getTargetAudience()) || "WEEKEND".equalsIgnoreCase(cleanCode)) {
            boolean isWeekend;
            if (request.getIsWeekendOverride() != null) {
                isWeekend = request.getIsWeekendOverride();
            } else {
                DayOfWeek dow = LocalDate.now().getDayOfWeek();
                isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);
            }

            if (!isWeekend) {
                return ResponseEntity.ok(VoucherApplyResponse.builder()
                        .valid(false)
                        .code(cleanCode)
                        .title(voucher.getTitle())
                        .errorType("WEEKEND_ONLY")
                        .message("Mã Flash Voucher " + cleanCode + " chỉ áp dụng vào các ngày cuối tuần (Thứ 7 & Chủ Nhật). Hãy quay lại vào cuối tuần nhé!")
                        .build());
            }
        }

        // 4. Kiểm tra điều kiện Giá trị đơn hàng tối thiểu:
        if (voucher.getMinOrderAmount() != null && orderAmount.compareTo(voucher.getMinOrderAmount()) < 0) {
            String formattedMin = formatVnd(voucher.getMinOrderAmount());
            return ResponseEntity.ok(VoucherApplyResponse.builder()
                    .valid(false)
                    .code(cleanCode)
                    .title(voucher.getTitle())
                    .errorType("MIN_ORDER_AMOUNT")
                    .message("Đơn hàng của bạn chưa đạt mức tối thiểu " + formattedMin + " để áp dụng mã " + cleanCode + " (" + voucher.getDetail() + ").")
                    .build());
        }

        // 5. Tính toán mức giảm giá:
        BigDecimal discount = BigDecimal.ZERO;
        if (voucher.getDiscountPercent() != null && voucher.getDiscountPercent() > 0) {
            discount = orderAmount.multiply(BigDecimal.valueOf(voucher.getDiscountPercent()))
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);
            // Giới hạn giảm tối đa cho mã VIP nếu có (200.000₫)
            if ("VIPBEAUTY".equalsIgnoreCase(cleanCode) && discount.compareTo(new BigDecimal("200000.00")) > 0) {
                discount = new BigDecimal("200000.00");
            }
        } else if (voucher.getDiscountAmount() != null && voucher.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
            discount = voucher.getDiscountAmount();
        }

        if (discount.compareTo(orderAmount) > 0) {
            discount = orderAmount;
        }

        BigDecimal finalAmount = orderAmount.subtract(discount);

        return ResponseEntity.ok(VoucherApplyResponse.builder()
                .valid(true)
                .code(cleanCode)
                .title(voucher.getTitle())
                .message("Áp dụng thành công mã " + cleanCode + "! " + voucher.getTitle() + " (-" + formatVnd(discount) + ")")
                .errorType("NONE")
                .discountAmount(discount)
                .finalAmount(finalAmount)
                .formattedDiscount(formatVnd(discount))
                .build());
    }

    private String formatVnd(BigDecimal amount) {
        if (amount == null) return "0₫";
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        return nf.format(amount) + "₫";
    }
}
