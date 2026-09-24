package iuh.wwwprogramming.dto;

import iuh.wwwprogramming.entity.OrderStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderFilterDTO {

    @Size(max = 100, message = "Từ khóa tìm kiếm không được vượt quá 100 ký tự")
    private String keyword;

    private OrderStatus status;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate fromDate;

    @DateTimeFormat(pattern = "dd/MM/yyyy")
    private LocalDate toDate;

    @Builder.Default
    @Min(value = 0, message = "Số trang phải từ 0 trở lên")
    private int page = 0;

    @Builder.Default
    @Min(value = 1, message = "Kích thước trang phải từ 1 trở lên")
    private int size = 10;

    public boolean isDateRangeInvalid() {
        return fromDate != null && toDate != null && fromDate.isAfter(toDate);
    }
}
