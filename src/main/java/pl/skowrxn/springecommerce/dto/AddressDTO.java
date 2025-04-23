package pl.skowrxn.springecommerce.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressDTO {

    private UUID id;

    @Size(min=3, message = "Street name must be at least 3 characters long")
    private String street;

    @Size(min=3, message = "City name must be at least 3 characters long")
    private String city;

    @Size(min=3, message = "Province name must be at least 3 characters long")
    private String province;

    @Size(min=3, message = "Country name must be at least 3 characters long")
    private String country;

    @Size(min=3, message = "Postal code must be at least 3 characters long")
    private String postalCode;

    @Size(min=9, message = "Phone number must be at least 9 characters long")
    private String phoneNumber;

    private boolean defaultAddress;

}
