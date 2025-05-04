package pl.skowrxn.springecommerce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import pl.skowrxn.springecommerce.dto.CartItemDTO;
import pl.skowrxn.springecommerce.dto.ProductDTO;
import pl.skowrxn.springecommerce.dto.response.CartContentResponse;
import pl.skowrxn.springecommerce.entity.Cart;
import pl.skowrxn.springecommerce.entity.CartItem;
import pl.skowrxn.springecommerce.entity.Product;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ProductOutOfStockException;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.CartItemRepository;
import pl.skowrxn.springecommerce.repository.CartRepository;
import pl.skowrxn.springecommerce.repository.ProductRepository;
import pl.skowrxn.springecommerce.service.CartServiceImpl;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private AuthUtil authUtil;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void testAddToCart_ProductNotFound() {
        Long productId = 999L;
        Integer quantity = 1;

        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.addToCart(productId, quantity));

        verify(productRepository).findById(productId);
        verifyNoInteractions(cartRepository, cartItemRepository, modelMapper);
    }

    @Test
    void testAddToCart_ProductOutOfStock() {
        Long productId = 1L;
        Integer quantity = 10;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(5);

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        assertThrows(ProductOutOfStockException.class, () -> cartService.addToCart(productId, quantity));

        verify(productRepository).findById(productId);
        verifyNoInteractions(cartRepository, cartItemRepository, modelMapper);
    }

    @Test
    void testAddToCart_NewCartCreated() {
        Long productId = 1L;
        Integer quantity = 2;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(10);
        product.setPrice(100.0);

        User user = new User();
        user.setId(1L);
        user.setCart(null);

        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setUser(user);
        newCart.setItems(new ArrayList<>());

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(quantity);
        cartItem.setSinglePrice(product.getPrice());
        cartItem.setTotalPrice(product.getPrice() * quantity);
        cartItem.setCart(newCart);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(1L);
        cartItemDTO.setQuantity(quantity);
        cartItemDTO.setTotalPrice(product.getPrice() * quantity);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setPrice(product.getPrice());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(cartItem);
        when(modelMapper.map(cartItem, CartItemDTO.class)).thenReturn(cartItemDTO);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartItemDTO result = cartService.addToCart(productId, quantity);

        assertNotNull(result);
        assertEquals(quantity, result.getQuantity());
        assertEquals(product.getPrice() * quantity, result.getTotalPrice());

        verify(productRepository).findById(productId);
        verify(authUtil).getLoggedInUser();
        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).save(any(CartItem.class));
        verify(productRepository).save(product);
        verify(modelMapper).map(cartItem, CartItemDTO.class);
        verify(modelMapper).map(product, ProductDTO.class);

        assertEquals(8, product.getStockQuantity());
    }

    @Test
    void testAddToCart_ExistingCartItem() {
        Long productId = 1L;
        Integer quantity = 2;

        Product product = new Product();
        product.setId(productId);
        product.setStockQuantity(10);
        product.setPrice(100.0);

        CartItem existingCartItem = new CartItem();
        existingCartItem.setId(1L);
        existingCartItem.setProduct(product);
        existingCartItem.setQuantity(3);
        existingCartItem.setSinglePrice(product.getPrice());
        existingCartItem.setTotalPrice(product.getPrice() * 3);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(existingCartItem)));
        cart.setTotalPrice(existingCartItem.getTotalPrice());

        existingCartItem.setCart(cart);

        User user = new User();
        user.setId(1L);
        user.setCart(cart);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(1L);
        updatedCartItem.setProduct(product);
        updatedCartItem.setQuantity(5);
        updatedCartItem.setSinglePrice(product.getPrice());
        updatedCartItem.setTotalPrice(product.getPrice() * 5);
        updatedCartItem.setCart(cart);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(1L);
        cartItemDTO.setQuantity(5);
        cartItemDTO.setTotalPrice(product.getPrice() * 5);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(productId);
        productDTO.setPrice(product.getPrice());

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(updatedCartItem);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(updatedCartItem, CartItemDTO.class)).thenReturn(cartItemDTO);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartItemDTO result = cartService.addToCart(productId, quantity);

        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        assertEquals(product.getPrice() * 5, result.getTotalPrice());

        verify(productRepository).findById(productId);
        verify(authUtil).getLoggedInUser();
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
        verify(productRepository).save(product);
        verify(modelMapper).map(updatedCartItem, CartItemDTO.class);
        verify(modelMapper).map(product, ProductDTO.class);

        assertEquals(8, product.getStockQuantity());
    }

    @Test
    void testRemoveFromCart_CartItemNotFound() {
        Long cartItemId = 999L;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.removeFromCart(cartItemId));

        verify(cartItemRepository).findById(cartItemId);
        verifyNoInteractions(cartRepository, productRepository);
    }

    @Test
    void testRemoveFromCart_Success() {
        Long cartItemId = 1L;

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(8);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(200.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setTotalPrice(200.0);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        cartItem.setCart(cart);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        cartService.removeFromCart(cartItemId);

        verify(cartItemRepository).findById(cartItemId);
        verify(productRepository).save(product);
        verify(cartItemRepository).delete(cartItem);
        verify(cartRepository).save(cart);

        assertEquals(10, product.getStockQuantity());
        assertEquals(0.0, cart.getTotalPrice());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testClearCart() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());
        cart.setTotalPrice(300.0);

        user.setCart(cart);

        when(authUtil.getLoggedInUser()).thenReturn(user);

        cartService.clearCart();

        verify(authUtil).getLoggedInUser();
        verify(cartItemRepository).deleteAll(cart.getItems());
        verify(cartRepository).save(cart);

        assertEquals(0.0, cart.getTotalPrice());
        assertTrue(cart.getItems().isEmpty());
    }

    @Test
    void testClearCart_NullCart() {
        User user = new User();
        user.setId(1L);
        user.setCart(null);

        Cart newCart = new Cart();
        newCart.setUser(user);

        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        cartService.clearCart();

        verify(authUtil, times(2)).getLoggedInUser();
        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).deleteAll(Collections.emptyList());
    }

    @Test
    void testGetTotalPrice() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setTotalPrice(300.0);

        user.setCart(cart);

        when(authUtil.getLoggedInUser()).thenReturn(user);

        Double result = cartService.getTotalPrice();

        assertEquals(300.0, result);
        verify(authUtil).getLoggedInUser();
    }

    @Test
    void testGetTotalPrice_NullCart() {
        User user = new User();
        user.setId(1L);
        user.setCart(null);

        Cart newCart = new Cart();
        newCart.setUser(user);
        newCart.setTotalPrice(0.0);

        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        Double result = cartService.getTotalPrice();

        assertEquals(0.0, result);
        verify(authUtil, times(2)).getLoggedInUser();
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void testGetTotalQuantity() {
        User user = new User();
        user.setId(1L);

        CartItem item1 = new CartItem();
        CartItem item2 = new CartItem();

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item1, item2)));

        user.setCart(cart);

        when(authUtil.getLoggedInUser()).thenReturn(user);

        Integer result = cartService.getTotalQuantity();

        assertEquals(2, result);
        verify(authUtil).getLoggedInUser();
    }

    @Test
    void testUpdateCartItemQuantity_CartItemNotFound() {
        Long cartItemId = 999L;
        Integer quantity = 5;

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cartService.updateCartItemQuantity(cartItemId, quantity));

        verify(cartItemRepository).findById(cartItemId);
        verifyNoInteractions(cartRepository, modelMapper);
    }

    @Test
    void testUpdateCartItemQuantity_NegativeQuantity() {
        Long cartItemId = 1L;
        Integer quantity = -1;

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));

        assertThrows(IllegalArgumentException.class, () -> cartService.updateCartItemQuantity(cartItemId, quantity));

        verify(cartItemRepository).findById(cartItemId);
        verifyNoInteractions(cartRepository, modelMapper);
    }

    @Test
    void testUpdateCartItemQuantity_ZeroQuantity() {
        Long cartItemId = 1L;
        Integer quantity = 0;

        Product product = new Product();
        product.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setTotalPrice(10.0);
        cartItem.setQuantity(5);
        cartItem.setProduct(product);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        cartItem.setCart(cart);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        doNothing().when(cartItemRepository).delete(cartItem);

        CartItemDTO result = cartService.updateCartItemQuantity(cartItemId, quantity);

        assertNull(result);
        verify(cartItemRepository).findById(cartItemId);
    }

    @Test
    void testUpdateCartItemQuantity_Success() {
        Long cartItemId = 1L;
        Integer quantity = 5;

        Product product = new Product();
        product.setId(1L);
        product.setPrice(100.0);

        CartItem cartItem = new CartItem();
        cartItem.setId(cartItemId);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setSinglePrice(100.0);
        cartItem.setTotalPrice(200.0);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setTotalPrice(200.0);
        cart.setItems(new ArrayList<>(List.of(cartItem)));

        cartItem.setCart(cart);

        CartItem updatedCartItem = new CartItem();
        updatedCartItem.setId(cartItemId);
        updatedCartItem.setProduct(product);
        updatedCartItem.setQuantity(quantity);
        updatedCartItem.setSinglePrice(100.0);
        updatedCartItem.setTotalPrice(500.0);
        updatedCartItem.setCart(cart);

        CartItemDTO cartItemDTO = new CartItemDTO();
        cartItemDTO.setId(cartItemId);
        cartItemDTO.setQuantity(quantity);
        cartItemDTO.setTotalPrice(500.0);

        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(1L);
        productDTO.setPrice(100.0);

        when(cartItemRepository.findById(cartItemId)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(updatedCartItem);
        when(cartRepository.save(cart)).thenReturn(cart);
        when(modelMapper.map(updatedCartItem, CartItemDTO.class)).thenReturn(cartItemDTO);
        when(modelMapper.map(product, ProductDTO.class)).thenReturn(productDTO);

        CartItemDTO result = cartService.updateCartItemQuantity(cartItemId, quantity);

        assertNotNull(result);
        assertEquals(quantity, result.getQuantity());
        assertEquals(500.0, result.getTotalPrice());

        verify(cartItemRepository).findById(cartItemId);
        verify(cartItemRepository).save(any(CartItem.class));
        verify(cartRepository).save(cart);
        verify(modelMapper).map(updatedCartItem, CartItemDTO.class);
        verify(modelMapper).map(product, ProductDTO.class);
    }

    @Test
    void testGetCartContent() {
        User user = new User();
        user.setId(1L);

        Product product1 = new Product();
        product1.setId(1L);

        Product product2 = new Product();
        product2.setId(2L);

        CartItem item1 = new CartItem();
        item1.setId(1L);
        item1.setProduct(product1);

        CartItem item2 = new CartItem();
        item2.setId(2L);
        item2.setProduct(product2);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setItems(new ArrayList<>(List.of(item1, item2)));
        cart.setTotalPrice(300.0);

        item1.setCart(cart);
        item2.setCart(cart);
        user.setCart(cart);

        CartItemDTO itemDTO1 = new CartItemDTO();
        itemDTO1.setId(1L);

        CartItemDTO itemDTO2 = new CartItemDTO();
        itemDTO2.setId(2L);

        ProductDTO productDTO1 = new ProductDTO();
        productDTO1.setId(1L);

        ProductDTO productDTO2 = new ProductDTO();
        productDTO2.setId(2L);

        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(modelMapper.map(item1, CartItemDTO.class)).thenReturn(itemDTO1);
        when(modelMapper.map(item2, CartItemDTO.class)).thenReturn(itemDTO2);
        when(modelMapper.map(product1, ProductDTO.class)).thenReturn(productDTO1);
        when(modelMapper.map(product2, ProductDTO.class)).thenReturn(productDTO2);

        CartContentResponse result = cartService.getCartContent();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(2, result.getQuantity());
        assertEquals(300.0, result.getTotalPrice());
        assertEquals(2, result.getCartItems().size());

        verify(authUtil).getLoggedInUser();
        verify(modelMapper, times(2)).map(any(CartItem.class), eq(CartItemDTO.class));
        verify(modelMapper, times(2)).map(any(Product.class), eq(ProductDTO.class));
    }

    @Test
    void testGetCartContent_NullCart() {
        User user = new User();
        user.setId(1L);
        user.setCart(null);

        Cart newCart = new Cart();
        newCart.setId(1L);
        newCart.setUser(user);

        when(authUtil.getLoggedInUser()).thenReturn(user);
        when(cartRepository.save(any(Cart.class))).thenReturn(newCart);

        CartContentResponse result = cartService.getCartContent();

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(0, result.getQuantity());
        assertEquals(0.0, result.getTotalPrice());
        assertTrue(result.getCartItems().isEmpty());

        verify(authUtil, times(2)).getLoggedInUser();
        verify(cartRepository).save(any(Cart.class));
    }
}
