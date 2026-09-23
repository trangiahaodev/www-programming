package iuh.wwwprogramming.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Builder.Default
    private Map<String, CartItemDTO> items = new LinkedHashMap<>();

    public Collection<CartItemDTO> getItemList() {
        return items.values();
    }

    public void addItem(CartItemDTO item) {
        if (item == null || item.getProductId() == null) {
            return;
        }
        if (items.containsKey(item.getProductId())) {
            CartItemDTO existing = items.get(item.getProductId());
            int newQuantity = existing.getQuantity() + item.getQuantity();
            existing.setQuantity(newQuantity);
            existing.setSubtotal(existing.getSubtotal());
        } else {
            item.setSubtotal(item.getSubtotal());
            items.put(item.getProductId(), item);
        }
    }

    public void updateQuantity(String productId, int quantity) {
        if (productId == null || !items.containsKey(productId)) {
            return;
        }
        if (quantity <= 0) {
            items.remove(productId);
        } else {
            CartItemDTO item = items.get(productId);
            item.setQuantity(quantity);
            item.setSubtotal(item.getSubtotal());
        }
    }

    public void removeItem(String productId) {
        if (productId != null) {
            items.remove(productId);
        }
    }

    public void clear() {
        items.clear();
    }

    public int getTotalQuantity() {
        return items.values().stream()
                .mapToInt(CartItemDTO::getQuantity)
                .sum();
    }

    public BigDecimal getTotalAmount() {
        return items.values().stream()
                .map(CartItemDTO::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }
}
