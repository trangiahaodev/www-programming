package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.CategoryCreateDTO;
import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.dto.CategoryUpdateDTO;
import iuh.wwwprogramming.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public String listCategories(
            @RequestParam(name = "keyword", required = false) String keyword,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {

        int pageNumber = Math.max(0, page);
        int pageSize = (size <= 0 || size > 100) ? 10 : size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        Page<CategoryResponseDTO> categoryPage = categoryService.getCategories(keyword, pageable);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", categoryPage.getNumber());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("size", pageSize);
        model.addAttribute("keyword", keyword);

        return "admin/category-list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("categoryDTO")) {
            model.addAttribute("categoryDTO", new CategoryCreateDTO());
        }
        return "admin/category-create";
    }

    @PostMapping("/create")
    public String createCategory(
            @Valid @ModelAttribute("categoryDTO") CategoryCreateDTO categoryDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/category-create";
        }

        try {
            categoryService.createCategory(categoryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm mới danh mục mỹ phẩm thành công!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/category-create";
        }
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(
            @PathVariable("id") String id,
            Model model,
            RedirectAttributes redirectAttributes) {

        try {
            if (!model.containsAttribute("categoryDTO")) {
                CategoryUpdateDTO updateDTO = categoryService.getCategoryForUpdate(id);
                model.addAttribute("categoryDTO", updateDTO);
            }
            return "admin/category-update";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/categories";
        }
    }

    @PostMapping("/{id}/edit")
    public String updateCategory(
            @PathVariable("id") String id,
            @Valid @ModelAttribute("categoryDTO") CategoryUpdateDTO categoryDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            return "admin/category-update";
        }

        try {
            categoryService.updateCategory(id, categoryDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật danh mục mỹ phẩm thành công!");
            return "redirect:/admin/categories";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "admin/category-update";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(
            @PathVariable("id") String id,
            RedirectAttributes redirectAttributes) {

        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa danh mục mỹ phẩm thành công!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa danh mục vào lúc này. Vui lòng thử lại sau!");
        }

        return "redirect:/admin/categories";
    }
}
