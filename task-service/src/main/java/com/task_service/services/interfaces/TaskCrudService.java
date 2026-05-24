package com.task_service.services.interfaces;

import com.task_service.dtos.TaskRequestDto;
import com.task_service.dtos.TaskResponseDto;
import com.task_service.dtos.TaskResponseCardDto;
import com.task_service.dtos.TaskSummaryDto;
import com.task_service.dtos.ApiResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TaskCrudService {
  // CRUD Operations
  ResponseEntity<TaskResponseDto> createTask(TaskRequestDto dto);
  
  ResponseEntity<List<TaskResponseDto>> getAllTasks();
  
  ResponseEntity<TaskResponseDto> getTaskById(String id);
  
  ResponseEntity<TaskResponseDto> updateTask(String id, TaskRequestDto dto);
  
  ResponseEntity<ApiResponseDto> deleteTask(String id);

  ResponseEntity<ApiResponseDto> deleteTaskSet(List<String> ids);
  
  // Queries by criteria
  ResponseEntity<List<TaskResponseDto>> getTasksByStatus(String status);
  
  ResponseEntity<List<TaskResponseDto>> getTasksByPriority(String priority);
  
  ResponseEntity<List<TaskResponseDto>> getTasksByType(String type);
  
  ResponseEntity<List<TaskResponseDto>> getTasksByProjectId(String projectId);

  ResponseEntity<List<TaskResponseDto>> getAllTasksByProjectsIds(List<String> projectsIds);
  
  ResponseEntity<List<TaskResponseDto>> getTasksByAssigneeId(String assigneeId);
  
  ResponseEntity<List<TaskResponseCardDto>> getTasksByProjectIdAndStatus(String projectId, String status);
  
  // Partial updates
  ResponseEntity<TaskResponseDto> updateTaskStatus(String id, String status);
  
  ResponseEntity<TaskResponseDto> updateTaskPriority(String id, String priority);
  
  ResponseEntity<TaskResponseDto> updateTaskEffortPoints(String id, Integer effortPoints);
  
  ResponseEntity<TaskResponseDto> updateTaskBlocked(String id, Boolean blocked);
  
  // Summary view
  ResponseEntity<TaskSummaryDto> getTaskSummary(String id);
}
