package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.service.CategoryService;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final iuh.wwwprogramming.service.HomeContentService homeContentService;

    // =========================================================================
    // 1. LUỒNG CUSTOMER (Khách hàng truy cập trang chủ)
    // =========================================================================
    @GetMapping({"/", "/trang-chu"})
    public String home(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "weekend", required = false) Boolean weekend,
            @RequestParam(name = "tier", required = false) String tier,
            @RequestParam(name = "usedNew", required = false, defaultValue = "false") boolean usedNew,
            Model model
    ) {
        // If search keyword is submitted directly from home search bar, forward/redirect to products page
        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/san-pham?keyword=" + keyword.trim();
        }

        // 1. Famous Brands & Featured Brand Products (4 products for asymmetrical Bento Grid)
        List<String> brands = productService.getTopBrands();
        List<ProductCardDTO> featuredBrandProducts = productService.getFeaturedBrandProducts(4);
        if (featuredBrandProducts.size() < 4) {
            // Ensure at least 4 products by supplementing from hot products if needed
            List<ProductCardDTO> supplementary = productService.getHotProducts(4);
            for (ProductCardDTO p : supplementary) {
                if (featuredBrandProducts.size() >= 4) break;
                if (featuredBrandProducts.stream().noneMatch(existing -> existing.getId().equals(p.getId()))) {
                    featuredBrandProducts.add(p);
                }
            }
        }

        // 2. Banner slider images: 1-2-3-4-5-6-7
        List<String> banners = List.of(
                "/IMG/banner01.png",
                "/IMG/banner02.png",
                "/IMG/banner03.png",
                "/IMG/banner04.png",
                "/IMG/banner05.png",
                "/IMG/banner06.png",
                "/IMG/banner07.png"
        );

        // 3. Hot Deals & New Arrivals (Triệt tiêu N+1)
        List<ProductCardDTO> hotProducts = productService.getHotProducts(6);
        List<ProductCardDTO> newProducts = productService.getNewProducts(6);
        List<CategoryResponseDTO> categories = categoryService.getCategories(null, PageRequest.of(0, 20)).getContent();

        // 4. Offices, News, Hot Vouchers, and Statistics
        var offices = homeContentService.getOffices();
        var newsList = homeContentService.getFeaturedNews(3);
        DayOfWeek dow = LocalDate.now().getDayOfWeek();
        boolean isWeekend = (weekend != null) ? weekend : (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);
        var hotVouchers = homeContentService.getActiveVouchers(isWeekend, tier, usedNew);
        long totalProducts = homeContentService.getTotalActiveProducts();
        long productCategoryCount = homeContentService.getTotalCategories();

        model.addAttribute("brands", brands);
        model.addAttribute("featuredBrandProducts", featuredBrandProducts);
        model.addAttribute("banners", banners);
        model.addAttribute("categories", categories);
        model.addAttribute("hotProducts", hotProducts);
        model.addAttribute("newProducts", newProducts);
        model.addAttribute("offices", offices);
        model.addAttribute("newsList", newsList);
        model.addAttribute("hotVouchers", hotVouchers);
        model.addAttribute("isWeekend", isWeekend);
        model.addAttribute("customerTier", tier != null ? tier : "NEW");
        model.addAttribute("usedNewCustomerVoucher", usedNew);
        model.addAttribute("totalProducts", totalProducts);
        model.addAttribute("productCategoryCount", productCategoryCount);
        model.addAttribute("activeMenu", "home");

        return "customer/home";
    }

    // =========================================================================
    // 2. LUỒNG ADMIN (Chuyển hướng vào trang quản lý đơn hàng mặc định)
    // =========================================================================
    @GetMapping({"/admin"}) // ĐÃ XÓA đường dẫn "/" ở đây để tránh conflict
    public String rootRedirect() {
        return "redirect:/admin/orders";
    }
}