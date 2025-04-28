package pl.skowrxn.springecommerce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.skowrxn.springecommerce.dto.AddressDTO;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddressListResponse {

    private List<AddressDTO> addresses;
    private Long userId;
    private Integer totalAddresses;

}
