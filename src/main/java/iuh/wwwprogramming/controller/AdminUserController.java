package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller @RequiredArgsConstructor @RequestMapping("/admin/users")
public class AdminUserController {
    private final UserService users;
    @GetMapping
    public String list(@Valid @ModelAttribute("query") UserSearchDTO query, BindingResult errors, Model model) {
        if (errors.hasErrors()) {
            model.addAttribute("errorMessage", "Bộ lọc không hợp lệ. Vui lòng nhập từ khóa tối đa 100 ký tự và số dòng từ 1 đến 100.");
            model.addAttribute("users", users.search(new UserSearchDTO()));
        } else {
            model.addAttribute("users", users.search(query));
        }
        return "admin/user-list";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable String id, Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {
        try {
            model.addAttribute("userForm", users.getForEdit(id));
            model.addAttribute("userId", id);
            return "admin/user-edit";
        } catch (iuh.wwwprogramming.exception.UserNotFoundException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        }
    }
    @PostMapping("/{id}/edit")
    public String update(@PathVariable String id, @Valid @ModelAttribute("userForm") UserUpdateDTO dto,
            BindingResult errors, @org.springframework.security.core.annotation.AuthenticationPrincipal
            iuh.wwwprogramming.security.ShopPrincipal actor, Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes flash) {
        model.addAttribute("userId", id);
        if (errors.hasErrors()) return "admin/user-edit";
        try {
            users.update(id, dto, actor.id());
            flash.addFlashAttribute("successMessage", "Đã cập nhật người dùng.");
            return "redirect:/admin/users";
        } catch (iuh.wwwprogramming.exception.UserNotFoundException ex) {
            flash.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/users";
        } catch (IllegalArgumentException ex) {
            errors.reject("user.update", ex.getMessage());
        } catch (org.springframework.dao.DataIntegrityViolationException ex) {
            errors.reject("user.conflict", "Thông tin đang trùng với tài khoản khác. Vui lòng kiểm tra email.");
        }
        return "admin/user-edit";
    }
}
