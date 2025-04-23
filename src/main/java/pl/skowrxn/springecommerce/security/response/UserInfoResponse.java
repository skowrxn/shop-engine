package pl.skowrxn.springecommerce.security.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@Setter
public class UserInfoResponse {

    private Long id;
    private String username;
    private List<String> roles;

}
