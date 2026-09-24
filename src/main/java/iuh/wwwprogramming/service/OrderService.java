package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.OrderDetailResponseDTO;
import iuh.wwwprogramming.dto.OrderFilterDTO;
import iuh.wwwprogramming.dto.OrderQuantityUpdateRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Page<OrderResponseDTO> getOrders(OrderFilterDTO filterDTO, Pageable pageable);
    OrderResponseDTO getOrderById(String id);
    OrderDetailResponseDTO getOrderDetailById(String id);
    OrderDetailResponseDTO updateOrderQuantities(String orderId, OrderQuantityUpdateRequestDTO requestDTO);
}
