package com.task_service.services.implementations;

import com.task_service.dtos.TaskRequestDto;
import com.task_service.dtos.TaskResponseDto;
import com.task_service.dtos.TaskResponseCardDto;
import com.task_service.dtos.TaskSummaryDto;
import com.task_service.dtos.ApiResponseDto;
import com.task_service.exceptions.BadRequestException;
import com.task_service.exceptions.ResourceNotFoundException;
import com.task_service.exceptions.TaskServiceException;
import com.task_service.models.TaskEntity;
import com.task_service.repositories.TaskRepository;
import com.task_service.services.interfaces.TaskCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class TaskCrudServiceImpl implements TaskCrudService {

  private final TaskRepository taskRepository;
  private final static String TASK_NOT_FOUND_MESSAGE = "Task not found with id: ";

  @Autowired
  public TaskCrudServiceImpl(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  // CRUD Operations

  @Override
  public ResponseEntity<TaskResponseDto> createTask(TaskRequestDto dto) {
    try {
      return ResponseEntity.status(HttpStatus.CREATED).body(createTaskLogic(dto));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getAllTasks() {
    try {
      return ResponseEntity.ok(getAllTasksLogic());
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> getTaskById(String id) {
    try {
      return ResponseEntity.ok(getTaskByIdLogic(id));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTask(String id, TaskRequestDto dto) {
    try {
      return ResponseEntity.ok(updateTaskLogic(id, dto));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ApiResponseDto> deleteTask(String id) {
    try {
      return ResponseEntity.ok(deleteTaskLogic(id));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ApiResponseDto> deleteTaskSet(List<String> ids) {
    try {
      return ResponseEntity.ok(deleteTaskSetLogic(ids));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  // Queries by criteria

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByStatus(String status) {
    try {
      return ResponseEntity.ok(getTasksByStatusLogic(status));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByPriority(String priority) {
    try {
      return ResponseEntity.ok(getTasksByPriorityLogic(priority));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByType(String type) {
    try {
      return ResponseEntity.ok(getTasksByTypeLogic(type));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(String projectId) {
    try {
      return ResponseEntity.ok(getTasksByProjectIdLogic(projectId));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getAllTasksByProjectsIds(List<String> projectsIds) {
    try {
      return ResponseEntity.ok(getAllTasksByProjectsIdsLogic(projectsIds));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseDto>> getTasksByAssigneeId(String assigneeId) {
    try {
      return ResponseEntity.ok(getTasksByAssigneeIdLogic(assigneeId));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<TaskResponseCardDto>> getTasksByProjectIdAndStatus(String projectId, String status) {
    try {
      return ResponseEntity.ok(getTasksByProjectIdAndStatusLogic(projectId, status));
    } catch (TaskServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  // Partial updates

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskStatus(String id, String status) {
    try {
      return ResponseEntity.ok(updateTaskStatusLogic(id, status));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskPriority(String id, String priority) {
    try {
      return ResponseEntity.ok(updateTaskPriorityLogic(id, priority));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskEffortPoints(String id, Integer effortPoints) {
    try {
      return ResponseEntity.ok(updateTaskEffortPointsLogic(id, effortPoints));
    } catch (TaskServiceException e) {
      if (e.getMessage().contains("Effort points must be between")) {
        throw new BadRequestException(e.getMessage());
      }
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskResponseDto> updateTaskBlocked(String id, Boolean blocked) {
    try {
      return ResponseEntity.ok(updateTaskBlockedLogic(id, blocked));
    } catch (TaskServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<TaskSummaryDto> getTaskSummary(String id) {
    try {
      return ResponseEntity.ok(getTaskSummaryLogic(id));
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

      TaskEntity entity = mapToEntity(dto);
      TaskEntity savedEntity = taskRepository.save(entity);
      return mapToResponseDto(savedEntity);
    } catch (Exception e) {
      throw new TaskServiceException("Error creating task: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getAllTasksLogic() throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findAll();
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks: " + e.getMessage());
    }
  }

  private TaskResponseDto getTaskByIdLogic(String id) throws TaskServiceException {
    try {
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      return mapToResponseDto(entity);
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving task: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskLogic(String id, TaskRequestDto dto) throws TaskServiceException {
    try {
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));

      entity.setTitle(dto.getTitle());
      entity.setDescription(dto.getDescription());
      entity.setType(dto.getType());
      entity.setStatus(dto.getStatus());
      entity.setProjectId(dto.getProjectId());
      entity.setAssigneeId(dto.getAssigneeId());
      entity.setParentTaskId(dto.getParentTaskId());
      entity.setDueDate(new Date(dto.getDueDate().getTime()));
      entity.setStartDate(dto.getStartDate() != null ? new Date(dto.getStartDate().getTime()) : null);
      entity.setPriority(dto.getPriority());
      entity.setEffortPoints(dto.getEffortPoints());
      entity.setBlocked(dto.getBlocked());

      TaskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (TaskServiceException e) {
      throw e;
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task: " + e.getMessage());
    }
  }

  private ApiResponseDto deleteTaskLogic(String id) throws TaskServiceException {
    try {
      taskRepository.findById(id)
        .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      taskRepository.deleteById(id);
      return ApiResponseDto.builder()
          .status(HttpStatus.OK.value())
          .message("Task deleted successfully")
          .timestamp(new Date())
          .build();
    } catch (Exception e) {
      throw new TaskServiceException("Error deleting task: " + e.getMessage());
    }
  }

  private ApiResponseDto deleteTaskSetLogic(List<String> ids) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findAllById(ids);
      if (tasks.size() != ids.size()) {
        throw new TaskServiceException("Error deleting, task not found");
      }
      taskRepository.deleteAllById(ids);
      return ApiResponseDto.builder()
          .status(HttpStatus.OK.value())
          .message("Task deleted successfully")
          .timestamp(new Date())
          .build();
    } catch (Exception e) {
      throw new TaskServiceException("Error deleting task: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByStatusLogic(String status) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByStatus(status);
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by status: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByPriorityLogic(String priority) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByPriority(priority);
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by priority: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByTypeLogic(String type) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByType(type);
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by type: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByProjectIdLogic(String projectId) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByProjectId(projectId);
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by project: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getAllTasksByProjectsIdsLogic(List<String> projectsIds) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByProjectIdIn(projectsIds);
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by project IDs: " + e.getMessage());
    }
  }

  private List<TaskResponseDto> getTasksByAssigneeIdLogic(String assigneeId) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByAssigneeId(assigneeId);
      return tasks.stream().map(this::mapToResponseDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by assignee: " + e.getMessage());
    }
  }

  private List<TaskResponseCardDto> getTasksByProjectIdAndStatusLogic(String projectId, String status) throws TaskServiceException {
    try {
      List<TaskEntity> tasks = taskRepository.findByProjectIdAndStatus(projectId, status);
      return tasks.stream().map(this::mapToResponseCardDto).toList();
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving tasks by project and status: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskStatusLogic(String id, String status) throws TaskServiceException {
    try {
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      entity.setStatus(status);
      TaskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task status: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskPriorityLogic(String id, String priority) throws TaskServiceException {
    try {
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      entity.setPriority(priority);
      TaskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task priority: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskEffortPointsLogic(String id, Integer effortPoints) throws TaskServiceException {
    try {
      if (effortPoints < 0 || effortPoints > 100) {
        throw new TaskServiceException("Effort points must be between 0 and 100");
      }
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      entity.setEffortPoints(effortPoints);
      TaskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task effort points: " + e.getMessage());
    }
  }

  private TaskResponseDto updateTaskBlockedLogic(String id, Boolean blocked) throws TaskServiceException {
    try {
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      entity.setBlocked(blocked);
      TaskEntity updatedEntity = taskRepository.save(entity);
      return mapToResponseDto(updatedEntity);
    } catch (Exception e) {
      throw new TaskServiceException("Error updating task blocked status: " + e.getMessage());
    }
  }

  private TaskSummaryDto getTaskSummaryLogic(String id) throws TaskServiceException {
    try {
      TaskEntity entity = taskRepository.findById(id)
          .orElseThrow(() -> new TaskServiceException(TASK_NOT_FOUND_MESSAGE + id));
      return mapToTaskSummaryDto(entity);
    } catch (Exception e) {
      throw new TaskServiceException("Error retrieving task summary: " + e.getMessage());
    }
  }

  // Private mapping methods

  private TaskEntity mapToEntity(TaskRequestDto dto) {
    return TaskEntity.builder()
        .title(dto.getTitle())
        .description(dto.getDescription())
        .type(dto.getType())
        .status(dto.getStatus())
        .projectId(dto.getProjectId())
        .assigneeId(dto.getAssigneeId())
        .parentTaskId(dto.getParentTaskId())
        .dueDate(new Date(dto.getDueDate().getTime()))
        .createdDate(dto.getCreatedDate() != null ? new Date(dto.getCreatedDate().getTime()) : new Date(System.currentTimeMillis()))
        .startDate(dto.getStartDate() != null ? new Date(dto.getStartDate().getTime()) : null)
        .priority(dto.getPriority())
        .effortPoints(dto.getEffortPoints() != null ? dto.getEffortPoints() : 0)
        .blocked(dto.getBlocked())
        .build();
  }

  private TaskResponseDto mapToResponseDto(TaskEntity entity) {
    return TaskResponseDto.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .type(entity.getType())
        .status(entity.getStatus())
        .projectId(entity.getProjectId())
        .assigneeId(entity.getAssigneeId())
        .parentTaskId(entity.getParentTaskId())
        .dueDate(entity.getDueDate())
        .createdDate(entity.getCreatedDate())
        .startDate(entity.getStartDate())
        .priority(entity.getPriority())
        .effortPoints(entity.getEffortPoints())
        .blocked(entity.getBlocked())
        .build();
  }

  private TaskResponseCardDto mapToResponseCardDto(TaskEntity entity) {
    return TaskResponseCardDto.builder()
        .id(entity.getId())
        .title(entity.getTitle())
        .description(entity.getDescription())
        .type(entity.getType())
        .status(entity.getStatus())
        .projectId(entity.getProjectId())
        .priority(entity.getPriority())
        .assigneeId(entity.getAssigneeId())
        .dueDate(entity.getDueDate())
        .effortPoints(entity.getEffortPoints())
        .blocked(entity.getBlocked())
        .parentTaskId(entity.getParentTaskId())
        .build();
  }

  private TaskSummaryDto mapToTaskSummaryDto(TaskEntity entity) {
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

}
