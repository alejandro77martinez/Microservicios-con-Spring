package com.project_service.dtos;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class RoleUserDto {

  private String userId;
  private String role;
}
