package pl.skowrxn.springecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import pl.skowrxn.springecommerce.entity.Address;
import pl.skowrxn.springecommerce.entity.Product;
import pl.skowrxn.springecommerce.entity.Role;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @Size(min=3, message = "Username must be at least 3 character long")
    @NotBlank(message = "Email cannot be blank")
    private String username;

    @Email
    private String email;

    private List<Role> roles;

    @JsonIgnore
    private List<Product> products;

    @JsonIgnore
    private List<Address> addresses;

}
