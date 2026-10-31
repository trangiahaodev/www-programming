package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.OrderAnalyticsDTO;
import iuh.wwwprogramming.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Controller
@RequestMapping("/admin/order-statistics")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final OrderService orderService;

    @GetMapping
    public String dashboard(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "5") int limit,
            Model model) {
        DateRange range = resolveRange(period, fromDate, toDate);
        int safeLimit = normalizeLimit(limit);
        model.addAttribute("analytics", orderService.getOrderAnalytics(range.from(), range.to(), safeLimit));
        model.addAttribute("limit", safeLimit);
        model.addAttribute("period", period);
        model.addAttribute("fromDate", range.from());
        model.addAttribute("toDate", range.to());
        model.addAttribute("activeMenu", "order-statistics");
        return "admin/order-statistics";
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(defaultValue = "5") int limit) {
        DateRange range = resolveRange(period, fromDate, toDate);
        List<OrderAnalyticsDTO> analytics = orderService.getOrderAnalytics(range.from(), range.to(), normalizeLimit(limit));
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            var sheet = workbook.createSheet("Thống kê đơn hàng");
            String[] headers = {"STT", "Tên sản phẩm", "Thương hiệu", "Tổng số lượng đã đặt",
                    "Tổng số đơn hàng chứa sản phẩm", "Tổng doanh thu (VNĐ)", "Xu hướng (%)"};
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setFillForegroundColor((short) 5);
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            CellStyle textStyle = workbook.createCellStyle();
            textStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            CellStyle numberStyle = workbook.createCellStyle();
            numberStyle.setAlignment(HorizontalAlignment.RIGHT);
            numberStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            var headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            for (int i = 0; i < analytics.size(); i++) {
                OrderAnalyticsDTO item = analytics.get(i);
                var row = sheet.createRow(i + 1);
                setCell(row.createCell(0), i + 1, numberStyle);
                setCell(row.createCell(1), item.getProductName(), textStyle);
                setCell(row.createCell(2), item.getBrand(), textStyle);
                setCell(row.createCell(3), item.getOrderedQuantity(), numberStyle);
                setCell(row.createCell(4), item.getOrderCount(), numberStyle);
                setCell(row.createCell(5), item.getRevenue().doubleValue(), numberStyle);
                setCell(row.createCell(6), item.getTrendPercent() + "%", numberStyle);
            }
            int[] widths = {8, 34, 22, 20, 32, 22, 16};
            for (int i = 0; i < widths.length; i++) {
                sheet.setColumnWidth(i, widths[i] * 256);
            }
            sheet.createFreezePane(0, 1);
            workbook.write(output);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"thong-ke-don-hang.xlsx\"")
                    .contentType(MediaType.parseMediaType(
                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(output.toByteArray());
        } catch (IOException e) {
            throw new IllegalStateException("Không thể tạo báo cáo Excel.", e);
        }
    }

    private void setCell(Cell cell, String value, CellStyle style) {
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    private void setCell(Cell cell, long value, CellStyle style) {
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void setCell(Cell cell, double value, CellStyle style) {
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private int normalizeLimit(int limit) {
        return Math.min(Math.max(limit, 1), 100);
    }

    private DateRange resolveRange(String period, LocalDate fromDate, LocalDate toDate) {
        LocalDate today = LocalDate.now();
        return switch (period) {
            case "today" -> new DateRange(today, today);
            case "week" -> new DateRange(today.minusDays(today.getDayOfWeek().getValue() - 1), today);
            case "custom" -> {
                if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
                    throw new IllegalArgumentException("Khoảng ngày tùy chỉnh không hợp lệ.");
                }
                yield new DateRange(fromDate, toDate);
            }
            default -> {
                YearMonth month = YearMonth.from(today);
                yield new DateRange(month.atDay(1), today);
            }
        };
    }

    private record DateRange(LocalDate from, LocalDate to) {
    }
}
