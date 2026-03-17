package com.auth_service.controllers;

import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.dtos.ValidateTokenRequest;
import com.auth_service.services.interfaces.AuthService;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

  @Autowired
  private AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<String> login(@Valid @RequestBody LoginRequest credentials) {
    return authService.login(credentials);
  }

  @PostMapping("/validate")
  public ResponseEntity<Boolean> validateUser(@Valid @RequestBody ValidateTokenRequest request) {
    return authService.validateUser(request.getToken());
  }

  @PostMapping("/refresh")
  public ResponseEntity<String> refreshToken(@RequestBody String token) {
    return authService.refreshToken(token);
  }

  @PostMapping("/session")
  public ResponseEntity<UserResponse> getUserSession(@RequestBody String token) {
    return authService.getUserLogged(token);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(){
    return authService.logout();
  }
}