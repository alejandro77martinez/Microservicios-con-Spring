package com.project_service.controllers;

import com.project_service.dtos.ApiResponseDto;
import com.project_service.exceptions.BadRequestException;
import com.project_service.exceptions.ResourceNotFoundException;

import jakarta.validation.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ExceptionController {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ApiResponseDto> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ApiResponseDto.builder()
      .status(404)
      .message(ex.getMessage())
      .timestamp(new Date())
      .build());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ApiResponseDto> handleBadRequest(BadRequestException ex) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponseDto.builder()
      .status(400)
      .message(ex.getMessage())
      .timestamp(new Date())
      .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidationErrors(
        MethodArgumentNotValidException ex) {

    Map<String, String> errores = new HashMap<>();

    ex.getBindingResult()
      .getFieldErrors()
      .forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));

    Map<String, Object> response = new HashMap<>();
    response.put("status", 400);
    response.put("errores", errores);
    response.put("timestamp", LocalDateTime.now().toString());

    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponseDto> handleConstraintViolationException(ConstraintViolationException ex) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ApiResponseDto.builder()
      .status(400)
      .message(ex.getMessage())
      .timestamp(new Date())
      .build());
  }

  // Captura cualquier otra excepción no controlada
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponseDto> handleGeneric(Exception ex) {
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponseDto.builder()
      .status(500)
      .message("Error de backend: "+ ex.getMessage())
      .timestamp(new Date())
      .build());
  }
}