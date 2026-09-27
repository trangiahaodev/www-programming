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
}
