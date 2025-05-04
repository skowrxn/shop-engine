package pl.skowrxn.springecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    public OrderServiceImpl(UserRepository userRepository, OrderItemRepository orderItemRepository,
                            OrderRepository orderRepository, AddressRepository addressRepository,
                            ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
        this.addressRepository = addressRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    @Override
    public OrderDTO placeNewOrder(OrderRequestDTO orderDTO, Long userId) {
        User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
        Cart cart = user.getCart();

        if (cart == null || cart.getItems().isEmpty()) {
            throw new ResourceNotFoundException("Cart is null or empty");
        }

        Address address = addressRepository.findAddressById(orderDTO.getAddressId());  
        if (address == null) {
            throw new ResourceNotFoundException("Address", "id", orderDTO.getAddressId());
        }

        Order order = new Order();
        order.setUser(user);

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getTotalPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);

            Product product = item.getProduct();
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());;
        }
        this.orderItemRepository.saveAll(orderItems);

        order.setOrderItems(orderItems);
        order.setOrderDate(LocalDate.now());
        order.setShippingAddress(address);
        order.setStatus("Pending payment");
        order.setTotalPrice(cart.getTotalPrice());

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(orderDTO.getPaymentMethod());

        Order savedOrder = this.orderRepository.save(order);

        OrderDTO savedOrderDTO = this.modelMapper.map(savedOrder, OrderDTO.class);
        savedOrderDTO.setOrderItems(new ArrayList<>());
        orderItems.forEach(orderItem -> {
            savedOrderDTO.getOrderItems().add(this.modelMapper.map(orderItem, OrderItemDTO.class));
        });
        savedOrderDTO.setShippingAddress(this.modelMapper.map(address, AddressDTO.class));

        return savedOrderDTO;
    }
}
