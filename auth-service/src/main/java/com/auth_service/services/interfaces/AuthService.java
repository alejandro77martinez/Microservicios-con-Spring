package com.auth_service.services.interfaces;

import org.springframework.http.ResponseEntity;

import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.UserResponse;

public interface AuthService {

  ResponseEntity<String> login(LoginRequest user);
  ResponseEntity<Boolean> validateUser(String token);
  ResponseEntity<String> refreshToken(String token);
  ResponseEntity<UserResponse> getUserLogged(String token);
  ResponseEntity<String> logout();
}
