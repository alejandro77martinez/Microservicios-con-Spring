package com.auth_service.services.implementations;

import com.auth_service.models.UserEntity;
import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserResponse;
import com.auth_service.dtos.LoginRequest;
import com.auth_service.dtos.AuthResponse;

import com.auth_service.exceptions.UserServiceException;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ResourceNotFoundException;
import com.auth_service.repositories.UserRepository;
import com.auth_service.services.interfaces.UserService;
import com.auth_service.services.interfaces.JwtService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

@Service
class UserServiceImpl implements UserService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Override
    public ResponseEntity<UserResponse> create(RegisterRequest user) {
        try {
            return ResponseEntity.created(null).body(saveUser(user));
        } catch (UserServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private UserResponse saveUser(RegisterRequest user) throws UserServiceException {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserServiceException("Email already in use");
        }
        UserEntity savedUser = userRepository.save(UserEntity.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .roles(user.getRoles() == null || user.getRoles().isEmpty() ? List.of("USER") : user.getRoles())
                .build());        
        return UserResponse.builder()
                .name(savedUser.getName())
                .lastName(savedUser.getLastName())
                .email(savedUser.getEmail())
                .roles(savedUser.getRoles())
                .build();
    }

    @Override
    public ResponseEntity<AuthResponse> login(LoginRequest user){
        try {
            return ResponseEntity.ok(authenticateAndGenerateToken(user));
        } catch (UserServiceException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private AuthResponse authenticateAndGenerateToken(LoginRequest credentials) throws UserServiceException {
        try {
            Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    credentials.getEmail(), 
                    credentials.getPassword()
                )
            );
            AuthResponse authResponse = AuthResponse.builder()
                    .token(jwtService.generateToken((UserDetails) auth.getPrincipal()))
                    .user(credentials.getEmail())
                    .roles(userRepository.findByEmail(credentials.getEmail())
                            .map(UserEntity::getRoles)
                            .orElse(List.of("USER")))
                    .build();
            return authResponse;
        } catch (AuthenticationException e) {
            throw new UserServiceException(e.getMessage());
        }
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
        return users.stream().map(user -> UserResponse.builder()
                .name(user.getName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .roles(user.getRoles())
                .build()).toList();
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
        UserResponse userResponse = UserResponse.builder()
                .name(user.get().getName())
                .lastName(user.get().getLastName())
                .email(user.get().getEmail())
                .roles(user.get().getRoles())
                .build();
        return userResponse;
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
        UserResponse userResponse = UserResponse.builder()
                .name(user.get().getName())
                .lastName(user.get().getLastName())
                .email(user.get().getEmail())
                .roles(user.get().getRoles())
                .build();
        return userResponse;
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
    public ResponseEntity<Boolean> validateUser(String token, String email) {
        try {
            return ResponseEntity.ok(validate(token, email));
        } catch (UserServiceException e) {
            throw new BadRequestException(e.getMessage()); 
        }
    }

    private Boolean validate(String token, String user) throws UserServiceException {
        Boolean isValid = jwtService.validateToken(token, user);
        if (!isValid) {
            throw new UserServiceException("Invalid token");
        }
        return isValid;
    }
}       