package com.auth_service.dtos;

import lombok.Data;
import lombok.Builder;
import java.util.Date;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private Date timestamp;
}