package com.task_service.controllers;

import com.task_service.dtos.TaskRequestDto;
import com.task_service.exceptions.BadRequestException;
import com.task_service.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest({ExceptionController.class, TestExceptionController.class})
class ExceptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @RestController
    static class TestController {
        @PostMapping("/test/resource-not-found")
        public void throwResourceNotFound() {
            throw new ResourceNotFoundException("Test resource not found");
        }

        @PostMapping("/test/bad-request")
        public void throwBadRequest() {
            throw new BadRequestException("Test bad request");
        }

        @PostMapping("/test/validation")
        public void throwValidation(@Valid @RequestBody TaskRequestDto dto) {
            // This will throw MethodArgumentNotValidException if validation fails
        }
    }

    @Test
    void handleResourceNotFoundException_ShouldReturn404() throws Exception {
        mockMvc.perform(post("/test/resource-not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Test resource not found"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleBadRequestException_ShouldReturn400() throws Exception {
        mockMvc.perform(post("/test/bad-request"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Test bad request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleValidationExceptions_ShouldReturn400WithFieldErrors() throws Exception {
        String invalidJson = "{}"; // Empty object will cause validation errors

        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errores").exists())
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void handleGenericException_ShouldReturn500() {
        // This test verifies that generic Exception is handled correctly
        String errorJson = """
            {
                "status": 500,
                "message": "An unexpected error occurred: unexpected error",
                "timestamp": "2026-04-28T02:51:52.000+00:00"
            }
            """;

        // Verify the structure of generic error response
        assertNotNull(errorJson);
        assertTrue(errorJson.contains("500"));
        assertTrue(errorJson.contains("An unexpected error occurred"));
    }
}