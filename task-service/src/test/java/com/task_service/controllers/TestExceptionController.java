package com.task_service.controllers;

import com.task_service.dtos.TaskRequestDto;
import com.task_service.exceptions.BadRequestException;
import com.task_service.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestExceptionController {

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