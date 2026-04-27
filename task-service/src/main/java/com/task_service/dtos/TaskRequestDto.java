package com.task_service.dtos;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskRequestDto {
  @NotBlank(message = "Title is required")
  @Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
  private String title;

  @NotBlank(message = "Description is required")
  @Size(min = 2, max = 500, message = "Description must be between 2 and 500 characters")
  private String description;

  @NotBlank(message = "Type is required")
  @Pattern(regexp = "Bug|Feature|Enhancement|Documentation", 
    message = "Type must be one of: Bug, Feature, Enhancement, Documentation")
  private String type;

  @NotBlank(message = "Status is required")
  @Pattern(regexp = "Pending|In Progress|Completed|Blocked", 
    message = "Status must be one of: Pending, In Progress, Completed, Blocked")
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
  @Pattern(regexp = "High|Medium|Low", 
    message = "Priority must be one of: High, Medium, Low")
  private String priority;

  @Min(value = 0, message = "Effort points must be at least 0")
  @Max(value = 100, message = "Effort points must not exceed 100")
  private Integer effortPoints;

  private Boolean blocked;
}
