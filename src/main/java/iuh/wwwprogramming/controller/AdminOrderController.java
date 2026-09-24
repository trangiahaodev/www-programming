package iuh.wwwprogramming.controller;

import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderItemQuantityUpdateDTO;
import iuh.wwwprogramming.dto.OrderQuantityUpdateRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.entity.OrderStatus;
import iuh.wwwprogramming.exception.InvalidOrderItemException;
import iuh.wwwprogramming.exception.InvalidOrderStatusException;
import iuh.wwwprogramming.exception.OrderItemNotFoundException;
import iuh.wwwprogramming.exception.OrderNotFoundException;
import iuh.wwwprogramming.service.OrderService;
import iuh.wwwprogramming.service.impl.OrderServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private static final List<Integer> ALLOWED_PAGE_SIZES = List.of(10, 20, 50);
    private static final Pattern UUID_PATTERN = Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

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

        Pageable pageable = PageRequest.of(pageNumber, pageSize, OrderServiceImpl.DEFAULT_SORT);

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
        if (id == null || id.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã đơn hàng không được để trống.");
            return "redirect:/admin/orders";
        }

        String trimmedId = id.trim();
        if (!UUID_PATTERN.matcher(trimmedId).matches()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã đơn hàng không đúng định dạng.");
            return "redirect:/admin/orders";
        }

        try {
            OrderDetailResponseDTO order = orderService.getOrderDetailById(trimmedId);
            model.addAttribute("order", order);
            if (!model.containsAttribute("updateRequest")) {
                OrderQuantityUpdateRequestDTO updateRequest = OrderQuantityUpdateRequestDTO.builder()
                        .orderId(order.getId())
                        .items(order.getItems() != null ? order.getItems().stream()
                                .map(item -> OrderItemQuantityUpdateDTO.builder()
                                        .orderItemId(item.getId())
                                        .quantity(item.getQuantity())
                                        .build())
                                .collect(Collectors.toList()) : Collections.emptyList())
                        .build();
                model.addAttribute("updateRequest", updateRequest);
            }
            return "admin/order-detail";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/orders";
        }
    }

    @PostMapping(value = {"/{id}/items", "/{id}/update-quantity"})
    public String updateOrderQuantities(
            @PathVariable("id") String id,
            @Valid @ModelAttribute("updateRequest") OrderQuantityUpdateRequestDTO requestDTO,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (id == null || id.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã đơn hàng không được để trống.");
            return "redirect:/admin/orders";
        }

        String trimmedId = id.trim();
        if (!UUID_PATTERN.matcher(trimmedId).matches()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã đơn hàng không đúng định dạng UUID.");
            return "redirect:/admin/orders";
        }

        if (requestDTO.getOrderId() == null || requestDTO.getOrderId().trim().isEmpty()) {
            requestDTO.setOrderId(trimmedId);
        } else if (!requestDTO.getOrderId().trim().equalsIgnoreCase(trimmedId)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mã đơn hàng trong URL và dữ liệu không đồng nhất.");
            return "redirect:/admin/orders/" + trimmedId;
        }

        if (bindingResult.hasErrors()) {
            String validationMsg = bindingResult.getAllErrors().stream()
                    .map(org.springframework.context.support.DefaultMessageSourceResolvable::getDefaultMessage)
                    .filter(msg -> msg != null && !msg.isBlank())
                    .findFirst()
                    .orElse("Dữ liệu cập nhật không hợp lệ. Số lượng sản phẩm phải là số nguyên từ 1 đến 999.");
            redirectAttributes.addFlashAttribute("errorMessage", validationMsg);
            redirectAttributes.addFlashAttribute(BindingResult.MODEL_KEY_PREFIX + "updateRequest", bindingResult);
            redirectAttributes.addFlashAttribute("updateRequest", requestDTO);
            return "redirect:/admin/orders/" + trimmedId;
        }

        try {
            orderService.updateOrderQuantities(trimmedId, requestDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật số lượng sản phẩm và tính lại tổng tiền đơn hàng thành công.");
            return "redirect:/admin/orders/" + trimmedId;
        } catch (OrderNotFoundException e) {
            log.warn("Không tìm thấy đơn hàng {} để cập nhật số lượng: {}", trimmedId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/orders";
        } catch (InvalidOrderStatusException | OrderItemNotFoundException | InvalidOrderItemException | IllegalArgumentException | IllegalStateException e) {
            log.warn("Nghiệp vụ cập nhật đơn hàng bị từ chối {}: {}", trimmedId, e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/orders/" + trimmedId;
        } catch (Exception e) {
            log.error("Lỗi không mong muốn khi cập nhật số lượng đơn hàng {}: ", trimmedId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Không thể lưu cập nhật số lượng vào lúc này. Vui lòng thử lại sau.");
            return "redirect:/admin/orders/" + trimmedId;
        }
    }

    @PostMapping(value = {"/{id}/items", "/{id}/update-quantity"}, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> updateOrderQuantitiesJson(
            @PathVariable("id") String id,
            @Valid @RequestBody OrderQuantityUpdateRequestDTO requestDTO,
            BindingResult bindingResult) {

        if (id == null || id.trim().isEmpty() || !UUID_PATTERN.matcher(id.trim()).matches()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã đơn hàng không hợp lệ."));
        }
        String trimmedId = id.trim();

        if (requestDTO.getOrderId() == null || requestDTO.getOrderId().trim().isEmpty()) {
            requestDTO.setOrderId(trimmedId);
        } else if (!requestDTO.getOrderId().trim().equalsIgnoreCase(trimmedId)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Mã đơn hàng trong URL và dữ liệu không đồng nhất."));
        }

        if (bindingResult.hasErrors()) {
            String validationMsg = bindingResult.getAllErrors().stream()
                    .map(org.springframework.context.support.DefaultMessageSourceResolvable::getDefaultMessage)
                    .filter(msg -> msg != null && !msg.isBlank())
                    .findFirst()
                    .orElse("Dữ liệu cập nhật không hợp lệ. Số lượng sản phẩm phải là số nguyên từ 1 đến 999.");
            return ResponseEntity.badRequest().body(Map.of("message", validationMsg));
        }

        try {
            OrderDetailResponseDTO updatedOrder = orderService.updateOrderQuantities(trimmedId, requestDTO);
            return ResponseEntity.ok(updatedOrder);
        } catch (OrderNotFoundException e) {
            log.warn("Không tìm thấy đơn hàng {} khi gọi API cập nhật số lượng: {}", trimmedId, e.getMessage());
            return ResponseEntity.status(404).body(Map.of("message", e.getMessage()));
        } catch (InvalidOrderStatusException | OrderItemNotFoundException | InvalidOrderItemException | IllegalArgumentException | IllegalStateException e) {
            log.warn("Nghiệp vụ cập nhật đơn hàng qua JSON bị từ chối {}: {}", trimmedId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi cập nhật số lượng qua JSON {}: ", trimmedId, e);
            return ResponseEntity.internalServerError().body(Map.of("message", "Không thể lưu cập nhật số lượng vào lúc này. Vui lòng thử lại sau."));
        }
    }
}

