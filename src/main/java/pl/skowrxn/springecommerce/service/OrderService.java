package pl.skowrxn.springecommerce.service;

import org.springframework.transaction.annotation.Transactional;
import pl.skowrxn.springecommerce.dto.OrderDTO;
import pl.skowrxn.springecommerce.dto.OrderRequestDTO;

public interface OrderService {

    @Transactional
    OrderDTO placeNewOrder(OrderRequestDTO orderDTO, Long userId);
}
