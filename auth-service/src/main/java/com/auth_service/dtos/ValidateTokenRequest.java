package com.auth_service.dtos;


import lombok.Data;
import lombok.Builder;

import jakarta.validation.constraints.NotBlank;

@Data
@Builder
public class ValidateTokenRequest {

  @NotBlank(message = "El token es obligatorio")
  private String token;

  @NotBlank(message = "El usuario es obligatorio")
  private String user;
}