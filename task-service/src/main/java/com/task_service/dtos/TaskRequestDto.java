package com.task_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Future;

import java.util.Date;
import java.util.List;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TaskRequestDto {

  @NotBlank(message = "Title is required")
  @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
  private String title;

  @NotBlank(message = "Description is required")
  @Size(min = 2, max = 500, message = "Description must be between 2 and 500 characters")
  private String description;

  @NotBlank(message = "Type is required")
  @Pattern(regexp = "Error|Funcionalidad|Mejora|Documentación", 
    message = "Type must be one of: Epica, Tarea, Sub Tarea")
  private String type;

  @NotBlank(message = "Status is required")
  @Pattern(regexp = "Creada|En curso|En revision|Completada", 
    message = "Status must be one of: Created, Pending, In Progress, Completed, Blocked")
  private String status;

  @NotBlank(message = "Project ID is required")
  private String projectId;

  private String assigneeId;

  private List<String> subTasks;

  @NotNull(message = "Due date is required")
  @Future(message = "Due date must be in the future")
  private Date dueDate;

  @PastOrPresent(message = "Created date cannot be in the future")
  private Date createdDate;

  @PastOrPresent(message = "Start date cannot be in the future")
  private Date startDate;

  @NotBlank(message = "Priority is required")
  @Pattern(regexp = "Alta|Media|Baja", 
    message = "Priority must be one of: Alta, Media, Baja")
  private String priority;

  @Min(value = 0, message = "Effort points must be at least 0")
  @Max(value = 100, message = "Effort points must not exceed 100")
  private Integer effortPoints;

  private Boolean blocked;
}
