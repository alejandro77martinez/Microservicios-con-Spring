package com.task_service.dtos;

import org.junit.jupiter.api.Test;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TaskRequestDtoTest {

    private final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private final Validator validator = factory.getValidator();

    @Test
    void builder_ShouldCreateValidDto() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .assigneeId("user123")
                .dueDate(futureDate)
                .createdDate(new Date())
                .priority("High")
                .effortPoints(5)
                .blocked(false)
                .build();

        assertNotNull(dto);
        assertEquals("Valid Title", dto.getTitle());
        assertEquals("Valid Description", dto.getDescription());
        assertEquals("Feature", dto.getType());
        assertEquals("Pending", dto.getStatus());
        assertEquals("project123", dto.getProjectId());
        assertEquals("user123", dto.getAssigneeId());
        assertEquals("High", dto.getPriority());
        assertEquals(5, dto.getEffortPoints());
        assertFalse(dto.getBlocked());
    }

    @Test
    void validation_ShouldPassWithValidData() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Error")
                .status("En curso")
                .projectId("project123")
                .assigneeId("user123")
                .dueDate(futureDate)
                .createdDate(new Date())
                .priority("Alta")
                .effortPoints(5)
                .blocked(false)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertTrue(violations.isEmpty());
    }

    @Test
    void validation_ShouldFailWithBlankTitle() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title is required")));
    }

    @Test
    void validation_ShouldFailWithShortTitle() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("A")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title must be between 2 and 100")));
    }

    @Test
    void validation_ShouldFailWithLongTitle() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        String longTitle = "A".repeat(101);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title(longTitle)
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Title must be between 2 and 100")));
    }

    @Test
    void validation_ShouldFailWithBlankDescription() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description is required")));
    }

    @Test
    void validation_ShouldFailWithShortDescription() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("A")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description must be between 2 and 500")));
    }

    @Test
    void validation_ShouldFailWithLongDescription() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        String longDescription = "A".repeat(501);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description(longDescription)
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Description must be between 2 and 500")));
    }

    @Test
    void validation_ShouldFailWithBlankType() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Type is required")));
    }

    @Test
    void validation_ShouldFailWithInvalidType() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("InvalidType")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Type must be one of")));
    }

    @Test
    void validation_ShouldFailWithBlankStatus() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Status is required")));
    }

    @Test
    void validation_ShouldFailWithInvalidStatus() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("InvalidStatus")
                .projectId("project123")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Status must be one of")));
    }

    @Test
    void validation_ShouldFailWithBlankProjectId() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("")
                .dueDate(futureDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Project ID is required")));
    }

    @Test
    void validation_ShouldFailWithNullDueDate() {
        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(null)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Due date is required")));
    }

    @Test
    void validation_ShouldFailWithPastDueDate() {
        Date pastDate = new Date(System.currentTimeMillis() - 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(pastDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Due date must be in the future")));
    }

    @Test
    void validation_ShouldFailWithFutureCreatedDate() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        Date futureCreatedDate = new Date(System.currentTimeMillis() + 86400000 * 2);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .createdDate(futureCreatedDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Created date cannot be in the future")));
    }

    @Test
    void validation_ShouldFailWithFutureStartDate() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);
        Date futureStartDate = new Date(System.currentTimeMillis() + 86400000 * 2);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .startDate(futureStartDate)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Start date cannot be in the future")));
    }

    @Test
    void validation_ShouldFailWithBlankPriority() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .priority("")
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Priority is required")));
    }

    @Test
    void validation_ShouldFailWithInvalidPriority() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .priority("InvalidPriority")
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Priority must be one of")));
    }

    @Test
    void validation_ShouldFailWithNegativeEffortPoints() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .priority("High")
                .effortPoints(-1)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Effort points must be at least 0")));
    }

    @Test
    void validation_ShouldFailWithEffortPointsOver100() {
        Date futureDate = new Date(System.currentTimeMillis() + 86400000);

        TaskRequestDto dto = TaskRequestDto.builder()
                .title("Valid Title")
                .description("Valid Description")
                .type("Feature")
                .status("Pending")
                .projectId("project123")
                .dueDate(futureDate)
                .priority("High")
                .effortPoints(101)
                .build();

        Set<ConstraintViolation<TaskRequestDto>> violations = validator.validate(dto);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("Effort points must not exceed 100")));
    }
}