package com.task_service.services.implementations;

import com.task_service.dtos.TaskRequestDto;
import com.task_service.dtos.TaskResponseDto;
import com.task_service.dtos.TaskResponseCardDto;
import com.task_service.dtos.TaskSummaryDto;
import com.task_service.dtos.ApiResponseDto;
import com.task_service.exceptions.BadRequestException;
import com.task_service.exceptions.ResourceNotFoundException;
import com.task_service.exceptions.TaskServiceException;
import com.task_service.models.taskEntity;
import com.task_service.repositories.TaskRepository;
import com.task_service.services.interfaces.TaskCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Service
public class TaskCrudServiceImpl implements TaskCrudService {

  @Autowired
  private TaskRepository taskRepository;

  // CRUD Operations

  @Override
  public ResponseEntity<TaskResponseDto> createTask(TaskRequestDto dto) {
    try {
      return new ResponseEntity<>(createTaskLogic(dto), HttpStatus.CREATED);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
    try {
      return new ResponseEntity<>(getAllTasksLogic(), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> getTaskById(String id) {
    try {
      return new ResponseEntity<>(getTaskByIdLogic(id), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTask(String id, TaskRequestDto dto) {
    try {
      return new ResponseEntity<>(updateTaskLogic(id, dto), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ApiResponseDto> deleteTask(String id) {
    try {
      deleteTaskLogic(id);
      ApiResponseDto response = ApiResponseDto.builder()
          .status(HttpStatus.OK.value())
          .message("Task deleted successfully")
          .timestamp(new Date())
          .build();
      return new ResponseEntity<>(response, HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  // Queries by criteria

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByStatus(String status) {
    try {
      return new ResponseEntity<>(getTasksByStatusLogic(status), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByPriority(String priority) {
    try {
      return new ResponseEntity<>(getTasksByPriorityLogic(priority), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByType(String type) {
    try {
      return new ResponseEntity<>(getTasksByTypeLogic(type), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(String projectId) {
    try {
      return new ResponseEntity<>(getTasksByProjectIdLogic(projectId), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByAssigneeId(String assigneeId) {
    try {
      return new ResponseEntity<>(getTasksByAssigneeIdLogic(assigneeId), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseCardDto>> getTasksByProjectIdAndStatus(String projectId, String status) {
    try {
      return new ResponseEntity<>(getTasksByProjectIdAndStatusLogic(projectId, status), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  // Partial updates

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskStatus(String id, String status) {
    try {
      return new ResponseEntity<>(updateTaskStatusLogic(id, status), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskPriority(String id, String priority) {
    try {
      return new ResponseEntity<>(updateTaskPriorityLogic(id, priority), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskEffortPoints(String id, Integer effortPoints) {
    try {
      return new ResponseEntity<>(updateTaskEffortPointsLogic(id, effortPoints), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskBlocked(String id, Boolean blocked) {
    try {
      return new ResponseEntity<>(updateTaskBlockedLogic(id, blocked), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskSummaryDto> getTaskSummary(String id) {
    try {
      return new ResponseEntity<>(getTaskSummaryLogic(id), HttpStatus.OK);
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  // Private logic methods

  private TaskResponseDto createTaskLogic(TaskRequestDto dto) throws TaskServiceException {
    try {
      if (dto.getTitle() == null || dto.getTitle().isBlank()) {
        throw new TaskServiceException("Task title cannot be empty");
      }

      if (dto.getProjectId() == null || dto.getProjectId().isBlank()) {
        throw new TaskServiceException("Project ID is required");
      }

      taskEntity entity = mapToEntity(dto);
      taskEntity savedEntity = taskRepository.save(entity);
      return mapToResponseDto(savedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error creating task: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getAllTasksLogic() throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findAll();
      return tasks.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks: " + e.getMessage());
    }
  }

  private TaskResponseDto getTaskByIdLogic(String id) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      return mapToResponseDto(entity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving task: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskLogic(String id, TaskRequestDto dto) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));

      entity.setTitle(dto.getTitle());
      entity.setDescription(dto.getDescription());
      entity.setType(dto.getType());
      entity.setStatus(dto.getStatus());
      entity.setProjectId(dto.getProjectId());
      entity.setAssigneeId(dto.getAssigneeId());
      entity.setSubTasks(dto.getSubTasks());
      entity.setDueDate(new java.sql.Date(dto.getDueDate().getTime()));
      entity.setStartDate(dto.getStartDate() != null ? new java.sql.Date(dto.getStartDate().getTime()) : null);
      entity.setPriority(dto.getPriority());
      entity.setEffortPoints(dto.getEffortPoints());
      entity.setBlocked(dto.getBlocked() != null ? dto.getBlocked() : false);

      taskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task: " + e.getMessage());
    }
  }

  private void deleteTaskLogic(String id) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      taskRepository.deleteById(id);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error deleting task: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByStatusLogic(String status) throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findByStatus(status);
      return tasks.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by status: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByPriorityLogic(String priority) throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findByPriority(priority);
      return tasks.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by priority: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByTypeLogic(String type) throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findByType(type);
      return tasks.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by type: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByProjectIdLogic(String projectId) throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findByProjectId(projectId);
      return tasks.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by project: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByAssigneeIdLogic(String assigneeId) throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findByAssigneeId(assigneeId);
      return tasks.stream().map(this::mapToResponseDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by assignee: " + e.getMessage());
    }
  }

  private List<TaskResponseCardDto> getTasksByProjectIdAndStatusLogic(String projectId, String status) throws TaskServiceException {
    try {
      List<taskEntity> tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
      return tasks.stream().map(this::mapToResponseCardDto).collect(Collectors.toList());
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by project and status: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskStatusLogic(String id, String status) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      entity.setStatus(status);
      taskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task status: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskPriorityLogic(String id, String priority) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      entity.setPriority(priority);
      taskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task priority: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskEffortPointsLogic(String id, Integer effortPoints) throws TaskServiceException {
    try {
      if (effortPoints < 0 || effortPoints > 100) {
        throw new TaskServiceException("Effort points must be between 0 and 100");
      }
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      entity.setEffortPoints(effortPoints);
      taskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task effort points: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskBlockedLogic(String id, Boolean blocked) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      entity.setBlocked(blocked != null ? blocked : false);
      taskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task blocked status: " + e.getMessage());
    }
  }

  private TaskSummaryDto getTaskSummaryLogic(String id) throws TaskServiceException {
    try {
      taskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException("Task not found with id: " + id));
      return mapToTaskSummaryDto(entity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving task summary: " + e.getMessage());
    }
  }

  // Private mapping methods

  private taskEntity mapToEntity(TaskRequestDto dto) {
    return taskEntity.builder()
        .title(dto.getTitle())
        .description(dto.getDescription())
        .type(dto.getType())
        .status(dto.getStatus())
        .projectId(dto.getProjectId())
        .assigneeId(dto.getAssigneeId())
        .subTasks(dto.getSubTasks())
        .dueDate(new java.sql.Date(dto.getDueDate().getTime()))
        .createdDate(dto.getCreatedDate() != null ? new java.sql.Date(dto.getCreatedDate().getTime()) : new java.sql.Date(System.currentTimeMillis()))
        .startDate(dto.getStartDate() != null ? new java.sql.Date(dto.getStartDate().getTime()) : null)
        .priority(dto.getPriority())
        .effortPoints(dto.getEffortPoints() != null ? dto.getEffortPoints() : 0)
        .blocked(dto.getBlocked() != null ? dto.getBlocked() : false)
        .build();
  }

  private TaskResponseDto mapToResponseDto(taskEntity entity) {
    return TaskResponseDto.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .type(entity.getType())
        .status(entity.getStatus())
        .projectId(entity.getProjectId())
        .assigneeId(entity.getAssigneeId())
        .subTasks(entity.getSubTasks())
        .dueDate(formatDate(entity.getDueDate()))
        .createdDate(formatDate(entity.getCreatedDate()))
        .startDate(formatDate(entity.getStartDate()))
        .priority(entity.getPriority())
        .effortPoints(entity.getEffortPoints())
        .blocked(entity.getBlocked())
        .build();
  }

  private TaskResponseCardDto mapToResponseCardDto(taskEntity entity) {
    return TaskResponseCardDto.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .type(entity.getType())
        .status(entity.getStatus())
        .projectId(entity.getProjectId())
        .priority(entity.getPriority())
        .assigneeId(entity.getAssigneeId())
        .dueDate(formatDate(entity.getDueDate()))
        .effortPoints(entity.getEffortPoints())
        .blocked(entity.getBlocked())
        .subTasks(entity.getSubTasks())
        .build();
  }

  private TaskSummaryDto mapToTaskSummaryDto(taskEntity entity) {
    return TaskSummaryDto.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .projectId(entity.getProjectId())
        .status(entity.getStatus())
        .priority(entity.getPriority())
        .type(entity.getType())
        .effortPoints(entity.getEffortPoints())
        .blocked(entity.getBlocked())
        .build();
  }

  private String formatDate(java.util.Date date) {
    if (date == null) {
      return null;
    }
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
    return sdf.format(date);
  }
}
