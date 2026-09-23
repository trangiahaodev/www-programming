package iuh.wwwprogramming.service.impl;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.CartItemDTO;
import iuh.wwwprogramming.dto.UpdateCartItemRequestDTO;
import iuh.wwwprogramming.entity.Product;
import iuh.wwwprogramming.repository.ProductRepository;
import iuh.wwwprogramming.service.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CartServiceImpl implements CartService {

    private final ProductRepository productRepository;

    @Override
    public CartDTO getCart(HttpSession session) {
        CartDTO cart = (CartDTO) session.getAttribute(CART_SESSION_KEY);
        if (cart == null) {
            cart = new CartDTO();
            session.setAttribute(CART_SESSION_KEY, cart);
        }
        return cart;
    }

    @Override
    public void addToCart(HttpSession session, AddToCartRequestDTO request) {
        Product product = productRepository.findByIdAndActiveTrue(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!"));

        CartDTO cart = getCart(session);
        int currentQuantityInCart = 0;
        if (cart.getItems().containsKey(product.getId())) {
            currentQuantityInCart = cart.getItems().get(product.getId()).getQuantity();
        }

        int totalRequestedQuantity = currentQuantityInCart + request.getQuantity();
        if (totalRequestedQuantity > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Số lượng yêu cầu vượt quá tồn kho (chỉ còn lại " + product.getStockQuantity() + " sản phẩm)!"
            );
        }

        CartItemDTO item = CartItemDTO.builder()
                .productId(product.getId())
                .productCode(product.getProductCode())
                .productName(product.getName())
                .imageUrl(product.getImageUrl())
                .unitPrice(product.getPrice())
                .quantity(request.getQuantity())
                .stockQuantity(product.getStockQuantity())
                .build();

        cart.addItem(item);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void updateQuantity(HttpSession session, UpdateCartItemRequestDTO request) {
        CartDTO cart = getCart(session);
        if (!cart.getItems().containsKey(request.getProductId())) {
            throw new IllegalArgumentException("Sản phẩm không có trong giỏ hàng!");
        }

        Product product = productRepository.findByIdAndActiveTrue(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại hoặc đã ngừng kinh doanh!"));

        if (request.getQuantity() > product.getStockQuantity()) {
            throw new IllegalArgumentException(
                    "Số lượng cập nhật vượt quá tồn kho (chỉ còn lại " + product.getStockQuantity() + " sản phẩm)!"
            );
        }

        cart.updateQuantity(request.getProductId(), request.getQuantity());
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void removeItem(HttpSession session, String productId) {
        CartDTO cart = getCart(session);
        cart.removeItem(productId);
        session.setAttribute(CART_SESSION_KEY, cart);
    }

    @Override
    public void clearCart(HttpSession session) {
        CartDTO cart = getCart(session);
        cart.clear();
        session.setAttribute(CART_SESSION_KEY, cart);
    }
}
