package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.AiTrendAnalysisDTO;
import iuh.wwwprogramming.service.PinkyAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminAiTrendController {

    private final PinkyAiService pinkyAiService;

    @GetMapping("/ai-trends")
    public String viewAiTrends(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            Model model) {

        DateRange range = resolveRange(period, fromDate, toDate);
        AiTrendAnalysisDTO analysis = pinkyAiService.analyzeTrendSummary(range.from(), range.to());

        model.addAttribute("analysis", analysis);
        model.addAttribute("period", period);
        model.addAttribute("fromDate", range.from());
        model.addAttribute("toDate", range.to());
        model.addAttribute("activeMenu", "ai-trends");
        return "admin/ai-trends";
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
