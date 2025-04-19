package pl.skowrxn.springecommerce.security.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class SignupRequest {

    private String username;
    private String email;
    private Set<String> roles;
    private String password;

}
