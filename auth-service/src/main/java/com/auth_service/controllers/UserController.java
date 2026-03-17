package com.auth_service.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.services.interfaces.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/user")
public class UserController {
  
  @Autowired
  private UserService userService;

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest user) {
    return userService.create(user);
  }

  @GetMapping("/all")
  public ResponseEntity<List<UserResponse>> healthCheck() {
    return userService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable String id) {
    return userService.findById(id);
  }
}
