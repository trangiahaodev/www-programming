package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.CheckoutRequestDTO;
import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderQuantityUpdateRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import iuh.wwwprogramming.dto.TopProductStatDTO;
import iuh.wwwprogramming.dto.OrderAnalyticsDTO;
import jakarta.servlet.http.HttpSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.time.LocalDate;

public interface OrderService {

    // =========================================================================
    // 1. NHÓM HÀM CHO ADMIN (Quản lý, thống kê và cập nhật đơn hàng)
    // =========================================================================

    Page<OrderResponseDTO> getOrders(OrderFilterDTO filterDTO, Pageable pageable);

    List<TopProductStatDTO> getTopOrderedProducts(int limit);

    List<OrderAnalyticsDTO> getOrderAnalytics(LocalDate fromDate, LocalDate toDate);

    List<OrderAnalyticsDTO> getOrderAnalytics(LocalDate fromDate, LocalDate toDate, int limit);

    OrderResponseDTO getOrderById(String id);

    OrderDetailResponseDTO getOrderDetailById(String id);

    OrderDetailResponseDTO updateOrderQuantities(String orderId, OrderQuantityUpdateRequestDTO requestDTO);

    OrderDetailResponseDTO cancelOrder(String orderId);


    // =========================================================================
    // 2. NHÓM HÀM CHO CUSTOMER (Tiến hành Checkout và tra cứu đơn)
    // =========================================================================

    OrderResponseDTO placeOrder(HttpSession session, String userEmail, CheckoutRequestDTO request);

    OrderResponseDTO getOrderByCode(String orderCode);
}
