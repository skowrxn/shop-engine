package pl.skowrxn.springecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import pl.skowrxn.springecommerce.dto.*;
import pl.skowrxn.springecommerce.entity.*;
import pl.skowrxn.springecommerce.exception.ProductOutOfStockException;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.CartItemRepository;
import pl.skowrxn.springecommerce.repository.CartRepository;
import pl.skowrxn.springecommerce.repository.ProductRepository;
import pl.skowrxn.springecommerce.security.service.UserDetailsImpl;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final AuthUtil authUtil;
    private final ModelMapper modelMapper;

    public CartServiceImpl(CartRepository cartRepository,
                           CartItemRepository cartItemRepository, ProductRepository productRepository,
                           AuthUtil authUtil, ModelMapper modelMapper) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.authUtil = authUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public CartItemDTO addToCart(Long productId, Integer quantity) {
        Product product = this.productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product", "id", productId));
        if (product.getStockQuantity() < quantity) {
            throw new ProductOutOfStockException(product, product.getStockQuantity(), quantity);
        }

        User user = this.authUtil.getLoggedInUser();
        Cart cart = user.getCart();

        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            user.setCart(cart);
            cart = this.cartRepository.save(cart);
        }

        CartItem existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        CartItem updatedCartItem;

        if (existingCartItem != null) {
            int newQuantity = existingCartItem.getQuantity() + quantity;
            if (product.getStockQuantity() < newQuantity) {
                throw new ProductOutOfStockException(product, product.getStockQuantity(), newQuantity);
            }
            existingCartItem.setQuantity(newQuantity);
            existingCartItem.setTotalPrice(existingCartItem.getSinglePrice() * newQuantity);
            updatedCartItem = this.cartItemRepository.save(existingCartItem);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setSinglePrice(product.getPrice());
            cartItem.setTotalPrice(product.getPrice() * quantity);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            updatedCartItem = this.cartItemRepository.save(cartItem);
            cart.getItems().add(cartItem);
        }

        double newTotal = cart.getItems().stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
        cart.setTotalPrice(newTotal);

        this.cartRepository.save(cart);
        CartItemDTO dto = this.modelMapper.map(updatedCartItem, CartItemDTO.class);
        dto.setProduct(this.modelMapper.map(updatedCartItem.getProduct(), ProductDTO.class));
        return dto;
    }

    @Override
    public void removeFromCart(Long cartItemId) {
        CartItem cartItem = this.cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));
        Cart cart = cartItem.getCart();
        cart.setTotalPrice(cart.getTotalPrice() - cartItem.getTotalPrice());
        cart.getItems().remove(cartItem);
        this.cartItemRepository.delete(cartItem);
        this.cartRepository.save(cart);
    }

    @Override
    public void clearCart() {
        Cart cart = this.authUtil.getUserCart();

        if(cart != null && !cart.getItems().isEmpty()) {
            this.cartItemRepository.deleteAll(cart.getItems());
            cart.setTotalPrice(0.0);
            cart.getItems().clear();
            this.cartRepository.save(cart);
        }
    }

    @Override
    public Double getTotalPrice() {
        Cart cart = this.authUtil.getUserCart();
        if(cart != null) {
            return cart.getTotalPrice();
        }
        return 0.0;
    }

    @Override
    public Integer getTotalQuantity() {
        Cart cart = this.authUtil.getUserCart();
        if(cart != null) {
            return cart.getItems().size();
        }
        return 0;
    }

    @Override
    public CartItemDTO updateCartItemQuantity(Long cartItemId, Integer quantity) {
        CartItem cartItem = this.cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new ResourceNotFoundException("CartItem", "id", cartItemId));

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        if (quantity == 0) {
            this.removeFromCart(cartItemId);
            return null;
        }

        cartItem.setQuantity(quantity);
        cartItem.setTotalPrice(cartItem.getSinglePrice() * quantity);

        CartItem savedCartitem = this.cartItemRepository.save(cartItem);

        Cart cart = cartItem.getCart();
        if (cart != null) {
            double price = cart.getItems().stream().mapToDouble(CartItem::getTotalPrice).sum();
            cart.setTotalPrice(price);
            this.cartRepository.save(cart);
        }
        CartItemDTO updatedDTO = this.modelMapper.map(savedCartitem, CartItemDTO.class);
        updatedDTO.setProduct(this.modelMapper.map(savedCartitem.getProduct(), ProductDTO.class));
        return updatedDTO;
    }

    @Override
    public CartContentResponse getCartContent() {
        Cart cart = this.authUtil.getUserCart();

        List<CartItemDTO> cartItemDTOs = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            CartItemDTO cartItemDTO = this.modelMapper.map(cartItem, CartItemDTO.class);
            cartItemDTO.setCartId(cartItem.getCart().getId());
            cartItemDTO.setProduct(this.modelMapper.map(cartItem.getProduct(), ProductDTO.class));
            cartItemDTOs.add(cartItemDTO);
        }

        CartContentResponse cartContentResponse = new CartContentResponse();
        cartContentResponse.setId(cart.getId());
        cartContentResponse.setQuantity(cart.getItems().size());
        cartContentResponse.setTotalPrice(cart.getTotalPrice());
        cartContentResponse.setCartItems(cartItemDTOs);

        return cartContentResponse;
    }


}
