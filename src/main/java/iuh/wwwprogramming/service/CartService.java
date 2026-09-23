package iuh.wwwprogramming.service;

import iuh.wwwprogramming.dto.AddToCartRequestDTO;
import iuh.wwwprogramming.dto.CartDTO;
import iuh.wwwprogramming.dto.UpdateCartItemRequestDTO;
import jakarta.servlet.http.HttpSession;

public interface CartService {

    String CART_SESSION_KEY = "SESSION_CART";

    CartDTO getCart(HttpSession session);

    void addToCart(HttpSession session, AddToCartRequestDTO request);

    void updateQuantity(HttpSession session, UpdateCartItemRequestDTO request);

    void removeItem(HttpSession session, String productId);

    void clearCart(HttpSession session);
}
