package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.dto.ProductCardDTO;
import iuh.wwwprogramming.dto.ProductDetailDTO;
import iuh.wwwprogramming.service.CategoryService;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping({"/san-pham", "/products"})
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "category", required = false, defaultValue = "all") String category,
            @RequestParam(name = "sort", required = false, defaultValue = "popular") String sort,
            @RequestParam(name = "page", required = false, defaultValue = "0") int page,
            @RequestParam(name = "size", required = false, defaultValue = "12") int size,
            Model model
    ) {
        int pageNumber = Math.max(0, page);
        int pageSize = (size > 0) ? size : 12;

        Page<ProductCardDTO> productPage = productService.searchProducts(
                keyword,
                category,
                sort,
                PageRequest.of(pageNumber, pageSize)
        );

        List<CategoryResponseDTO> categories = categoryService.getCategories(null, PageRequest.of(0, 50)).getContent();

        long totalElements = productPage.getTotalElements();
        long startItem = (totalElements == 0) ? 0 : (long) productPage.getNumber() * pageSize + 1;
        long endItem = Math.min((long) (productPage.getNumber() + 1) * pageSize, totalElements);

        model.addAttribute("productPage", productPage);
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("selectedCategory", category);
        model.addAttribute("sortBy", sort);
        model.addAttribute("currentPage", productPage.getNumber());
        model.addAttribute("totalPages", productPage.getTotalPages());
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("startItem", startItem);
        model.addAttribute("endItem", endItem);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("activeMenu", "products");

        return "customer/product-list";
    }

    @GetMapping("/{id}")
    public String productDetail(
            @PathVariable("id") String id,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        try {
            ProductDetailDTO product = productService.getProductDetail(id);
            List<ProductCardDTO> relatedProducts = productService.getRelatedProducts(
                    product.getCategoryId(),
                    product.getId(),
                    4
            );

            model.addAttribute("product", product);
            model.addAttribute("relatedProducts", relatedProducts);
            model.addAttribute("activeMenu", "products");

            return "customer/product-detail";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/san-pham";
        }
    }
}
