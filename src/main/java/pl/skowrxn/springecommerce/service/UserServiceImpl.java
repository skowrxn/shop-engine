package pl.skowrxn.springecommerce.service;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.skowrxn.springecommerce.dto.UserDTO;
import pl.skowrxn.springecommerce.dto.UserListResponse;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ResourceConflictException;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.UserRepository;

import java.util.List;

public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    public UserDTO getUserDTOById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return this.modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserDTOByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
        return this.modelMapper.map(user, UserDTO.class);
    }

    public UserDTO getUserDTOByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return this.modelMapper.map(user, UserDTO.class);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

    @Override
    public UserDTO saveUser(UserDTO userDTO) {
        this.userRepository.findByEmail(userDTO.getEmail()).ifPresent(user -> {
            throw new ResourceConflictException("User", "email", user.getEmail());
        });
        this.userRepository.findByUsername(userDTO.getUsername()).ifPresent(user -> {
            throw new ResourceConflictException("User", "username", user.getUsername());
        });
        User savedUser = this.userRepository.save(this.modelMapper.map(userDTO, User.class));
        return this.modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public User saveUser(User user) {
        this.userRepository.findByEmail(user.getEmail()).ifPresent(existingUser -> {
            throw new ResourceConflictException("User", "email", user.getEmail());
        });
        this.userRepository.findByUsername(user.getUsername()).ifPresent(existingUser -> {
            throw new ResourceConflictException("User", "username", user.getUsername());
        });
        return this.userRepository.save(user);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO) {
        User existingUser = this.userRepository.findById(userDTO.getId())
                        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userDTO.getId()));
        existingUser.setEmail(userDTO.getEmail());
        existingUser.setUsername(userDTO.getUsername());
        existingUser.setAddresses(userDTO.getAddresses());
        existingUser.setRoles(userDTO.getRoles());
        existingUser.setProducts(userDTO.getProducts());
        User updatedUser = this.userRepository.save(existingUser);
        return this.modelMapper.map(updatedUser, UserDTO.class);
    }

    @Override
    public void deleteUser(Long id) {
        User user = this.userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        this.userRepository.delete(user);
    }

    @Override
    public UserListResponse getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> users = this.userRepository.findAll(pageable);
        List<UserDTO> userDTOs = users.stream()
                .map(user -> this.modelMapper.map(user, UserDTO.class))
                .toList();
        return new UserListResponse(userDTOs, page, size, users.getTotalPages(), users.getTotalElements(), users.isLast());
    }

    @Override
    public boolean existsByUsernameIgnoreCase(String username) {
        return this.userRepository.existsByUsernameIgnoreCase(username);
    }

    @Override
    public boolean existsByEmailIgnoreCase(String email) {
        return this.userRepository.existsByEmailIgnoreCase(email);
    }
}
