package com.auth_service.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class RegisterRequest {
    
    private String name;
    private String lastName;
    private String password;
    private String email;
    private List<String> roles;
}
