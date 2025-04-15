package pl.skowrxn.springecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.skowrxn.springecommerce.entity.Category;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private Long id;

    @Size(min=3, message = "Product name must be at least 3 character long")
    @NotBlank(message = "Name cannot be blank")
    private String name;

    private String description;
    private String image;

    @Min(value=0, message = "Stock quantity cannot be lower than 0")
    private Integer stockQuantity;

    @Min(value=0, message = "Price cannot be lower than 0")
    private double price;

    private double specialPrice;
    private double discount;
    private Category category;



}
