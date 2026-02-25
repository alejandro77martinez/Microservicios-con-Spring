package com.auth_service.dtos;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class UserResponse {
    
    private String name;
    private String lastName;
    private String email;
    private List<String> roles;
}