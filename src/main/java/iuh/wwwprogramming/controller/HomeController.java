package iuh.wwwprogramming.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/admin"})
    public String rootRedirect() {
        return "redirect:/admin/orders";
    }
}
