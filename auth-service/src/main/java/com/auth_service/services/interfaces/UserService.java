package com.auth_service.services.interfaces;

import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.AuthResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {
    ResponseEntity<UserResponse> create(RegisterRequest user);
    ResponseEntity<AuthResponse> login(LoginRequest user);
    ResponseEntity<List<UserResponse>> findAll();
    ResponseEntity<UserResponse> findById(String id);
    ResponseEntity<UserResponse> findByEmail(String email);
    ResponseEntity<UserResponse> update(String id, RegisterRequest user);
    ResponseEntity<String> deleteById(String id);
    ResponseEntity<Boolean> validateUser(String token, String email);
}
