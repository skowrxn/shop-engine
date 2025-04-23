package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.CartContentResponse;
import pl.skowrxn.springecommerce.dto.CartItemDTO;

public interface CartService {

    CartItemDTO addToCart(Long productId, Integer quantity);

    void removeFromCart(Long cartItemId);

    void clearCart();

    Double getTotalPrice();

    Integer getTotalQuantity();

    CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity);

    CartContentResponse getCartContent();
}
