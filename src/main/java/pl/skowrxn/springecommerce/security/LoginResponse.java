package pl.skowrxn.springecommerce.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class LoginResponse {

    private String jwtToken;
    private String username;
    private List<String> roles;

}
