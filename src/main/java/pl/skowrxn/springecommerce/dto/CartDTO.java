package pl.skowrxn.springecommerce.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CartDTO {

    private Long id;

    @Min(value = 0, message = "Quantity must be greater than or equal to 0")
    private Integer quantity;

    @Min(value = 0, message = "Total price must be greater than or equal to 0")
    private Double totalPrice;

    private List<ProductDTO> products;

}
