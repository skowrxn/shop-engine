package pl.skowrxn.springecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.skowrxn.springecommerce.dto.OrderDTO;
import pl.skowrxn.springecommerce.dto.OrderRequestDTO;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.service.CartService;
import pl.skowrxn.springecommerce.service.OrderService;
import pl.skowrxn.springecommerce.util.AuthUtil;

import java.net.URI;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;
    private final AuthUtil authUtil;

    public OrderController(OrderService orderService, AuthUtil authUtil, CartService cartService) {
        this.orderService = orderService;
        this.authUtil = authUtil;
        this.cartService = cartService;
    }

    @PostMapping("/orders")
    public ResponseEntity<OrderDTO> createNewOrder(@RequestBody @Valid OrderRequestDTO orderRequestDTO) {
        User user = this.authUtil.getLoggedInUser();
        OrderDTO newOrder = orderService.placeNewOrder(orderRequestDTO, user.getId());
        this.cartService.clearCart();
        return ResponseEntity.created(URI.create("/orders/" + newOrder.getId())).body(newOrder);
    }

}
