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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 20, 50);
    private static final Sort DEFAULT_SORT = Sort.by(
            Sort.Order.desc("createdAt"),
            Sort.Order.desc("id")
    );

    private final OrderService orderService;

    @GetMapping
    public String listOrders(
            @Valid @ModelAttribute("filter") OrderFilterDTO filterDTO,
            BindingResult bindingResult,
            Model model) {

        int pageNumber = Math.max(0, filterDTO.getPage());
        int pageSize = ALLOWED_PAGE_SIZES.contains(filterDTO.getSize()) ? filterDTO.getSize() : 10;
        filterDTO.setPage(pageNumber);
        filterDTO.setSize(pageSize);

        Pageable pageable = PageRequest.of(pageNumber, pageSize, DEFAULT_SORT);

        model.addAttribute("orderStatuses", Arrays.stream(OrderStatus.values()).toList());
        model.addAttribute("size", pageSize);

        if (!bindingResult.hasFieldErrors("fromDate")
                && !bindingResult.hasFieldErrors("toDate")
                && filterDTO.isDateRangeInvalid()) {
            bindingResult.reject("dateRange.invalid",
                    "Khoảng thời gian không hợp lệ: Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("orders", Collections.emptyList());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0L);
            return "admin/order-list";
        }

        try {
            Page<OrderResponseDTO> orderPage = orderService.getOrders(filterDTO, pageable);
            if (orderPage.getTotalPages() > 0 && pageNumber >= orderPage.getTotalPages()) {
                pageable = PageRequest.of(orderPage.getTotalPages() - 1, pageSize, DEFAULT_SORT);
                filterDTO.setPage(orderPage.getTotalPages() - 1);
                orderPage = orderService.getOrders(filterDTO, pageable);
            }
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
