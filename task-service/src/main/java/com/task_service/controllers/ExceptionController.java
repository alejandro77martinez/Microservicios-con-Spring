package com.task_service.controllers;

import com.task_service.dtos.ApiResponseDto;
import com.task_service.exceptions.BadRequestException;
import com.task_service.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponseDto> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiResponseDto response = ApiResponseDto.builder()
        .status(HttpStatus.NOT_FOUND.value())
        .message(ex.getMessage())
        .timestamp(new Date())
        .build();
    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponseDto> handleBadRequestException(BadRequestException ex) {
    ApiResponseDto response = ApiResponseDto.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message(ex.getMessage())
        .timestamp(new Date())
        .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("timestamp", new Date());
    
    Map<String, String> errores = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errores.put(error.getField(), error.getDefaultMessage())
    );
    response.put("errores", errores);
    
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
    ApiResponseDto response = ApiResponseDto.builder()
        .status(HttpStatus.BAD_REQUEST.value())
        .message("Validation error: " + ex.getMessage())
        .timestamp(new Date())
        .build();
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto> handleGenericException(Exception ex) {
    ApiResponseDto response = ApiResponseDto.builder()
        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .message("An unexpected error occurred: " + ex.getMessage())
        .timestamp(new Date())
        .build();
    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
