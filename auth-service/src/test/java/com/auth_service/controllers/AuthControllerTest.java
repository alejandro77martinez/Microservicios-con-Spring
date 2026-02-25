package com.auth_service.controllers;

import com.auth_service.dtos.AuthResponse;
import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.dtos.ValidateTokenRequest;
import com.auth_service.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController controller;

    @Test
    void registerShouldDelegateToUserService() {
        RegisterRequest request = RegisterRequest.builder().email("user@test.com").build();
        UserResponse user = UserResponse.builder().email("user@test.com").build();
        when(userService.create(request)).thenReturn(ResponseEntity.status(201).body(user));

        ResponseEntity<UserResponse> response = controller.register(request);

        assertEquals(201, response.getStatusCode().value());
        assertEquals("user@test.com", response.getBody().getEmail());
        verify(userService).create(request);
    }

    @Test
    void loginShouldDelegateToUserService() {
        LoginRequest request = LoginRequest.builder().email("user@test.com").password("secret").build();
        AuthResponse authResponse = AuthResponse.builder().token("token").user("user@test.com").roles(List.of("USER")).build();
        when(userService.login(request)).thenReturn(ResponseEntity.ok(authResponse));

        ResponseEntity<AuthResponse> response = controller.login(request);

        assertEquals(200, response.getStatusCode().value());
        assertEquals("token", response.getBody().getToken());
        verify(userService).login(request);
    }

    @Test
    void validateUserShouldDelegateToUserService() {
        ValidateTokenRequest request = ValidateTokenRequest.builder()
                .token("token")
                .user("user@test.com")
                .build();
        when(userService.validateUser("token", "user@test.com")).thenReturn(ResponseEntity.ok(true));

        ResponseEntity<Boolean> response = controller.validateUser(request);

        assertTrue(response.getBody());
        verify(userService).validateUser("token", "user@test.com");
    }
}
