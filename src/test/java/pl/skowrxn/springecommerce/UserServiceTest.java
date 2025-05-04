package pl.skowrxn.springecommerce;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import pl.skowrxn.springecommerce.dto.UserDTO;
import pl.skowrxn.springecommerce.dto.response.UserListResponse;
import pl.skowrxn.springecommerce.entity.Address;
import pl.skowrxn.springecommerce.entity.Product;
import pl.skowrxn.springecommerce.entity.Role;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.exception.ResourceConflictException;
import pl.skowrxn.springecommerce.exception.ResourceNotFoundException;
import pl.skowrxn.springecommerce.repository.UserRepository;
import pl.skowrxn.springecommerce.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testGetUserDTOById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(userId, "testuser", "test@example.com", roles, products, addresses);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserDTOById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findById(userId);
        verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void testGetUserDTOById_UserNotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserDTOById(userId));

        verify(userRepository).findById(userId);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetUserDTOByEmail_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail(email);

        List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(1L, "testuser", email, roles, products, addresses);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserDTOByEmail(email);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals(email, result.getEmail());

        verify(userRepository).findByEmail(email);
        verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void testGetUserDTOByEmail_UserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserDTOByEmail(email));

        verify(userRepository).findByEmail(email);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetUserDTOByUsername_Success() {
        String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("test@example.com");

        List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(1L, username, "test@example.com", roles, products, addresses);

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(modelMapper.map(user, UserDTO.class)).thenReturn(userDTO);

        UserDTO result = userService.getUserDTOByUsername(username);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findByUsername(username);
        verify(modelMapper).map(user, UserDTO.class);
    }

    @Test
    void testGetUserDTOByUsername_UserNotFound() {
        String username = "nonexistent";

        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserDTOByUsername(username));

        verify(userRepository).findByUsername(username);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testGetUserById_Success() {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        User result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("test@example.com", result.getEmail());

        verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserById_UserNotFound() {
        Long userId = 999L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(userId));

                verify(userRepository).findById(userId);
    }

    @Test
    void testGetUserByEmail_Success() {
                String email = "test@example.com";
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail(email);

                when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

                User result = userService.getUserByEmail(email);

                assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals(email, result.getEmail());

                verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetUserByEmail_UserNotFound() {
                String email = "nonexistent@example.com";

                when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> userService.getUserByEmail(email));

                verify(userRepository).findByEmail(email);
    }

    @Test
    void testGetUserByUsername_Success() {
                String username = "testuser";
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail("test@example.com");

                when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

                User result = userService.getUserByUsername(username);

                assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(username, result.getUsername());
        assertEquals("test@example.com", result.getEmail());

                verify(userRepository).findByUsername(username);
    }

    @Test
    void testGetUserByUsername_UserNotFound() {
                String username = "nonexistent";

                when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> userService.getUserByUsername(username));

                verify(userRepository).findByUsername(username);
    }

    @Test
    void testSaveUserDTO_Success() {
                List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(null, "newuser", "new@example.com", roles, products, addresses);

        User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");

        UserDTO savedUserDTO = new UserDTO(1L, "newuser", "new@example.com", roles, products, addresses);

                when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.empty());
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(savedUserDTO);

                UserDTO result = userService.saveUser(userDTO);

                assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());

                verify(userRepository).findByEmail(userDTO.getEmail());
        verify(userRepository).findByUsername(userDTO.getUsername());
        verify(modelMapper).map(userDTO, User.class);
        verify(userRepository).save(user);
        verify(modelMapper).map(savedUser, UserDTO.class);
    }

    @Test
    void testSaveUserDTO_EmailConflict() {
                List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(null, "newuser", "existing@example.com", roles, products, addresses);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");

                when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.of(existingUser));

                assertThrows(ResourceConflictException.class, () -> userService.saveUser(userDTO));

                verify(userRepository).findByEmail(userDTO.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testSaveUserDTO_UsernameConflict() {
                List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(null, "existinguser", "new@example.com", roles, products, addresses);

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");

                when(userRepository.findByEmail(userDTO.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(Optional.of(existingUser));

                assertThrows(ResourceConflictException.class, () -> userService.saveUser(userDTO));

                verify(userRepository).findByEmail(userDTO.getEmail());
        verify(userRepository).findByUsername(userDTO.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testSaveUser_Success() {
                User user = new User();
        user.setUsername("newuser");
        user.setEmail("new@example.com");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("newuser");
        savedUser.setEmail("new@example.com");

                when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(savedUser);

                User result = userService.saveUser(user);

                assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("newuser", result.getUsername());
        assertEquals("new@example.com", result.getEmail());

                verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).findByUsername(user.getUsername());
        verify(userRepository).save(user);
    }

    @Test
    void testSaveUser_EmailConflict() {
                User user = new User();
        user.setUsername("newuser");
        user.setEmail("existing@example.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");

                when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(existingUser));

                assertThrows(ResourceConflictException.class, () -> userService.saveUser(user));

                verify(userRepository).findByEmail(user.getEmail());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testSaveUser_UsernameConflict() {
                User user = new User();
        user.setUsername("existinguser");
        user.setEmail("new@example.com");

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("existinguser");
        existingUser.setEmail("existing@example.com");

                when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(existingUser));

                assertThrows(ResourceConflictException.class, () -> userService.saveUser(user));

                verify(userRepository).findByEmail(user.getEmail());
        verify(userRepository).findByUsername(user.getUsername());
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void testUpdateUser_Success() {
                Long userId = 1L;
        List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(userId, "updateduser", "updated@example.com", roles, products, addresses);

        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setUsername("originaluser");
        existingUser.setEmail("original@example.com");

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setAddresses(new ArrayList<>());
        updatedUser.setRoles(new ArrayList<>());
        updatedUser.setProducts(new ArrayList<>());

        UserDTO updatedUserDTO = new UserDTO(userId, "updateduser", "updated@example.com", roles, products, addresses);

                when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(updatedUser);
        when(modelMapper.map(updatedUser, UserDTO.class)).thenReturn(updatedUserDTO);

                UserDTO result = userService.updateUser(userDTO);

                assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals("updateduser", result.getUsername());
        assertEquals("updated@example.com", result.getEmail());

                verify(userRepository).findById(userId);
        verify(userRepository).save(existingUser);
        verify(modelMapper).map(updatedUser, UserDTO.class);
    }

    @Test
    void testUpdateUser_UserNotFound() {
                Long userId = 999L;
        List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO = new UserDTO(userId, "updateduser", "updated@example.com", roles, products, addresses);

                when(userRepository.findById(userId)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(userDTO));

                verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(modelMapper);
    }

    @Test
    void testDeleteUser_Success() {
                Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

                when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userRepository).delete(user);

                userService.deleteUser(userId);

                verify(userRepository).findById(userId);
        verify(userRepository).delete(user);
    }

    @Test
    void testDeleteUser_UserNotFound() {
                Long userId = 999L;

                when(userRepository.findById(userId)).thenReturn(Optional.empty());

                assertThrows(ResourceNotFoundException.class, () -> userService.deleteUser(userId));

                verify(userRepository).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    void testGetAllUsers() {
                int page = 0;
        int size = 10;

        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("user1");
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");
        user2.setEmail("user2@example.com");

        List<User> users = List.of(user1, user2);
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(page, size), users.size());

        List<Role> roles = new ArrayList<>();
        List<Product> products = new ArrayList<>();
        List<Address> addresses = new ArrayList<>();

        UserDTO userDTO1 = new UserDTO(1L, "user1", "user1@example.com", roles, products, addresses);
        UserDTO userDTO2 = new UserDTO(2L, "user2", "user2@example.com", roles, products, addresses);

                when(userRepository.findAll(any(Pageable.class))).thenReturn(userPage);
        when(modelMapper.map(user1, UserDTO.class)).thenReturn(userDTO1);
        when(modelMapper.map(user2, UserDTO.class)).thenReturn(userDTO2);

                UserListResponse result = userService.getAllUsers(page, size);

                assertNotNull(result);
        assertEquals(page, result.getPage());
        assertEquals(size, result.getPageSize());
        assertEquals(1, result.getTotalPages());
        assertEquals(2, result.getTotalElements());
        assertTrue(result.isLastPage());
        assertEquals(2, result.getUsers().size());

                verify(userRepository).findAll(any(Pageable.class));
        verify(modelMapper).map(user1, UserDTO.class);
        verify(modelMapper).map(user2, UserDTO.class);
    }

    @Test
    void testExistsByUsernameIgnoreCase_True() {
                String username = "testuser";

                when(userRepository.existsByUsernameIgnoreCase(username)).thenReturn(true);

                boolean result = userService.existsByUsernameIgnoreCase(username);

                assertTrue(result);

                verify(userRepository).existsByUsernameIgnoreCase(username);
    }

    @Test
    void testExistsByUsernameIgnoreCase_False() {
                String username = "nonexistent";

                when(userRepository.existsByUsernameIgnoreCase(username)).thenReturn(false);

                boolean result = userService.existsByUsernameIgnoreCase(username);

                assertFalse(result);

                verify(userRepository).existsByUsernameIgnoreCase(username);
    }

    @Test
    void testExistsByEmailIgnoreCase_True() {
                String email = "test@example.com";

                when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

                boolean result = userService.existsByEmailIgnoreCase(email);

                assertTrue(result);

                verify(userRepository).existsByEmailIgnoreCase(email);
    }

    @Test
    void testExistsByEmailIgnoreCase_False() {
                String email = "nonexistent@example.com";

                when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(false);

                boolean result = userService.existsByEmailIgnoreCase(email);

                assertFalse(result);

                verify(userRepository).existsByEmailIgnoreCase(email);
    }
}