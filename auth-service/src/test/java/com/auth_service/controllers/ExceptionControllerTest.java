package com.auth_service.controllers;

import com.auth_service.dtos.ErrorResponse;
import com.auth_service.exceptions.BadRequestException;
import com.auth_service.exceptions.ForbiddenException;
import com.auth_service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TestBean {
  private String email;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }
}

class ExceptionControllerTest {

  private final ExceptionController controller = new ExceptionController();

  @Test
  void handleNotFoundShouldReturn404() {
    ResourceNotFoundException ex = new ResourceNotFoundException("missing");

    ResponseEntity<ErrorResponse> response = controller.handleNotFound(ex);

    assertEquals(404, response.getStatusCode().value());
    assertEquals("missing", response.getBody().getMessage());
  }

  @Test
  void handleBadRequestShouldReturn400() {
    BadRequestException ex = new BadRequestException("bad");

    ResponseEntity<ErrorResponse> response = controller.handleBadRequest(ex);

    assertEquals(400, response.getStatusCode().value());
    assertEquals("bad", response.getBody().getMessage());
  }

  @Test
  void handleForbiddenShouldReturn403() {
    ForbiddenException ex = new ForbiddenException("forbidden");

    ResponseEntity<ErrorResponse> response = controller.handleForbidden(ex);

    assertEquals(403, response.getStatusCode().value());
    assertEquals("forbidden", response.getBody().getMessage());
  }

  @Test
  void handleGenericShouldReturn500() {
    ResponseEntity<ErrorResponse> response = controller.handleGeneric(new RuntimeException("boom"));

    assertEquals(500, response.getStatusCode().value());
    assertEquals("Error de backend: boom", response.getBody().getMessage());
  }

  @Test
  void handleValidationErrorsShouldReturnMap() {
    BindingResult bindingResult = new BeanPropertyBindingResult(new TestBean(), "object");
    bindingResult.rejectValue("email", "NotBlank", "Email is required");
    MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);

    ResponseEntity<Map<String, Object>> response = controller.handleValidationErrors(ex);

    assertEquals(400, response.getStatusCode().value());
    assertNotNull(response.getBody());
    assertEquals("Email is required", ((Map<String, String>) response.getBody().get("errores")).get("email"));
  }
}
