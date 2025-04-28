package pl.skowrxn.springecommerce.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import pl.skowrxn.springecommerce.entity.Address;
import pl.skowrxn.springecommerce.entity.Product;
import pl.skowrxn.springecommerce.entity.Role;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {

    private Long id;

    @Min(value=3, message = "Username must be at least 3 character long")
    @NotBlank(message = "Email cannot be blank")
    private String username;

    @Email
    private String email;

    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    private Set<Product> products = new HashSet<>();

    @JsonIgnore
    private Set<Address> addresses = new HashSet<>();

}
