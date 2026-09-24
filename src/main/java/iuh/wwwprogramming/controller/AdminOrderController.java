package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public String listOrders(
            @Valid @ModelAttribute("filter") OrderFilterDTO filterDTO,
            BindingResult bindingResult,
            Model model) {

        int pageNumber = Math.max(0, filterDTO.getPage());
        int pageSize = (filterDTO.getSize() <= 0 || filterDTO.getSize() > 100) ? 10 : filterDTO.getSize();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));

        model.addAttribute("orderStatuses", OrderStatus.values());
        model.addAttribute("size", pageSize);

        if (bindingResult.hasErrors()) {
            model.addAttribute("orders", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
            return "admin/order-list";
        }

        try {
            Page<OrderResponseDTO> orderPage = orderService.getOrders(filterDTO, pageable);
            model.addAttribute("orders", orderPage.getContent());
            model.addAttribute("currentPage", orderPage.getNumber());
            model.addAttribute("totalPages", orderPage.getTotalPages());
            model.addAttribute("totalElements", orderPage.getTotalElements());
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("orders", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
        }

        return "admin/order-list";
    }

    @GetMapping("/{id}")
    public String viewOrderDetail(@PathVariable("id") String id, RedirectAttributes redirectAttributes, Model model) {
        try {
            OrderResponseDTO order = orderService.getOrderById(id);
            model.addAttribute("order", order);
            return "admin/order-detail";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/orders";
        }
    }
}
