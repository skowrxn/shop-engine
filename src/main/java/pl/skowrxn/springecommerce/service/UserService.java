package pl.skowrxn.springecommerce.service;

import org.springframework.stereotype.Service;
import pl.skowrxn.springecommerce.dto.UserDTO;
import pl.skowrxn.springecommerce.dto.UserListResponse;
import pl.skowrxn.springecommerce.entity.User;

@Service
public interface UserService {

    User getUserById(Long id);

    User getUserByEmail(String email);

    User getUserByUsername(String username);

    UserDTO saveUser(UserDTO user);

    User saveUser(User user);

    UserDTO updateUser(UserDTO user);

    void deleteUser(Long id);

    UserListResponse getAllUsers(int page, int size);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByEmailIgnoreCase(String email);
}
