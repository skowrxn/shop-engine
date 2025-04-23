package pl.skowrxn.springecommerce.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CartItemDTO {

    private Long id;
    private Long cartId;
    private ProductDTO product;
    private Double singlePrice;
    private Double totalPrice;
    private Integer quantity;

}
