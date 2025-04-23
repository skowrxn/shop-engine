package pl.skowrxn.springecommerce.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.skowrxn.springecommerce.entity.Cart;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.repository.UserRepository;
import pl.skowrxn.springecommerce.security.service.UserDetailsImpl;

public class AuthUtil {

    UserRepository userRepository;

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }
        Long loggedInUserId = ((UserDetailsImpl) authentication.getPrincipal()).getId();
        return userRepository.findById(loggedInUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Cart getUserCart() {
        return this.getLoggedInUser().getCart();
    }



}
