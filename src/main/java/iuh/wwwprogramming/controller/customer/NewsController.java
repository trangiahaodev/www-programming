package iuh.wwwprogramming.controller.customer;

import iuh.wwwprogramming.dto.NewsDTO;
import iuh.wwwprogramming.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping({"/tin-tuc", "/news", "/cam-nang"})
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping
    public String listNews(
            @RequestParam(name = "category", required = false, defaultValue = "all") String category,
            @RequestParam(name = "keyword", required = false) String keyword,
            Model model
    ) {
        List<NewsDTO> articles = newsService.getAllNews(category, keyword);
        List<String> categories = newsService.getNewsCategories();
        NewsDTO featuredArticle = newsService.getFeaturedArticle();

        model.addAttribute("articles", articles);
        model.addAttribute("newsList", articles);
        model.addAttribute("featuredArticle", featuredArticle);
        model.addAttribute("categories", categories);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("totalArticles", articles.size());
        model.addAttribute("activeMenu", "news");

        return "customer/news-list";
    }

    @GetMapping("/{slug}")
    public String newsDetail(
            @PathVariable("slug") String slug,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        NewsDTO article = newsService.getNewsBySlug(slug);

        if (article == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy bài viết yêu cầu hoặc bài viết đã bị gỡ bỏ.");
            return "redirect:/tin-tuc";
        }

        List<NewsDTO> relatedNews = newsService.getRelatedNews(article.getId(), article.getCategory(), 3);

        model.addAttribute("article", article);
        model.addAttribute("news", article);
        model.addAttribute("relatedNews", relatedNews);
        model.addAttribute("activeMenu", "news");

        return "customer/news-detail";
    }

    @PostMapping("/subscribe")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> subscribeNewsletter(@RequestBody(required = false) Map<String, String> payload) {
        String email = payload != null ? payload.get("email") : "";
        if (email == null || !email.contains("@") || !email.contains(".")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Vui lòng nhập địa chỉ email hợp lệ!"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Đăng ký thành công! Cẩm nang Skincare & E-voucher 10% đã được gửi tới email của bạn.",
                "email", email
        ));
    }
}
