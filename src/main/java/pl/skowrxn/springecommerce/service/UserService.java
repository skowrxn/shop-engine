package pl.skowrxn.springecommerce.service;

import pl.skowrxn.springecommerce.dto.UserDTO;
import pl.skowrxn.springecommerce.dto.UserListResponse;

public interface UserService {

    public UserDTO getUserById(Long id);

    public UserDTO getUserByEmail(String email);

    public UserDTO getUserByUsername(String username);

    public UserDTO createUser(UserDTO user);

    public UserDTO updateUser(UserDTO user);

    public void deleteUser(Long id);

    public UserListResponse getAllUsers(int page, int size);

}
