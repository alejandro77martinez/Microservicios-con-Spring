package com.auth_service.dtos;

import lombok.Data;
import lombok.Builder;
import java.util.List;

@Data
@Builder
public class ValidateTokenRequest {
    private String token;
    private String user;
}