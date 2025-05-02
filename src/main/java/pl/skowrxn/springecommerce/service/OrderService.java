package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.OrderDTO;
import pl.skowrxn.springecommerce.dto.OrderRequestDTO;

public interface OrderService {

    OrderDTO placeNewOrder(OrderRequestDTO orderDTO, Long userId);
}
