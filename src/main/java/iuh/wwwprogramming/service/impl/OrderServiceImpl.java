package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.*;
import iuh.wwwprogramming.entity.Order;
import iuh.wwwprogramming.entity.OrderDetail;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.entity.User;
import iuh.wwwprogramming.repository.OrderRepository;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.repository.UserRepository;
import iuh.wwwprogramming.service.CartService;
import iuh.wwwprogramming.service.OrderService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public OrderResponseDTO placeOrder(HttpSession session, String userEmail, CheckoutRequestDTO request) {
        CartDTO cart = cartService.getCart(session);
        if (cart == null || cart.isEmpty()) {
            throw new IllegalStateException("Giỏ hàng của bạn đang trống! Vui lòng chọn sản phẩm trước khi đặt hàng.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin tài khoản người dùng: " + userEmail));

        // 1. Kiểm tra tồn kho của từng sản phẩm trong giỏ hàng
        List<OrderDetail> orderDetails = new ArrayList<>();
        BigDecimal computedTotal = BigDecimal.ZERO;

        for (CartItemDTO item : cart.getItems().values()) {
            Product product = productRepository.findByIdAndActiveTrue(item.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Sản phẩm '" + item.getProductName() + "' không còn khả dụng!"));

            if (item.getQuantity() > product.getStockQuantity()) {
                throw new IllegalArgumentException(
                        "Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho (chỉ còn lại " + product.getStockQuantity() + " sản phẩm)!"
                );
            }

            // 2. Trừ tồn kho sản phẩm
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);

            BigDecimal lineSubtotal = product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            computedTotal = computedTotal.add(lineSubtotal);

            OrderDetail detail = OrderDetail.builder()
                    .product(product)
                    .unitPrice(product.getPrice())
                    .quantity(item.getQuantity())
                    .subtotal(lineSubtotal)
                    .build();

            orderDetails.add(detail);
        }

        // 3. Sinh Business Key: orderCode duy nhất (VD: DH26001234)
        String orderCode = generateUniqueOrderCode();

        // 4. Tạo thực thể Order
        Order order = Order.builder()
                .orderCode(orderCode)
                .user(user)
                .recipientName(request.getRecipientName().trim())
                .recipientPhone(request.getRecipientPhone().trim())
                .shippingAddress(request.getShippingAddress().trim())
                .note(request.getNote() != null ? request.getNote().trim() : null)
                .paymentMethod(request.getPaymentMethod())
                .totalAmount(computedTotal)
                .status("PENDING")
                .build();

        // Gắn quan hệ 2 chiều giữa Order và OrderDetail
        for (OrderDetail detail : orderDetails) {
            detail.setOrder(order);
        }
        order.setOrderDetails(orderDetails);

        // 5. Lưu đơn hàng vào Database
        Order savedOrder = orderRepository.save(order);

        // 6. Dọn sạch giỏ hàng trong Session
        cartService.clearCart(session);

        // 7. Map sang DTO trả về View
        return mapToResponseDTO(savedOrder);
    }

    @Override
    public OrderResponseDTO getOrderByCode(String orderCode) {
        Order order = orderRepository.findWithDetailsByOrderCode(orderCode)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy đơn hàng với mã: " + orderCode));
        return mapToResponseDTO(order);
    }

    private String generateUniqueOrderCode() {
        int currentYear = Year.now().getValue() % 100;
        Random random = new Random();
        String candidateCode;
        do {
            int seq = 100000 + random.nextInt(900000);
            candidateCode = String.format("DH%02d%d", currentYear, seq);
        } while (orderRepository.existsByOrderCode(candidateCode));
        return candidateCode;
    }

    private OrderResponseDTO mapToResponseDTO(Order order) {
        List<OrderDetailResponseDTO> items = order.getOrderDetails().stream()
                .map(d -> OrderDetailResponseDTO.builder()
                        .id(d.getId())
                        .productId(d.getProduct() != null ? d.getProduct().getId() : null)
                        .productCode(d.getProduct() != null ? d.getProduct().getProductCode() : "")
                        .productName(d.getProduct() != null ? d.getProduct().getName() : "")
                        .productImageUrl(d.getProduct() != null ? d.getProduct().getImageUrl() : "")
                        .unitPrice(d.getUnitPrice())
                        .quantity(d.getQuantity())
                        .subtotal(d.getSubtotal())
                        .build())
                .collect(Collectors.toList());

        return OrderResponseDTO.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .shippingAddress(order.getShippingAddress())
                .note(order.getNote())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}
