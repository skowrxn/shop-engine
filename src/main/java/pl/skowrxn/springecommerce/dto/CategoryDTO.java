package pl.skowrxn.springecommerce.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Name cannot be blank")
    @Size(min=3, message = "Category name must be at least 3 character long")
    private String name;

}
