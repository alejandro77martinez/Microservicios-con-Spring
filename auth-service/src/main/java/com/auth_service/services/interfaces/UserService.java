package com.auth_service.services.interfaces;

import com.auth_service.dtos.RegisterRequest;
import com.auth_service.dtos.UserEmailResponse;
import com.auth_service.dtos.UserResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface UserService {

  ResponseEntity<UserResponse> create(RegisterRequest user);
  ResponseEntity<List<UserResponse>> findAll();
  ResponseEntity<UserResponse> findById(String id);
  ResponseEntity<UserResponse> findByEmail(String email);
  ResponseEntity<UserResponse> update(String id, RegisterRequest user);
  ResponseEntity<String> deleteById(String id); 
  ResponseEntity<Boolean> existUserName(String name);
  ResponseEntity<List<UserEmailResponse>> searchUsersByEmail(String email);
  ResponseEntity<List<UserEmailResponse>> searchUsersByTeamIds(List<String> teamIds);
}
