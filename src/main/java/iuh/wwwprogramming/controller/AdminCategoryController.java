package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.CategoryResponseDTO;
import iuh.wwwprogramming.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

        // Giới hạn giá trị page và size hợp lệ
        int pageNumber = Math.max(0, page);
        int pageSize = (size <= 0 || size > 100) ? 10 : size;

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CategoryResponseDTO> categoryPage = categoryService.getCategories(keyword, pageable);

        model.addAttribute("categories", categoryPage.getContent());
        model.addAttribute("currentPage", categoryPage.getNumber());
        model.addAttribute("totalPages", categoryPage.getTotalPages());
        model.addAttribute("totalElements", categoryPage.getTotalElements());
        model.addAttribute("size", pageSize);
        model.addAttribute("keyword", keyword != null ? keyword.trim() : "");

        return "admin/category-list";
    }
}
