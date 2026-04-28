package com.task_service.services;

import com.task_service.dtos.*;
import com.task_service.exceptions.BadRequestException;
import com.task_service.exceptions.ResourceNotFoundException;
import com.task_service.models.TaskEntity;
import com.task_service.repositories.TaskRepository;
import com.task_service.services.implementations.TaskCrudServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskCrudServiceImplTest {

  @Mock
  private TaskRepository taskRepository;

  @InjectMocks
  private TaskCrudServiceImpl taskCrudService;

  private TaskRequestDto taskRequestDto;
  private TaskEntity taskEntity;

  @BeforeEach
  void setUp() {
    Date futureDate = new Date(System.currentTimeMillis() + 86400000); // +1 day
    Date pastDate = new Date(System.currentTimeMillis() - 86400000); // -1 day

    taskRequestDto = TaskRequestDto.builder()
        .title("Test Task")
        .description("Test Description")
        .type("Feature")
        .status("Pending")
        .projectId("project123")
        .assigneeId("user123")
        .dueDate(futureDate)
        .createdDate(pastDate)
        .priority("High")
        .effortPoints(5)
        .blocked(false)
        .build();

    taskEntity = TaskEntity.builder()
        .id("task123")
        .title("Test Task")
        .description("Test Description")
        .type("Feature")
        .status("Pending")
        .projectId("project123")
        .assigneeId("user123")
        .dueDate(new java.sql.Date(futureDate.getTime()))
        .createdDate(new java.sql.Date(pastDate.getTime()))
        .priority("High")
        .effortPoints(5)
        .blocked(false)
        .build();
  }

  @Test
  void createTask_ShouldReturnCreatedTask() {
    when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

    ResponseEntity<TaskResponseDto> response = taskCrudService.createTask(taskRequestDto);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    assertEquals("Test Task", response.getBody().getTitle());
    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  @Test
  void createTask_WithNullTitle_ShouldThrowBadRequestException() {
    TaskRequestDto invalidDto = TaskRequestDto.builder()
        .title(null)
        .description("Test Description")
        .type("Feature")
        .status("Pending")
        .projectId("project123")
        .dueDate(new Date())
        .build();

    assertThrows(BadRequestException.class, () -> taskCrudService.createTask(invalidDto));
  }

  @Test
  void createTask_WithNullProjectId_ShouldThrowBadRequestException() {
    TaskRequestDto invalidDto = TaskRequestDto.builder()
        .title("Test Task")
        .description("Test Description")
        .type("Feature")
        .status("Pending")
        .projectId(null)
        .dueDate(new Date())
        .build();

    assertThrows(BadRequestException.class, () -> taskCrudService.createTask(invalidDto));
  }

  @Test
  void getAllTasks_ShouldReturnTaskList() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findAll()).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseDto>> response = taskCrudService.getAllTasks();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("task123", response.getBody().get(0).getId());
    verify(taskRepository, times(1)).findAll();
  }

  @Test
  void getAllTasks_WithRepositoryException_ShouldThrowBadRequestException() {
    when(taskRepository.findAll()).thenThrow(new RuntimeException("Database error"));

    assertThrows(BadRequestException.class, () -> taskCrudService.getAllTasks());
    verify(taskRepository, times(1)).findAll();
  }

  @Test
  void getTaskById_ShouldReturnTask() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));

    ResponseEntity<TaskResponseDto> response = taskCrudService.getTaskById("task123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    verify(taskRepository, times(1)).findById("task123");
  }

  @Test
  void getTaskById_WithNonExistentId_ShouldThrowResourceNotFoundException() {
    when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskCrudService.getTaskById("nonexistent"));
    verify(taskRepository, times(1)).findById("nonexistent");
  }

  @Test
  void updateTask_ShouldReturnUpdatedTask() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));
    when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

    ResponseEntity<TaskResponseDto> response = taskCrudService.updateTask("task123", taskRequestDto);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    verify(taskRepository, times(1)).findById("task123");
    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  @Test
  void updateTask_WithNonExistentId_ShouldThrowResourceNotFoundException() {
    when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskCrudService.updateTask("nonexistent", taskRequestDto));
    verify(taskRepository, times(1)).findById("nonexistent");
  }

  @Test
  void deleteTask_ShouldReturnSuccessResponse() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));

    ResponseEntity<ApiResponseDto> response = taskCrudService.deleteTask("task123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(200, response.getBody().getStatus());
    assertEquals("Task deleted successfully", response.getBody().getMessage());
    verify(taskRepository, times(1)).findById("task123");
    verify(taskRepository, times(1)).deleteById("task123");
  }

  @Test
  void deleteTask_WithNonExistentId_ShouldThrowResourceNotFoundException() {
    when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskCrudService.deleteTask("nonexistent"));
    verify(taskRepository, times(1)).findById("nonexistent");
  }

  @Test
  void getTasksByStatus_ShouldReturnFilteredTasks() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findByStatus("Pending")).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseDto>> response = taskCrudService.getTasksByStatus("Pending");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("Pending", response.getBody().get(0).getStatus());
    verify(taskRepository, times(1)).findByStatus("Pending");
  }

  @Test
  void getTasksByStatus_WithRepositoryException_ShouldThrowBadRequestException() {
    when(taskRepository.findByStatus("Pending")).thenThrow(new RuntimeException("Database error"));

    assertThrows(BadRequestException.class, () -> taskCrudService.getTasksByStatus("Pending"));
    verify(taskRepository, times(1)).findByStatus("Pending");
  }

  @Test
  void getTasksByPriority_ShouldReturnFilteredTasks() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findByPriority("High")).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseDto>> response = taskCrudService.getTasksByPriority("High");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("High", response.getBody().get(0).getPriority());
    verify(taskRepository, times(1)).findByPriority("High");
  }

  @Test
  void getTasksByPriority_WithRepositoryException_ShouldThrowBadRequestException() {
    when(taskRepository.findByPriority("High")).thenThrow(new RuntimeException("Database error"));

    assertThrows(BadRequestException.class, () -> taskCrudService.getTasksByPriority("High"));
    verify(taskRepository, times(1)).findByPriority("High");
  }

  @Test
  void getTasksByType_ShouldReturnFilteredTasks() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findByType("Feature")).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseDto>> response = taskCrudService.getTasksByType("Feature");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("Feature", response.getBody().get(0).getType());
    verify(taskRepository, times(1)).findByType("Feature");
  }

  @Test
  void getTasksByProjectId_ShouldReturnFilteredTasks() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findByProjectId("project123")).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseDto>> response = taskCrudService.getTasksByProjectId("project123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("project123", response.getBody().get(0).getProjectId());
    verify(taskRepository, times(1)).findByProjectId("project123");
  }

  @Test
  void getTasksByAssigneeId_ShouldReturnFilteredTasks() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findByAssigneeId("user123")).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseDto>> response = taskCrudService.getTasksByAssigneeId("user123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("user123", response.getBody().get(0).getAssigneeId());
    verify(taskRepository, times(1)).findByAssigneeId("user123");
  }

  @Test
  void getTasksByProjectIdAndStatus_ShouldReturnFilteredTasks() {
    List<TaskEntity> taskEntities = Arrays.asList(taskEntity);
    when(taskRepository.findByProjectIdAndStatus("project123", "Pending")).thenReturn(taskEntities);

    ResponseEntity<List<TaskResponseCardDto>> response = taskCrudService.getTasksByProjectIdAndStatus("project123",
        "Pending");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals(1, response.getBody().size());
    assertEquals("task123", response.getBody().get(0).getId());
    verify(taskRepository, times(1)).findByProjectIdAndStatus("project123", "Pending");
  }

  @Test
  void updateTaskStatus_ShouldReturnUpdatedTask() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));
    when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

    ResponseEntity<TaskResponseDto> response = taskCrudService.updateTaskStatus("task123", "In Progress");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    verify(taskRepository, times(1)).findById("task123");
    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  @Test
  void updateTaskStatus_WithNonExistentId_ShouldThrowResourceNotFoundException() {
    when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskCrudService.updateTaskStatus("nonexistent", "In Progress"));
    verify(taskRepository, times(1)).findById("nonexistent");
  }

  @Test
  void updateTaskPriority_ShouldReturnUpdatedTask() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));
    when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

    ResponseEntity<TaskResponseDto> response = taskCrudService.updateTaskPriority("task123", "Medium");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    verify(taskRepository, times(1)).findById("task123");
    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  @Test
  void updateTaskEffortPoints_ShouldReturnUpdatedTask() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));
    when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

    ResponseEntity<TaskResponseDto> response = taskCrudService.updateTaskEffortPoints("task123", 10);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    verify(taskRepository, times(1)).findById("task123");
    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  @Test
  void updateTaskEffortPoints_WithInvalidValue_ShouldThrowBadRequestException() {
    assertThrows(BadRequestException.class, () -> taskCrudService.updateTaskEffortPoints("task123", 150)); // > 100
  }

  @Test
  void updateTaskBlocked_ShouldReturnUpdatedTask() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));
    when(taskRepository.save(any(TaskEntity.class))).thenReturn(taskEntity);

    ResponseEntity<TaskResponseDto> response = taskCrudService.updateTaskBlocked("task123", true);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    verify(taskRepository, times(1)).findById("task123");
    verify(taskRepository, times(1)).save(any(TaskEntity.class));
  }

  @Test
  void getTaskSummary_ShouldReturnTaskSummary() {
    when(taskRepository.findById("task123")).thenReturn(Optional.of(taskEntity));

    ResponseEntity<TaskSummaryDto> response = taskCrudService.getTaskSummary("task123");

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertNotNull(response.getBody());
    assertEquals("task123", response.getBody().getId());
    assertEquals("Test Task", response.getBody().getTitle());
    verify(taskRepository, times(1)).findById("task123");
  }

  @Test
  void getTaskSummary_WithNonExistentId_ShouldThrowResourceNotFoundException() {
    when(taskRepository.findById("nonexistent")).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> taskCrudService.getTaskSummary("nonexistent"));
    verify(taskRepository, times(1)).findById("nonexistent");
  }
}
