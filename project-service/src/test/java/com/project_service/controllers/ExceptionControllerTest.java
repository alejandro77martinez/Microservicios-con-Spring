package com.project_service.controllers;

import com.project_service.dtos.ApiResponseDto;
import com.project_service.dtos.ProjectRequestDto;
import com.project_service.exceptions.BadRequestException;
import com.project_service.exceptions.ResourceNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ExceptionControllerTest {

  private final ExceptionController exceptionController = new ExceptionController();

  @Test
  void handleNotFound_returnsNotFoundResponse() {
    ResponseEntity<ApiResponseDto> response =
        exceptionController.handleNotFound(new ResourceNotFoundException("Proyecto no encontrado"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(404);
    assertThat(response.getBody().getMessage()).isEqualTo("Proyecto no encontrado");
    assertThat(response.getBody().getTimestamp()).isNotNull();
  }

  @Test
  void handleBadRequest_returnsBadRequestResponse() {
    ResponseEntity<ApiResponseDto> response =
        exceptionController.handleBadRequest(new BadRequestException("Solicitud invalida"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).isEqualTo("Solicitud invalida");
    assertThat(response.getBody().getTimestamp()).isNotNull();
  }

  @Test
  void handleValidationErrors_returnsFieldErrorsMap() throws NoSuchMethodException {
    MethodArgumentNotValidException exception = buildValidationException();

    ResponseEntity<Map<String, Object>> response =
        exceptionController.handleValidationErrors(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody()).containsEntry("status", 400);
    assertThat(response.getBody()).containsKey("timestamp");

    @SuppressWarnings("unchecked")
    Map<String, String> errors = (Map<String, String>) response.getBody().get("errores");

    assertThat(errors)
        .containsEntry("name", "Project name is required")
        .containsEntry("priority", "Priority must be Alta, Media, or Baja");
  }

  @Test
  void handleConstraintViolationException_returnsBadRequestResponse() {
    ConstraintViolationException exception =
        new ConstraintViolationException("Violacion de restriccion", Set.of());

    ResponseEntity<ApiResponseDto> response =
        exceptionController.handleConstraintViolationException(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(400);
    assertThat(response.getBody().getMessage()).isEqualTo("Violacion de restriccion");
    assertThat(response.getBody().getTimestamp()).isNotNull();
  }

  @Test
  void handleGeneric_returnsInternalServerErrorResponse() {
    ResponseEntity<ApiResponseDto> response =
        exceptionController.handleGeneric(new RuntimeException("fallo inesperado"));

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(500);
    assertThat(response.getBody().getMessage()).isEqualTo("Error de backend: fallo inesperado");
    assertThat(response.getBody().getTimestamp()).isNotNull();
  }

  private MethodArgumentNotValidException buildValidationException() throws NoSuchMethodException {
    ProjectRequestDto request = ProjectRequestDto.builder().build();
    BeanPropertyBindingResult bindingResult =
        new BeanPropertyBindingResult(request, "projectRequestDto");

    bindingResult.addError(
        new FieldError("projectRequestDto", "name", "Project name is required"));
    bindingResult.addError(
        new FieldError(
            "projectRequestDto",
            "priority",
            "Priority must be Alta, Media, or Baja"));

    Method method =
        ValidationTarget.class.getDeclaredMethod("validate", ProjectRequestDto.class);
    MethodParameter parameter = new MethodParameter(method, 0);

    return new MethodArgumentNotValidException(parameter, bindingResult);
  }

  private static final class ValidationTarget {
    private void validate(ProjectRequestDto projectRequestDto) {
    }
  }
}
