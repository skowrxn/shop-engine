package pl.skowrxn.springecommerce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import pl.skowrxn.springecommerce.dto.AddressDTO;
import pl.skowrxn.springecommerce.dto.OrderDTO;
import pl.skowrxn.springecommerce.dto.OrderItemDTO;
import pl.skowrxn.springecommerce.dto.OrderRequestDTO;
import pl.skowrxn.springecommerce.entity.*;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.AddressRepository;
import pl.skowrxn.springecommerce.repository.OrderItemRepository;
import pl.skowrxn.springecommerce.repository.OrderRepository;
import pl.skowrxn.springecommerce.repository.UserRepository;
import pl.skowrxn.springecommerce.service.OrderServiceImpl;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AddressRepository addressRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void testPlaceNewOrder_userNotExists(){
        Long userId = 999L;
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> orderService.placeNewOrder(new OrderRequestDTO(), userId));
        verify(userRepository).findById(userId);

        verifyNoInteractions(orderItemRepository, orderRepository, addressRepository, modelMapper);
    }

    @Test
    void placeNewOrder_success() {
        Long userId = 1L;
        UUID addressId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Cart cart = new Cart();
        cart.setItems(new ArrayList<>());
        user.setCart(cart);

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(10.0);

        cart.setItems(List.of(cartItem));
        cart.setTotalPrice(20.0);

        Address address = new Address();
        address.setId(addressId);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setAddressId(addressId);
        requestDTO.setPaymentMethod("Card");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findAddressById(addressId)).thenReturn(address);

        Order savedOrder = new Order();
        savedOrder.setId(UUID.randomUUID());
        savedOrder.setOrderDate(LocalDate.now());
        savedOrder.setOrderItems(List.of());
        savedOrder.setShippingAddress(address);
        savedOrder.setOrderItems(new ArrayList<>());

        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

        OrderDTO expectedOrderDTO = new OrderDTO();
        when(modelMapper.map(savedOrder, OrderDTO.class)).thenReturn(expectedOrderDTO);
        when(modelMapper.map(any(OrderItem.class), eq(OrderItemDTO.class))).thenReturn(new OrderItemDTO());
        when(modelMapper.map(address, AddressDTO.class)).thenReturn(new AddressDTO());

        OrderDTO result = orderService.placeNewOrder(requestDTO, userId);

        assertNotNull(result);
        verify(userRepository).findById(userId);
        verify(addressRepository).findAddressById(addressId);
        verify(orderItemRepository).saveAll(any());
        verify(orderRepository).save(any());
        verify(modelMapper).map(savedOrder, OrderDTO.class);
        verify(modelMapper, atLeastOnce()).map(any(OrderItem.class), eq(OrderItemDTO.class));
        verify(modelMapper).map(address, AddressDTO.class);

        assertEquals(expectedOrderDTO, result);
        assertEquals(8, product.getStockQuantity());
    }

    @Test
    void placeNewOrder_cartIsNull() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setCart(null);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        OrderRequestDTO requestDTO = new OrderRequestDTO();

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.placeNewOrder(requestDTO, userId));

        verify(userRepository).findById(userId);
    }

    @Test
    void placeNewOrder_cartIsEmpty() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        Cart cart = new Cart();
        cart.setItems(List.of());
        user.setCart(cart);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        OrderRequestDTO requestDTO = new OrderRequestDTO();

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> orderService.placeNewOrder(requestDTO, userId));

        assertEquals("Cart is null or empty", ex.getMessage());
        verify(userRepository).findById(userId);
    }

    @Test
    void placeNewOrder_addressNotFound() {
        Long userId = 1L;
        UUID addressId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);
        Cart cart = new Cart();

        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);

        CartItem cartItem = new CartItem();
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setTotalPrice(20.0);

        cart.setItems(List.of(cartItem));
        user.setCart(cart);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(addressRepository.findAddressById(addressId)).thenReturn(null);

        OrderRequestDTO requestDTO = new OrderRequestDTO();
        requestDTO.setAddressId(addressId);

        assertThrows(ResourceNotFoundException.class,
                () -> orderService.placeNewOrder(requestDTO, userId));

        verify(userRepository).findById(userId);
        verify(addressRepository).findAddressById(addressId);
    }




}
