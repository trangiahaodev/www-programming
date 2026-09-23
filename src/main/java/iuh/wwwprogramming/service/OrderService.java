package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.CheckoutRequestDTO;
import iuh.wwwprogramming.dto.OrderResponseDTO;
import jakarta.servlet.http.HttpSession;

public interface OrderService {

    OrderResponseDTO placeOrder(HttpSession session, String userEmail, CheckoutRequestDTO request);

    OrderResponseDTO getOrderByCode(String orderCode);
}
