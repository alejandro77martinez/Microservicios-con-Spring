package com.auth_service.services.implementations;

import com.auth_service.dtos.LoginRequest;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtService jwtService;

  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private AuthServiceImpl authService;

  @Test
  void loginShouldSetHttpOnlyCookieAndReturnSuccessMessage() {
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

    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenReturn(authentication);
    when(jwtService.generateToken(principal)).thenReturn("jwt-token");

    ResponseEntity<String> response = authService.login(request);

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Login successful", response.getBody());
    String setCookie = response.getHeaders().getFirst("Set-Cookie");
    assertTrue(setCookie.contains("AUTH_TOKEN=jwt-token"));
    assertTrue(setCookie.contains("HttpOnly"));
  }

  @Test
  void loginShouldThrowBadRequestWhenAuthenticationFails() {
    LoginRequest request = LoginRequest.builder().email("user@test.com").password("wrong").build();
    when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
      .thenThrow(new BadCredentialsException("Bad credentials"));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> authService.login(request));

    assertEquals("Bad credentials", exception.getMessage());
  }

  @Test
  void validateUserShouldReturnTrueWhenTokenIsValid() {
    when(jwtService.validateToken("token")).thenReturn(true);

    ResponseEntity<Boolean> response = authService.validateUser("token");

    assertTrue(response.getBody());
  }

  @Test
  void validateUserShouldReturnFalseWhenTokenIsInvalid() {
    when(jwtService.validateToken("bad-token")).thenReturn(false);

    ResponseEntity<Boolean> response = authService.validateUser("bad-token");

    assertEquals(200, response.getStatusCode().value());
    assertEquals(false, response.getBody());
  }

  @Test
  void refreshTokenShouldReturnNewTokenWhenValid() {
    when(jwtService.refreshToken("token")).thenReturn("new-token");

    ResponseEntity<String> response = authService.refreshToken("token");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Refresh successful", response.getBody());
  }

  @Test
  void getUserLoggedShouldReturnUserFromToken() {
    when(jwtService.getUserFromToken("token")).thenReturn("user@test.com");
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(UserEntity.builder()
      .name("User")
      .lastName("Test")
      .email("user@test.com")
      .roles(List.of("USER"))
      .build()));

    ResponseEntity<UserResponse> response = authService.getUserLogged("token");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("user@test.com", response.getBody().getEmail());
  }

  @Test
  void getUserLoggedShouldThrowResourceNotFoundWhenUserMissing() {
    when(jwtService.getUserFromToken("token")).thenReturn("missing@test.com");
    when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

    ResourceNotFoundException exception = assertThrows(
      ResourceNotFoundException.class,
      () -> authService.getUserLogged("token")
    );

    assertEquals("User not found with email: missing@test.com", exception.getMessage());
  }

  @Test
  void refreshTokenShouldReturnDeleteCookieWhenRefreshTokenIsEmpty() {
    when(jwtService.refreshToken("token")).thenReturn("");

    ResponseEntity<String> response = authService.refreshToken("token");

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Refresh successful", response.getBody());
    String setCookie = response.getHeaders().getFirst("Set-Cookie");
    assertTrue(setCookie.contains("Max-Age=0"));
  }

  @Test
  void logoutShouldClearContextAndReturnEmptyCookie() {
    ResponseEntity<String> response = authService.logout();

    assertEquals(200, response.getStatusCode().value());
    assertEquals("Logout successful", response.getBody());
    String setCookie = response.getHeaders().getFirst("Set-Cookie");
    assertTrue(setCookie.contains("Max-Age=0"));
    assertTrue(setCookie.contains("AUTH_TOKEN="));
  }

  @Test
  void getUserLoggedShouldUseDefaultAvatarWhenUserHasNoAvatar() {
    when(jwtService.getUserFromToken("token")).thenReturn("user@test.com");
    when(userRepository.findByEmail("user@test.com")).thenReturn(Optional.of(UserEntity.builder()
      .id("123")
      .name("User")
      .lastName("Test")
      .email("user@test.com")
      .roles(List.of("USER"))
      .avatar("   ")
      .build()));

    ResponseEntity<UserResponse> response = authService.getUserLogged("token");

    assertEquals("/user.png", response.getBody().getAvatar());
  }
}
