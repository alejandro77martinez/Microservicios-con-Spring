package com.auth_service.controllers;

import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.services.interfaces.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private UserController controller;

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
  void healthCheckShouldDelegateToUserService() {
    UserResponse user = UserResponse.builder().email("user@test.com").build();
    when(userService.findAll()).thenReturn(ResponseEntity.ok(List.of(user)));

    ResponseEntity<List<UserResponse>> response = controller.healthCheck();

    assertEquals(200, response.getStatusCode().value());
    assertEquals(1, response.getBody().size());
    assertEquals("user@test.com", response.getBody().get(0).getEmail());
    verify(userService).findAll();
  }

  @Test
  void getUserByIdShouldDelegateToUserService() {
    UserResponse user = UserResponse.builder().email("user@test.com").build();
    when(userService.findById("123")).thenReturn(ResponseEntity.ok(user));

    ResponseEntity<UserResponse> response = controller.getUserById("123");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("user@test.com", response.getBody().getEmail());
    verify(userService).findById("123");
  }
}
