package pl.skowrxn.springecommerce.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.skowrxn.springecommerce.dto.response.CartContentResponse;
import pl.skowrxn.springecommerce.dto.CartItemDTO;
import pl.skowrxn.springecommerce.service.CartService;

@RestController
public class CartController {

     private final CartService cartService;

     public CartController(CartService cartService) {
         this.cartService = cartService;
     }

     @PostMapping("/cart")
     public ResponseEntity<?> addToCart(@RequestParam Long productId, @RequestParam Integer quantity) {
         CartItemDTO cartItemDTO = cartService.addToCart(productId, quantity);
         return ResponseEntity.ok(cartItemDTO);
     }

     @DeleteMapping("/cart/{cartItemId}")
     public ResponseEntity<?> removeFromCart(@PathVariable Long cartItemId) {
         cartService.removeFromCart(cartItemId);
         return ResponseEntity.noContent().build();
     }

     @PutMapping("/cart/{cartItemId}")
     public ResponseEntity<CartItemDTO> updateCartItemQuantity(@PathVariable Long cartItemId, @RequestParam Integer quantity) {
         CartItemDTO cartItemDTO = cartService.updateCartItemQuantity(cartItemId, quantity);
         return ResponseEntity.ok(cartItemDTO);
     }

     @GetMapping("/cart/content")
     public ResponseEntity<CartContentResponse> getCartContent() {
         return ResponseEntity.ok(cartService.getCartContent());
     }

     @PostMapping("/cart/clear")
     public ResponseEntity<?> clearCart() {
         this.cartService.clearCart();
         return ResponseEntity.noContent().build();
     }

}
