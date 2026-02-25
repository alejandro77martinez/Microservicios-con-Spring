package com.auth_service.services.implementations;

import com.auth_service.dtos.AuthResponse;
import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ResourceNotFoundException;
import com.auth_service.models.UserEntity;
import com.auth_service.repositories.UserRepository;
import com.auth_service.services.interfaces.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void createShouldReturnCreatedUserWithDefaultRoleWhenRolesAreMissing() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .password("plain")
                .build();

        when(userRepository.findByEmail("neil@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<UserResponse> response = userService.create(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("neil@test.com", response.getBody().getEmail());
        assertEquals(List.of("USER"), response.getBody().getRoles());
    }

    @Test
    void createShouldThrowBadRequestWhenEmailAlreadyExists() {
        RegisterRequest request = RegisterRequest.builder().email("exists@test.com").build();
        when(userRepository.findByEmail("exists@test.com")).thenReturn(Optional.of(new UserEntity()));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.create(request));

        assertEquals("Email already in use", exception.getMessage());
    }

    @Test
    void loginShouldReturnTokenAndRolesWhenCredentialsAreValid() {
        LoginRequest request = LoginRequest.builder().email("user@test.com").password("secret").build();
        UserDetails principal = new User(
                "user@test.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(jwtService.generateToken(principal)).thenReturn("jwt-token");
        when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(UserEntity.builder()
                .email("user@test.com")
                .roles(List.of("ADMIN"))
                .build()));

        ResponseEntity<AuthResponse> response = userService.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("jwt-token", response.getBody().getToken());
        assertEquals("user@test.com", response.getBody().getUser());
        assertEquals(List.of("ADMIN"), response.getBody().getRoles());
    }

    @Test
    void loginShouldThrowBadRequestWhenAuthenticationFails() {
        LoginRequest request = LoginRequest.builder().email("user@test.com").password("wrong").build();
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.login(request));

        assertEquals("Bad credentials", exception.getMessage());
    }

    @Test
    void findAllShouldThrowResourceNotFoundWhenNoUsersExist() {
        when(userRepository.findAll()).thenReturn(List.of());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, userService::findAll);

        assertEquals("No users found", exception.getMessage());
    }

    @Test
    void findByEmailShouldThrowResourceNotFoundWhenUserDoesNotExist() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> userService.findByEmail("missing@test.com")
        );

        assertEquals("User not found with email: missing@test.com", exception.getMessage());
    }

    @Test
    void validateUserShouldReturnTrueWhenTokenIsValid() {
        when(jwtService.validateToken("token", "user@test.com")).thenReturn(true);

        ResponseEntity<Boolean> response = userService.validateUser("token", "user@test.com");

        assertTrue(response.getBody());
        verify(jwtService).validateToken("token", "user@test.com");
    }

    @Test
    void validateUserShouldThrowBadRequestWhenTokenIsInvalid() {
        when(jwtService.validateToken(anyString(), anyString())).thenReturn(false);

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> userService.validateUser("bad-token", "user@test.com")
        );

        assertEquals("Invalid token", exception.getMessage());
    }
}
