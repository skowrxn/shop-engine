package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.UserDTO;
import pl.skowrxn.springecommerce.dto.response.UserListResponse;
import pl.skowrxn.springecommerce.entity.User;

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
