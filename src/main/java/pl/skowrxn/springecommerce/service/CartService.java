package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.response.CartContentResponse;
import pl.skowrxn.springecommerce.dto.CartItemDTO;
import pl.skowrxn.springecommerce.entity.CartItem;

public interface CartService {

    CartItemDTO addToCart(Long productId, Integer quantity);

    void removeFromCart(Long cartItemId);

    void removeFromCart(CartItem cartItem);

    void clearCart();

    Double getTotalPrice();

    Integer getTotalQuantity();

    CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity);

    CartContentResponse getCartContent();
}
