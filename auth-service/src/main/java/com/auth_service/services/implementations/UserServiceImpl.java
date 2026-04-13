package com.auth_service.services.implementations;

import com.auth_service.models.UserEntity;
import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserEmailResponse;
import com.auth_service.dtos.UserResponse;

import com.auth_service.exceptions.UserServiceException;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ResourceNotFoundException;
import com.auth_service.repositories.UserRepository;
import com.auth_service.services.interfaces.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserServiceImpl (
    UserRepository userRepository,
    PasswordEncoder passwordEncoder){
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public ResponseEntity<UserResponse> create(RegisterRequest user) {
    try {
      return ResponseEntity.status(201).body(saveUser(user));
    } catch (UserServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  private UserResponse saveUser(RegisterRequest user) throws UserServiceException {
    if (userRepository.findByEmail(user.getEmail()).isPresent()) {
      throw new UserServiceException("Email already in use");
    }
    UserEntity newUser = UserEntity.builder()
      .name(user.getName())
      .lastName(user.getLastName())
      .email(user.getEmail())
      .password(passwordEncoder.encode(user.getPassword()))
      .roles(user.getRoles() == null || user.getRoles().isEmpty() ? List.of("USER") : user.getRoles())
      .build();
    UserEntity savedUser = userRepository.save(newUser);
    return mapToUserResponse(savedUser);
  }

  @Override
  public ResponseEntity<List<UserResponse>> findAll() {
    List<UserResponse> userResponses = mapToUserResponseList();
    if (userResponses.isEmpty()) {
      throw new ResourceNotFoundException("No users found");
    }
    return ResponseEntity.ok(userResponses);
  }

  private List<UserResponse> mapToUserResponseList() {
    List<UserEntity> users = userRepository.findAll();
    return users.stream().map(this::mapToUserResponse).toList();
  }

  @Override
  public ResponseEntity<UserResponse> findById(String id) {
    try {
      return ResponseEntity.ok(getById(id));
    } catch (UserServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  private UserResponse getById(String id) throws UserServiceException {
    Optional<UserEntity> user = userRepository.findById(id);
    if (user.isEmpty()) {
      throw new UserServiceException("User not found with id: " + id);
    }
    return mapToUserResponse(user.get());
  }

  @Override
  public ResponseEntity<UserResponse> findByEmail(String email) {
    try {
      return ResponseEntity.ok(getByEmail(email));
    } catch (UserServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  private UserResponse getByEmail(String email) throws UserServiceException {
    Optional<UserEntity> user = userRepository.findByEmail(email);
    if (user.isEmpty()) {
      throw new UserServiceException("User not found with email: " + email);
    }
    return mapToUserResponse(user.get());
  }

  @Override
  public ResponseEntity<UserResponse> update(String id, RegisterRequest user) {
    // Implementation to update a user by ID
    return null; // Placeholder
  }

  @Override
  public ResponseEntity<String> deleteById(String id) {
    // Implementation to delete a user by ID
    return null; // Placeholder
  }

  @Override
  public ResponseEntity<Boolean> existUserName(String name) {
    if (name == null){
      throw new BadRequestException("Sin user name");
    }
    Optional<UserEntity> user = userRepository.findByEmail(name);
    return ResponseEntity.ok(!user.isEmpty());
  }

  @Override
  public ResponseEntity<List<UserEmailResponse>> searchUsersByEmail(String email) {
    try {
      return ResponseEntity.ok(searchUsersByEmailLogic(email));
    } catch (UserServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  private List<UserEmailResponse> searchUsersByEmailLogic(String email) throws UserServiceException {
    List<UserEntity> users = userRepository.findByEmailContainingIgnoreCase(email);
    if (users.isEmpty()) {
      throw new UserServiceException("No users found with email containing: " + email);
    }
    return mapToUserEmailResponseList(users);
  }

  private List<UserEmailResponse> mapToUserEmailResponseList(List<UserEntity> users) {
    return users.stream().map(user -> UserEmailResponse.builder()
      .id(user.getId())
      .email(user.getEmail())
      .name(user.getName() + " " + user.getLastName())
      .avatar(user.getAvatar() != null && !user.getAvatar().isBlank() ? user.getAvatar() : "/user.png")
      .build()).toList();
  }

  private UserResponse mapToUserResponse(UserEntity user) {
    return UserResponse.builder()
      .id(user.getId())
      .name(user.getName())
      .lastName(user.getLastName())
      .email(user.getEmail())
      .avatar(user.getAvatar() != null && !user.getAvatar().isBlank() ? user.getAvatar() : "/user.png")
      .roles(user.getRoles())
      .build();
  }
}