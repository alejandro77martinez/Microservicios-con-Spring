package com.auth_service.controllers;

import com.auth_service.dtos.ErrorResponse;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ForbiddenException;
import com.auth_service.exceptions.ResourceNotFoundException;
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
  public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
    return ResponseEntity
      .status(HttpStatus.NOT_FOUND)
      .body(ErrorResponse.builder()
      .status(404)
      .message(ex.getMessage())
      .timestamp(new Date())
      .build());
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {
    return ResponseEntity
      .status(HttpStatus.BAD_REQUEST)
      .body(ErrorResponse.builder()
      .status(400)
      .message(ex.getMessage())
      .timestamp(new Date())
      .build());
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex) {
    return ResponseEntity
      .status(HttpStatus.FORBIDDEN)
      .body(ErrorResponse.builder()
      .status(403)
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

  // Captura cualquier otra excepción no controlada
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ErrorResponse.builder()
      .status(500)
      .message(ex.getMessage())
      .timestamp(new Date())
      .build());
  }
}