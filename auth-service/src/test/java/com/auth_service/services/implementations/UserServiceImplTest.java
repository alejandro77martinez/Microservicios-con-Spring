package com.auth_service.services.implementations;

import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ResourceNotFoundException;
import com.auth_service.models.UserEntity;
import com.auth_service.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

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

}
