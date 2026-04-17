package com.auth_service.services.implementations;

import java.util.Optional;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.auth_service.exceptions.AuthServiceException;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ResourceNotFoundException;
import com.auth_service.models.UserEntity;
import com.auth_service.repositories.UserRepository;
import com.auth_service.services.interfaces.AuthService;
import com.auth_service.services.interfaces.JwtService;

@Service
public class AuthServiceImpl implements AuthService {

  @Value("${app.cookie.secure}")  // false por defecto en local
  private boolean secureCookie;
  @Value("${app.cookie.same-site}") // Lax por defecto en local
  private String sameSite;
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;
  private final UserRepository userRepository;
  private final String KEY_TOKEN = "AUTH_TOKEN";
  private final Logger logger = Logger.getLogger(getClass().getName());

  @Autowired
  public AuthServiceImpl(
    AuthenticationManager authenticationManager, 
    JwtService jwtService, 
    UserRepository userRepository) {
    this.authenticationManager = authenticationManager;
    this.jwtService = jwtService;
    this.userRepository = userRepository; 
  }

  @Override
  public ResponseEntity<String> login(LoginRequest user) {
    try {
      return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, authenticateAndGenerateCookieWithToken(user).toString())
        .body("Login successful");
    } catch (AuthServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  private ResponseCookie authenticateAndGenerateCookieWithToken(LoginRequest credentials) throws AuthServiceException {
    try {
      Authentication auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
          credentials.getEmail(),
          credentials.getPassword()
        )
      );
      String token = jwtService.generateToken((UserDetails) auth.getPrincipal());
      return ResponseCookie.from(KEY_TOKEN, token.isEmpty() ? "" : token)
        .httpOnly(true)
        .secure(secureCookie) // Set to true in production with HTTPS
        .path("/")
        .maxAge(600) // 10 minutes
        .sameSite(sameSite) // "None" si frontend y backend están en dominios diferentes
        .build();
    } catch (AuthenticationException e) {
      throw new AuthServiceException(e.getMessage());
    }
  }
  
  @Override
  public ResponseEntity<Boolean> validateUser(String token) {
    try {
      return ResponseEntity.ok(validate(token));
    } catch (AuthServiceException e) {
      logger.info("Token Invalido!!!");
      return ResponseEntity.ok(false);
    }
  }

  private Boolean validate(String token) throws AuthServiceException {
    Boolean isValid = jwtService.validateToken(token);
    if (Boolean.FALSE.equals(isValid)) {
      throw new AuthServiceException("Invalid token");
    }
    return isValid;
  }

  @Override
  public ResponseEntity<String> refreshToken(String token) {
    try {
      return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, refrehCookieToken(token).toString())
        .body("Refresh successful");
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  private ResponseCookie refrehCookieToken(String token) throws AuthServiceException{
    String newToken = jwtService.refreshToken(token);
    if (newToken.isEmpty()) {
      return deleteAccessTokenCookie();
    }
    return ResponseCookie.from(KEY_TOKEN, newToken)
      .httpOnly(true)
      .secure(secureCookie) // Set to true in production with HTTPS
      .path("/")
      .maxAge(600) // 10 minutes
      .sameSite(sameSite) // "None" si frontend y backend están en dominios diferentes
      .build();
  }

  @Override
  public ResponseEntity<UserResponse> getUserLogged(String token) {
    try {
      return ResponseEntity.ok(getUserLoggedFromToken(token));
    } catch (AuthServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  private UserResponse getUserLoggedFromToken(String token) throws AuthServiceException {
    String email = jwtService.getUserFromToken(token);
    Optional<UserEntity> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new AuthServiceException("User not found with email: " + email);
    }
    return UserResponse.builder()
      .id(user.get().getId())
      .name(user.get().getName())
      .lastName(user.get().getLastName())
      .email(user.get().getEmail())
      .avatar(user.get().getAvatar() != null && !user.get().getAvatar().isBlank() ? user.get().getAvatar() : "/user.png")
      .roles(user.get().getRoles())
      .build();
  }

  @Override
  public ResponseEntity<String> logout() {
    try {
      return ResponseEntity.ok()
      .header(HttpHeaders.SET_COOKIE, deleteAccessTokenCookie().toString())
      .body("Logout successful");
    } catch (AuthServiceException e) {
      throw new BadRequestException("Logout error");
    }
  }

  private ResponseCookie deleteAccessTokenCookie() throws AuthServiceException {
    try {
      SecurityContextHolder.clearContext();
      return ResponseCookie.from(KEY_TOKEN,"")
        .httpOnly(true)
        .secure(secureCookie) // Set to true in production with HTTPS
        .sameSite(sameSite) // "None" si frontend y backend están en dominios diferentes
        .path("/")
        .maxAge(0)
        .build();
    } catch (AuthenticationException e) {
      throw new AuthServiceException(e.getMessage());
    }
  }
}
