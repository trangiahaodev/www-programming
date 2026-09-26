package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.UserRegisterDTO;
import iuh.wwwprogramming.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller @RequiredArgsConstructor
public class RegistrationController {
    private final RegistrationService registration;
    @GetMapping("/register")
    public String form(Model model) {
        model.addAttribute("registration", new UserRegisterDTO());
        return "guest/register";
    }
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registration") UserRegisterDTO dto, BindingResult errors,
            RedirectAttributes flash) {
        if (!errors.hasErrors()) {
            try {
                registration.register(dto);
                flash.addFlashAttribute("successMessage", "Đăng ký thành công. Bạn có thể đăng nhập ngay.");
                return "redirect:/login";
            } catch (IllegalArgumentException ex) {
                errors.rejectValue("email", "duplicate", ex.getMessage());
            } catch (DataIntegrityViolationException ex) {
                errors.reject("registration.conflict", "Không thể tạo tài khoản với thông tin này. Vui lòng kiểm tra email và thử lại.");
            }
        }
        dto.setPassword(null); dto.setConfirmPassword(null);
        return "guest/register";
    }
}
