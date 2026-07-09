package com.auth_service.controllers;

import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.dtos.ValidateTokenRequest;
import com.auth_service.services.interfaces.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  @Mock
  private AuthService userService;

  @InjectMocks
  private AuthController controller;

  @Test
  void loginShouldDelegateToUserService() {
    LoginRequest request = LoginRequest.builder().email("user@test.com").password("secret").build();
    when(userService.login(request)).thenReturn(ResponseEntity.ok("Login successful"));

    ResponseEntity<String> response = controller.login(request);

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Login successful", response.getBody());
    verify(userService).login(request);
  }

  @Test
  void validateUserShouldDelegateToUserService() {
    ValidateTokenRequest request = ValidateTokenRequest.builder()
      .token("token")
      .build();
    when(userService.validateUser("token")).thenReturn(ResponseEntity.ok(true));

    ResponseEntity<Boolean> response = controller.validateUser(request);

    assertTrue(response.getBody());
    verify(userService).validateUser("token");
  }

  @Test
  void refreshTokenShouldDelegateToUserService() {
    ValidateTokenRequest mockToken = ValidateTokenRequest.builder()
      .token("token")
      .build(); 
    when(userService.refreshToken("token")).thenReturn(ResponseEntity.ok("new-token"));

    ResponseEntity<String> response = controller.refreshToken(mockToken);

    assertEquals(200, response.getStatusCode().value());
    assertEquals("new-token", response.getBody());
    verify(userService).refreshToken("token");
  }

  @Test
  void getUserSessionShouldDelegateToUserService() {
    ValidateTokenRequest request = ValidateTokenRequest.builder().token("token").build();
    UserResponse user = UserResponse.builder().email("user@test.com").build();
    when(userService.getUserLogged("token")).thenReturn(ResponseEntity.ok(user));

    ResponseEntity<UserResponse> response = controller.getUserSession(request);

    assertEquals(200, response.getStatusCode().value());
    assertEquals("user@test.com", response.getBody().getEmail());
    verify(userService).getUserLogged("token");
  }

  @Test
  void logoutShouldDelegateToUserService() {
    when(userService.logout()).thenReturn(ResponseEntity.ok("Logout successful"));

    ResponseEntity<String> response = controller.logout();

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Logout successful", response.getBody());
    verify(userService).logout();
  }
}
