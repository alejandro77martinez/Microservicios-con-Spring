package com.auth_service.controllers;

import com.auth_service.dtos.AuthResponse;
import com.auth_service.dtos.ErrorResponse;
import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.dtos.ValidateTokenRequest;
import com.auth_service.services.interfaces.UserService;
import com.auth_service.exceptions.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@RequestBody RegisterRequest user) {
        return userService.create(user);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest credentials) {
        return userService.login(credentials);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserResponse>> healthCheck() {
        return userService.findAll();
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> getUserByEmail(@RequestBody String email) {
        return userService.findByEmail(email);
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateUser(@RequestBody ValidateTokenRequest request) {
        return userService.validateUser(request.getToken(), request.getUser());
    }
}