package pl.skowrxn.springecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.skowrxn.springecommerce.dto.CartItemDTO;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartContentResponse {

    private Long id;
    private Integer quantity;
    private Double totalPrice;
    private List<CartItemDTO> cartItems;

}
