package com.auth_service.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@Builder
public class RegisterRequest {
    
  @NotBlank(message = "El nombre es obligatorio")
  private String name;
    
  @NotBlank(message = "El apellido es obligatorio")
  private String lastName;
    
  @NotBlank(message = "El email es obligatorio")
  @Email(message = "El email no tiene un formato válido")
  private String email;
    
  @NotBlank(message = "La contraseña es obligatoria")
  @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
  private String password;

  private List<String> roles;
}
