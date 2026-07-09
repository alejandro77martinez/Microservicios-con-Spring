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
import static org.junit.jupiter.api.Assertions.assertNull;
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

    @Test
    void createShouldReturnCreatedUserWithProvidedRoles() {
        RegisterRequest request = RegisterRequest.builder()
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .password("plain")
                .roles(List.of("ADMIN"))
                .build();

        when(userRepository.findByEmail("neil@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ResponseEntity<UserResponse> response = userService.create(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals(List.of("ADMIN"), response.getBody().getRoles());
    }

    @Test
    void findAllShouldReturnUsersWhenPresent() {
        when(userRepository.findAll()).thenReturn(List.of(UserEntity.builder()
                .id("1")
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .roles(List.of("USER"))
                .build()));

        ResponseEntity<List<UserResponse>> response = userService.findAll();

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("neil@test.com", response.getBody().get(0).getEmail());
    }

    @Test
    void findByIdShouldReturnUserWhenFound() {
        when(userRepository.findById("1")).thenReturn(Optional.of(UserEntity.builder()
                .id("1")
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .roles(List.of("USER"))
                .build()));

        ResponseEntity<UserResponse> response = userService.findById("1");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("neil@test.com", response.getBody().getEmail());
    }

    @Test
    void findByEmailShouldReturnUserWhenFound() {
        when(userRepository.findByEmail("neil@test.com")).thenReturn(Optional.of(UserEntity.builder()
                .id("1")
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .roles(List.of("USER"))
                .build()));

        ResponseEntity<UserResponse> response = userService.findByEmail("neil@test.com");

        assertEquals(200, response.getStatusCode().value());
        assertEquals("neil@test.com", response.getBody().getEmail());
    }

    @Test
    void existUserNameShouldThrowBadRequestWhenNameIsNull() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.existUserName(null));

        assertEquals("Sin user name", exception.getMessage());
    }

    @Test
    void existUserNameShouldReturnBooleanBasedOnRepository() {
        when(userRepository.findByEmail("neil@test.com")).thenReturn(Optional.of(new UserEntity()));

        ResponseEntity<Boolean> response = userService.existUserName("neil@test.com");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(true, response.getBody());
    }

    @Test
    void searchUsersByEmailShouldReturnMatchingUsers() {
        when(userRepository.findByEmailContainingIgnoreCase("neil")).thenReturn(List.of(UserEntity.builder()
                .id("1")
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .build()));

        ResponseEntity<List<com.auth_service.dtos.UserEmailResponse>> response = userService.searchUsersByEmail("neil");

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
        assertEquals("neil@test.com", response.getBody().get(0).getEmail());
    }

    @Test
    void searchUsersByTeamIdsShouldThrowBadRequestWhenIdsAreEmpty() {
        BadRequestException exception = assertThrows(BadRequestException.class, () -> userService.searchUsersByTeamIds(List.of()));

        assertEquals("Team IDs cannot be null or empty", exception.getMessage());
    }

    @Test
    void searchUsersByTeamIdsShouldReturnMatchingUsers() {
        when(userRepository.findAllById(List.of("1", "2"))).thenReturn(List.of(UserEntity.builder()
                .id("1")
                .name("Neil")
                .lastName("Dev")
                .email("neil@test.com")
                .build()));

        ResponseEntity<List<com.auth_service.dtos.UserEmailResponse>> response = userService.searchUsersByTeamIds(List.of("1", "2"));

        assertEquals(200, response.getStatusCode().value());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void updateAndDeleteShouldReturnNullPlaceholders() {
        assertNull(userService.update("1", RegisterRequest.builder().build()));
        assertNull(userService.deleteById("1"));
    }
}
