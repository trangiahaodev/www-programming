package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherDTO {
    private Integer id;
    private String title;
    private String code;
    private String detail;
    private String status;
    private String accent;
    private Integer discountPercent;
    private BigDecimal discountAmount;
    private BigDecimal minOrderAmount;
    private String targetAudience; // ALL, NEW_CUSTOMER, WEEKEND_ONLY, VIP_ONLY
    private String badgeText;
    private Integer priority;
    private boolean isWeekendOnly;
    private boolean isNewCustomerOnly;
    private boolean isVipOnly;
}
