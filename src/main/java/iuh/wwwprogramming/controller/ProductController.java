package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.dto.ProductResponseDTO;
import iuh.wwwprogramming.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public String home() {
        return "redirect:/products";
    }

    @GetMapping("/products")
    public String listProducts(Model model) {
        // Tự động khởi tạo dữ liệu mẫu nếu chưa có sản phẩm
        productService.initSampleProductsIfEmpty();

        List<ProductResponseDTO> products = productService.getActiveProducts();
        model.addAttribute("products", products);
        model.addAttribute("addToCartRequest", new AddToCartRequestDTO());
        return "customer/product-list";
    }
}
