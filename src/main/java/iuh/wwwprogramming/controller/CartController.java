package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.UpdateCartItemRequestDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.TrackingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Slf4j
@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final TrackingService trackingService;
    private final UserRepository userRepository;

    @GetMapping
    public String viewCart(HttpSession session, Model model) {
        CartDTO cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        return "cart/cart-view";
    }

    @PostMapping("/add")
    public String addToCart(@Valid @ModelAttribute("addToCartRequest") AddToCartRequestDTO request,
                            BindingResult bindingResult,
                            @RequestParam(value = "action", required = false) String action,
                            @RequestParam(value = "redirect", required = false) String redirect,
                            HttpServletRequest servletRequest,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        String referer = servletRequest.getHeader("Referer");
        String returnUrl = (referer != null && !referer.isBlank()) ? referer : "/products";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    bindingResult.getFieldError() != null ? bindingResult.getFieldError().getDefaultMessage() : "Dữ liệu không hợp lệ!"
            );
            return "redirect:" + returnUrl;
        }

        try {
            cartService.addToCart(session, request);
            redirectAttributes.addFlashAttribute("successMessage", "Đã thêm sản phẩm vào giỏ hàng thành công!");

            // HOOK NGẦM (Phân công Member DƯƠNG):
            // Mỗi khi khách bấm "Thêm vào giỏ" (/cart/add) hoặc bấm "Mua ngay",
            // hệ thống tự động lưu lại sự kiện ADD_TO_CART vào bảng log hành vi.
            try {
                User currentUser = null;
                if (servletRequest.getUserPrincipal() != null) {
                    currentUser = userRepository.findByEmail(servletRequest.getUserPrincipal().getName()).orElse(null);
                }
                boolean isBuyNow = "buyNow".equalsIgnoreCase(action) || "checkout".equalsIgnoreCase(redirect);
                int quantity = (request.getQuantity() != null && request.getQuantity() > 0) ? request.getQuantity() : 1;
                trackingService.recordCartEvent(session.getId(), request.getProductId(), quantity, isBuyNow, currentUser);
            } catch (Exception trackingEx) {
                log.warn("Không thể ghi log hành vi giỏ hàng ngầm: {}", trackingEx.getMessage());
            }

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:" + returnUrl;
        }

        // Nếu bấm "Mua Ngay" -> chuyển hướng thẳng tới checkout
        if ("buyNow".equalsIgnoreCase(action) || "checkout".equalsIgnoreCase(redirect)) {
            return "redirect:/checkout";
        }

        if ("cart".equalsIgnoreCase(redirect) || "cart".equalsIgnoreCase(action)) {
            return "redirect:/cart";
        }

        // Mặc định: Giữ người dùng ở lại trang hiện tại (nếu có referer hợp lệ không phải giỏ)
        if (referer != null && !referer.isBlank() && !referer.contains("/cart")) {
            return "redirect:" + referer;
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
