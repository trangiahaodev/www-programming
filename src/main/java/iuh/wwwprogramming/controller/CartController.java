package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.UpdateCartItemRequestDTO;
import iuh.wwwprogramming.service.CartService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        CartDTO cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        return "cart/cart-view";
    }

    @PostMapping("/add")
    public String addToCart(@Valid @ModelAttribute("addToCartRequest") AddToCartRequestDTO request,
                            BindingResult bindingResult,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ!"
            );
            return "redirect:/products";
        }

        try {
            cartService.addToCart(session, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateQuantity(@Valid @ModelAttribute("updateRequest") UpdateCartItemRequestDTO request,
                                 BindingResult bindingResult,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "Số lượng không hợp lệ!"
            );
            return "redirect:/cart";
        }

        try {
            cartService.updateQuantity(session, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật số lượng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeItem(@RequestParam("productId") String productId,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        try {
            cartService.removeItem(session, productId);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa sản phẩm khỏi giỏ hàng!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể xóa sản phẩm lúc này!");
        }
        return "redirect:/cart";
    }

    @PostMapping("/clear")
    public String clearCart(HttpSession session, RedirectAttributes redirectAttributes) {
        cartService.clearCart(session);
        redirectAttributes.addFlashAttribute("successMessage", "Đã làm trống giỏ hàng!");
        return "redirect:/cart";
    }
}
