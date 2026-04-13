package com.auth_service.dtos;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserEmailResponse {
  
  private String id;
  private String email;
  private String name;
  private String avatar;
}
