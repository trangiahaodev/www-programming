package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.ProductCreateDTO;
import iuh.wwwprogramming.dto.ProductResponseDTO;
import iuh.wwwprogramming.dto.ProductUpdateDTO;
import iuh.wwwprogramming.service.CategoryService;
import iuh.wwwprogramming.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping
    public String listProducts(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "categoryId", required = false) String categoryId,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        int pageIndex = Math.max(0, page);
        int pageSize = (size == 10 || size == 20 || size == 50) ? size : 10;

        Pageable pageable = PageRequest.of(pageIndex, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductResponseDTO> productPage = productService.getProducts(keyword, categoryId, pageable);

        model.addAttribute("products", productPage);
        model.addAttribute("categories", categoryService.getActiveCategories());
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");
        model.addAttribute("categoryId", categoryId != null ? categoryId.trim() : "");
        model.addAttribute("currentPage", pageIndex);
        model.addAttribute("pageSize", pageSize);

        return "admin/product-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("productDTO")) {
            model.addAttribute("productDTO", new ProductCreateDTO());
        }
        model.addAttribute("categories", categoryService.getActiveCategories());
        return "admin/product-create";
    }

    @PostMapping("/create")
    public String createProduct(
            @Valid @ModelAttribute("productDTO") ProductCreateDTO productDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "admin/product-create";
        }

        try {
            productService.createProduct(productDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới sản phẩm mỹ phẩm thành công!");
            return "redirect:/admin/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/product-create";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable("id") String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            if (!model.containsAttribute("productDTO")) {
                ProductUpdateDTO productDTO = productService.getProductForEdit(id);
                model.addAttribute("productDTO", productDTO);
            }
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "admin/product-edit";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/products";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateProduct(
            @PathVariable("id") String id,
            @Valid @ModelAttribute("productDTO") ProductUpdateDTO productDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            return "admin/product-edit";
        }

        try {
            productService.updateProduct(id, productDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật sản phẩm mỹ phẩm thành công!");
            return "redirect:/admin/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("categories", categoryService.getActiveCategories());
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/product-edit";
        }
    }

    @PostMapping("/delete/{id}")
    public String deleteProduct(
            @PathVariable("id") String id,
            RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa sản phẩm mỹ phẩm thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sản phẩm vào lúc này. Vui lòng thử lại sau!");
        }
        return "redirect:/admin/products";
    }
}
