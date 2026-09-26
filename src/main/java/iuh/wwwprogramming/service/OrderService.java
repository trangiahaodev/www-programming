package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.CheckoutRequestDTO;
import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderQuantityUpdateRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {

    // =========================================================================
    // 1. NHÓM HÀM CHO ADMIN (Quản lý, thống kê và cập nhật đơn hàng)
    // =========================================================================

    Page<OrderResponseDTO> getOrders(OrderFilterDTO filterDTO, Pageable pageable);

    OrderResponseDTO getOrderById(String id);

    OrderDetailResponseDTO getOrderDetailById(String id);

    OrderDetailResponseDTO updateOrderQuantities(String orderId, OrderQuantityUpdateRequestDTO requestDTO);


    // =========================================================================
    // 2. NHÓM HÀM CHO CUSTOMER (Tiến hành Checkout và tra cứu đơn)
    // =========================================================================

    OrderResponseDTO placeOrder(HttpSession session, String userEmail, CheckoutRequestDTO request);

    OrderResponseDTO getOrderByCode(String orderCode);
}