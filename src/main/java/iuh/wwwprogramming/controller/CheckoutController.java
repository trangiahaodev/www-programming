package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.CheckoutRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.OrderService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;

@Controller
@RequestMapping("/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CartService cartService;
    private final OrderService orderService;
    private final UserRepository userRepository;

    @GetMapping
    public String showCheckoutForm(Principal principal, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        CartDTO cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng đang trống! Vui lòng chọn sản phẩm trước khi thanh toán.");
            return "redirect:/cart";
        }

        CheckoutRequestDTO checkoutRequest = new CheckoutRequestDTO();

        // Nạp sẵn thông tin tài khoản nếu người dùng đã có thông tin cá nhân
        if (principal != null) {
            userRepository.findByEmail(principal.getName()).ifPresent(user -> {
                checkoutRequest.setRecipientName(user.getFullName());
                checkoutRequest.setRecipientPhone(user.getPhone() != null ? user.getPhone() : "");
                checkoutRequest.setShippingAddress(user.getAddress() != null ? user.getAddress() : "");
            });
        }

        model.addAttribute("cart", cart);
        model.addAttribute("checkoutRequest", checkoutRequest);
        return "checkout/checkout-form";
    }

    @PostMapping
    public String processCheckout(@Valid @ModelAttribute("checkoutRequest") CheckoutRequestDTO checkoutRequest,
                                  BindingResult bindingResult,
                                  Principal principal,
                                  HttpSession session,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {
        CartDTO cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng của bạn đang trống!");
            return "redirect:/cart";
        }

        // 1. Kiểm tra lỗi Validate JSR-380
        if (bindingResult.hasErrors()) {
            model.addAttribute("cart", cart);
            return "checkout/checkout-form";
        }

        String userEmail = principal != null ? principal.getName() : "khachhang@pinkycloud.com";

        try {
            // 2. Thực thi đặt hàng và trừ kho tại Service
            OrderResponseDTO placedOrder = orderService.placeOrder(session, userEmail, checkoutRequest);

            // 3. Chuyển hướng sang trang thông báo thành công (PRG pattern)
            redirectAttributes.addFlashAttribute("orderCode", placedOrder.getOrderCode());
            redirectAttributes.addFlashAttribute("order", placedOrder);
            redirectAttributes.addFlashAttribute("successMessage", "Đặt hàng thành công! Cảm ơn quý khách.");
            return "redirect:/checkout/success";

        } catch (IllegalArgumentException | IllegalStateException e) {
            model.addAttribute("cart", cart);
            model.addAttribute("errorMessage", e.getMessage());
            return "checkout/checkout-form";
        }
    }

    @GetMapping("/success")
    public String orderSuccess(@RequestParam(value = "orderCode", required = false) String paramCode,
                               Model model) {
        if (!model.containsAttribute("order")) {
            if (paramCode != null && !paramCode.isBlank()) {
                try {
                    OrderResponseDTO order = orderService.getOrderByCode(paramCode);
                    model.addAttribute("order", order);
                } catch (IllegalArgumentException e) {
                    model.addAttribute("errorMessage", "Không tìm thấy thông tin đơn hàng!");
                }
            }
        }
        return "checkout/order-success";
    }
}
