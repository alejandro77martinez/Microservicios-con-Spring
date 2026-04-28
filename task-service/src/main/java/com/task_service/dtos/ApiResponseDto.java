package com.task_service.dtos;

import lombok.Data;
import lombok.Builder;

import java.util.Date;

@Data
@Builder
public class ApiResponseDto {
  private Integer status;
  private String message;
  private Date timestamp;
}
