package com.auth_service.dtos;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class AuthResponse {
    private String token;
    private String user;
    private List<String> roles;
}
