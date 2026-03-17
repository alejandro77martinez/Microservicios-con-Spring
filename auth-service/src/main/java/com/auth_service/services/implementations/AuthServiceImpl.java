package com.auth_service.services.implementations;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;

import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ResourceNotFoundException;
import com.auth_service.exceptions.UserServiceException;
import com.auth_service.models.UserEntity;
import com.auth_service.repositories.UserRepository;
import com.auth_service.services.interfaces.AuthService;
import com.auth_service.services.interfaces.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

  @Autowired
  private AuthenticationManager authenticationManager;
  @Autowired
  private JwtService jwtService;
  @Autowired
  private UserRepository userRepository;

  @Override
  public ResponseEntity<String> login(LoginRequest user) {
    try {
      return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, authenticateAndGenerateCookieWithToken(user).toString())
        .body("Login successful");
    } catch (UserServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  private ResponseCookie authenticateAndGenerateCookieWithToken(LoginRequest credentials) throws UserServiceException {
    try {
      Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          credentials.getEmail(),
          credentials.getPassword()
        )
      );
      String token = jwtService.generateToken((UserDetails) auth.getPrincipal());
      return ResponseCookie.from("AUTH_TOKEN", token.isEmpty() ? "" : token)
        .httpOnly(true)
        .secure(false) // Set to true in production with HTTPS
        .path("/")
        .maxAge(10 * 60) // 10 minutes
        .sameSite("Lax") // "None" si frontend y backend están en dominios diferentes
        .build();
    } catch (AuthenticationException e) {
      throw new UserServiceException(e.getMessage());
    }
  }
  
  @Override
  public ResponseEntity<Boolean> validateUser(String token) {
    try {
      return ResponseEntity.ok(validate(token));
    } catch (UserServiceException e) {
      System.out.println("Token Invalido!!!");
      return ResponseEntity.ok(false);
    }
  }

  private Boolean validate(String token) throws UserServiceException {
    Boolean isValid = jwtService.validateToken(token);
    if (!isValid) {
      throw new UserServiceException("Invalid token");
    }
    return isValid;
  }

  @Override
  public ResponseEntity<String> refreshToken(String token) {
    String newToken = jwtService.refreshToken(token);
    if (newToken.isEmpty()) {
      throw new BadRequestException("Invalid or expired token");
    }
    return ResponseEntity.ok(newToken);
  }

  @Override
  public ResponseEntity<UserResponse> getUserLogged(String token) {
    try {
      return ResponseEntity.ok(getUserLoggedFromToken(token));
    } catch (UserServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  private UserResponse getUserLoggedFromToken(String token) throws UserServiceException {
    String email = jwtService.getUserFromToken(token);
    Optional<UserEntity> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new UserServiceException("User not found with email: " + email);
    }
    return UserResponse.builder()
      .name(user.get().getName())
      .lastName(user.get().getLastName())
      .email(user.get().getEmail())
      .roles(user.get().getRoles())
      .build();
  }

  @Override
  public ResponseEntity<String> logout() {
    try {
      return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, deleteAccessTokenCookie().toString())
      .body("Logout successful");
    } catch (UserServiceException e) {
      throw new BadRequestException("Logout error");
    }
  }

  private ResponseCookie deleteAccessTokenCookie() throws UserServiceException {
    try {
      SecurityContextHolder.clearContext();
      return ResponseCookie.from("AUTH_TOKEN","")
        .httpOnly(true)
        .path("/")
        .maxAge(0)
        .build();
    } catch (AuthenticationException e) {
      throw new UserServiceException(e.getMessage());
    }
  }
}
