package com.task_service.controllers;

import com.task_service.dtos.TaskRequestDto;
import com.task_service.dtos.TaskResponseDto;
import com.task_service.dtos.TaskResponseCardDto;
import com.task_service.dtos.TaskSummaryDto;
import com.task_service.dtos.ApiResponseDto;
import com.task_service.services.interfaces.TaskCrudService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

  @Autowired
  private TaskCrudService taskCrudService;

  // CRUD Operations

  @PostMapping
  public ResponseEntity<TaskResponseDto> createTask(@Valid @RequestBody TaskRequestDto dto) {
    return taskCrudService.createTask(dto);
  }

  @GetMapping
  public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
    return taskCrudService.getAllTasks();
  }

  @GetMapping("/{taskId}")
  public ResponseEntity<TaskResponseDto> getTaskById(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId) {
    return taskCrudService.getTaskById(taskId);
  }

  @PutMapping("/{taskId}")
  public ResponseEntity<TaskResponseDto> updateTask(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId,
      @Valid @RequestBody TaskRequestDto dto) {
    return taskCrudService.updateTask(taskId, dto);
  }

  @DeleteMapping("/{taskId}")
  public ResponseEntity<ApiResponseDto> deleteTask(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId) {
    return taskCrudService.deleteTask(taskId);
  }

  // Queries by criteria

  @GetMapping("/status/{status}")
  public ResponseEntity<List<TaskResponseDto>> getTasksByStatus(
      @PathVariable @NotBlank(message = "Status cannot be blank")
      @Pattern(regexp = "Pending|In Progress|Completed|Blocked", 
        message = "Status must be one of: Pending, In Progress, Completed, Blocked") String status) {
    return taskCrudService.getTasksByStatus(status);
  }

  @GetMapping("/priority/{priority}")
  public ResponseEntity<List<TaskResponseDto>> getTasksByPriority(
      @PathVariable @NotBlank(message = "Priority cannot be blank")
      @Pattern(regexp = "High|Medium|Low", 
        message = "Priority must be one of: High, Medium, Low") String priority) {
    return taskCrudService.getTasksByPriority(priority);
  }

  @GetMapping("/type/{type}")
  public ResponseEntity<List<TaskResponseDto>> getTasksByType(
      @PathVariable @NotBlank(message = "Type cannot be blank")
      @Pattern(regexp = "Bug|Feature|Enhancement|Documentation", 
        message = "Type must be one of: Bug, Feature, Enhancement, Documentation") String type) {
    return taskCrudService.getTasksByType(type);
  }

  @GetMapping("/project/{projectId}")
  public ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(
      @PathVariable @NotBlank(message = "Project ID cannot be blank") String projectId) {
    return taskCrudService.getTasksByProjectId(projectId);
  }

  @GetMapping("/assignee/{assigneeId}")
  public ResponseEntity<List<TaskResponseDto>> getTasksByAssigneeId(
      @PathVariable @NotBlank(message = "Assignee ID cannot be blank") String assigneeId) {
    return taskCrudService.getTasksByAssigneeId(assigneeId);
  }

  @GetMapping("/project/{projectId}/status/{status}")
  public ResponseEntity<List<TaskResponseCardDto>> getTasksByProjectIdAndStatus(
      @PathVariable @NotBlank(message = "Project ID cannot be blank") String projectId,
      @PathVariable @NotBlank(message = "Status cannot be blank")
      @Pattern(regexp = "Pending|In Progress|Completed|Blocked", 
        message = "Status must be one of: Pending, In Progress, Completed, Blocked") String status) {
    return taskCrudService.getTasksByProjectIdAndStatus(projectId, status);
  }

  // Partial updates

  @PutMapping("/{taskId}/status/{status}")
  public ResponseEntity<TaskResponseDto> updateTaskStatus(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId,
      @PathVariable @NotBlank(message = "Status cannot be blank")
      @Pattern(regexp = "Pending|In Progress|Completed|Blocked", 
        message = "Status must be one of: Pending, In Progress, Completed, Blocked") String status) {
    return taskCrudService.updateTaskStatus(taskId, status);
  }

  @PutMapping("/{taskId}/priority/{priority}")
  public ResponseEntity<TaskResponseDto> updateTaskPriority(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId,
      @PathVariable @NotBlank(message = "Priority cannot be blank")
      @Pattern(regexp = "High|Medium|Low", 
        message = "Priority must be one of: High, Medium, Low") String priority) {
    return taskCrudService.updateTaskPriority(taskId, priority);
  }

  @PutMapping("/{taskId}/effortPoints/{effortPoints}")
  public ResponseEntity<TaskResponseDto> updateTaskEffortPoints(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId,
      @PathVariable @Min(value = 0, message = "Effort points must be at least 0")
      @Max(value = 100, message = "Effort points must not exceed 100") Integer effortPoints) {
    return taskCrudService.updateTaskEffortPoints(taskId, effortPoints);
  }

  @PutMapping("/{taskId}/blocked/{blocked}")
  public ResponseEntity<TaskResponseDto> updateTaskBlocked(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId,
      @PathVariable Boolean blocked) {
    return taskCrudService.updateTaskBlocked(taskId, blocked);
  }

  // Summary view

  @GetMapping("/{taskId}/summary")
  public ResponseEntity<TaskSummaryDto> getTaskSummary(
      @PathVariable @NotBlank(message = "Task ID cannot be blank") String taskId) {
    return taskCrudService.getTaskSummary(taskId);
  }
}
