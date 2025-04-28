package pl.skowrxn.springecommerce.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import pl.skowrxn.springecommerce.entity.Role;
import pl.skowrxn.springecommerce.entity.RoleType;
import pl.skowrxn.springecommerce.entity.User;
import pl.skowrxn.springecommerce.repository.RoleRepository;
import pl.skowrxn.springecommerce.security.JWTUtils;
import pl.skowrxn.springecommerce.security.request.LoginRequest;
import pl.skowrxn.springecommerce.security.request.SignupRequest;
import pl.skowrxn.springecommerce.security.response.AuthMessageResponse;
import pl.skowrxn.springecommerce.security.response.UserInfoResponse;
import pl.skowrxn.springecommerce.security.service.UserDetailsImpl;
import pl.skowrxn.springecommerce.service.UserService;

import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JWTUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JWTUtils jwtUtils, AuthenticationManager authenticationManager, RoleRepository roleRepository,
                          UserService userService, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping ("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (AuthenticationException e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Invalid username or password");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }

        SecurityContextHolder .getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        ResponseCookie responseCookie = jwtUtils.generateJwtCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        UserInfoResponse userInfoResponse = new UserInfoResponse(
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, responseCookie.toString()).body(userInfoResponse);
    }


    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signupRequest) {
        if(this.userService.existsByUsernameIgnoreCase(signupRequest.getUsername())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Username " + signupRequest.getUsername() + " is already taken");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        if(this.userService.existsByEmailIgnoreCase(signupRequest.getEmail())) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Email " + signupRequest.getEmail() + " is already taken");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(this.passwordEncoder.encode(signupRequest.getPassword()));

        Set<String> roles = signupRequest.getRoles();
        Set<Role> userRoles = new HashSet<>();

        if (roles == null) {
            Role userRole = this.roleRepository.findRolesByRoleType(RoleType.ROLE_USER).stream().findFirst()
                    .orElseThrow(() -> new RuntimeException("Error: Default user role not found."));
            userRoles.add(userRole);
        } else {
            roles.forEach(role -> {
                switch (role) {
                    case "admin" -> {
                        Role adminRole = this.roleRepository.findRolesByRoleType(RoleType.ROLE_ADMIN).stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        userRoles.add(adminRole);
                    }
                    case "seller" -> {
                        Role sellerRole = this.roleRepository.findRolesByRoleType(RoleType.ROLE_SELLER).stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Error: Role not found."));
                        userRoles.add(sellerRole);
                    }
                    default -> {
                        Role userRole = this.roleRepository.findRolesByRoleType(RoleType.ROLE_USER).stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("Error: Default User role could not be found."));
                        userRoles.add(userRole);
                    }
                }
            });
        }

        user.setRoles(userRoles);
        User savedUser = this.userService.saveUser(user);

        AuthMessageResponse authMessageResponse = new AuthMessageResponse(savedUser.getId(), "User registered successfully");
        return new ResponseEntity<>(authMessageResponse, HttpStatus.CREATED);
    }

    @GetMapping("/account-details")
    public ResponseEntity<?> getAccountDetails(Authentication authentication) {
        if(authentication == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not authenticated");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        UserDetailsImpl user = (UserDetailsImpl) authentication.getPrincipal();
        UserInfoResponse userInfoResponse = new UserInfoResponse(user.getId(), user.getUsername(), user.getEmail(),
                user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList());
        return ResponseEntity.ok(userInfoResponse);
    }


    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(Authentication authentication) {
        if (authentication == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "User not authenticated");
            errorResponse.put("status", false);
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
        ResponseCookie cookie = jwtUtils.generateCleanJwtCookie();
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You've been signed out!");
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(response);
    }



}
