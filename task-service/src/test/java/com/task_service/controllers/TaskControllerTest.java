package com.task_service.controllers;

import com.task_service.dtos.*;
import com.task_service.services.interfaces.TaskCrudService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TaskControllerTest {

  @Mock
  private TaskCrudService taskCrudService;

  @InjectMocks
  private TaskController taskController;

  private MockMvc mockMvc;

  private TaskResponseDto taskResponseDto;
  private TaskResponseCardDto taskResponseCardDto;
  private TaskSummaryDto taskSummaryDto;
  private ApiResponseDto apiResponseDto;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(taskController).build();

    taskResponseDto = TaskResponseDto.builder()
        .id("task123")
        .title("Test Task")
        .description("Test Description")
        .type("Feature")
        .status("Pending")
        .projectId("project123")
        .assigneeId("user123")
        .dueDate(new Date())
        .createdDate(new Date())
        .priority("High")
        .effortPoints(5)
        .blocked(false)
        .build();

    taskResponseCardDto = TaskResponseCardDto.builder()
        .id("task123")
        .title("Test Task")
        .description("Test Description")
        .type("Feature")
        .status("Pending")
        .projectId("project123")
        .priority("High")
        .assigneeId("user123")
        .dueDate(new Date())
        .effortPoints(5)
        .blocked(false)
        .build();

    taskSummaryDto = TaskSummaryDto.builder()
        .id("task123")
        .title("Test Task")
        .projectId("project123")
        .status("Pending")
        .priority("High")
        .type("Feature")
        .effortPoints(5)
        .blocked(false)
        .build();

    apiResponseDto = ApiResponseDto.builder()
        .status(200)
        .message("Task deleted successfully")
        .timestamp(new Date())
        .build();
  }

  @Test
  void createTask_ShouldReturnCreatedTask() throws Exception {
    when(taskCrudService.createTask(any(TaskRequestDto.class)))
        .thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(taskResponseDto));

    mockMvc.perform(post("/")
        .contentType("application/json")
        .content(
            "{\"title\":\"Test Task\",\"description\":\"Test Description\",\"type\":\"Feature\",\"status\":\"Pending\",\"projectId\":\"project123\",\"assigneeId\":\"user123\",\"dueDate\":\"2050-01-01T00:00:00.000Z\",\"createdDate\":\"2026-04-27T02:51:52.000Z\",\"priority\":\"High\",\"effortPoints\":5,\"blocked\":false}"))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value("task123"))
        .andExpect(jsonPath("$.title").value("Test Task"));
  }

  @Test
  void getAllTasks_ShouldReturnTaskList() throws Exception {
    List<TaskResponseDto> taskList = Arrays.asList(taskResponseDto);
    when(taskCrudService.getAllTasks())
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("task123"))
        .andExpect(jsonPath("$[0].title").value("Test Task"));
  }

  @Test
  void getTaskById_ShouldReturnTask() throws Exception {
    when(taskCrudService.getTaskById("task123"))
        .thenReturn(ResponseEntity.ok(taskResponseDto));

    mockMvc.perform(get("/task123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"))
        .andExpect(jsonPath("$.title").value("Test Task"));
  }

  @Test
  void updateTask_ShouldReturnUpdatedTask() throws Exception {
    when(taskCrudService.updateTask(eq("task123"), any(TaskRequestDto.class)))
        .thenReturn(ResponseEntity.ok(taskResponseDto));

    mockMvc.perform(put("/task123")
        .contentType("application/json")
        .content(
            "{\"title\":\"Updated Task\",\"description\":\"Updated Description\",\"type\":\"Feature\",\"status\":\"In Progress\",\"projectId\":\"project123\",\"assigneeId\":\"user123\",\"dueDate\":\"2050-01-01T00:00:00.000Z\",\"createdDate\":\"2026-04-27T02:51:52.000Z\",\"priority\":\"High\",\"effortPoints\":8,\"blocked\":false}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"));
  }

  @Test
  void deleteTask_ShouldReturnSuccessResponse() throws Exception {
    when(taskCrudService.deleteTask("task123"))
        .thenReturn(ResponseEntity.ok(apiResponseDto));

    mockMvc.perform(delete("/task123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.message").value("Task deleted successfully"));
  }

  @Test
  void getTasksByStatus_ShouldReturnFilteredTasks() throws Exception {
    List<TaskResponseDto> taskList = Arrays.asList(taskResponseDto);
    when(taskCrudService.getTasksByStatus("Pending"))
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/status/Pending"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].status").value("Pending"));
  }

  @Test
  void getTasksByPriority_ShouldReturnFilteredTasks() throws Exception {
    List<TaskResponseDto> taskList = Arrays.asList(taskResponseDto);
    when(taskCrudService.getTasksByPriority("High"))
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/priority/High"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].priority").value("High"));
  }

  @Test
  void getTasksByType_ShouldReturnFilteredTasks() throws Exception {
    List<TaskResponseDto> taskList = Arrays.asList(taskResponseDto);
    when(taskCrudService.getTasksByType("Feature"))
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/type/Feature"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].type").value("Feature"));
  }

  @Test
  void getTasksByProjectId_ShouldReturnFilteredTasks() throws Exception {
    List<TaskResponseDto> taskList = Arrays.asList(taskResponseDto);
    when(taskCrudService.getTasksByProjectId("project123"))
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/project/project123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].projectId").value("project123"));
  }

  @Test
  void getTasksByAssigneeId_ShouldReturnFilteredTasks() throws Exception {
    List<TaskResponseDto> taskList = Arrays.asList(taskResponseDto);
    when(taskCrudService.getTasksByAssigneeId("user123"))
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/assignee/user123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].assigneeId").value("user123"));
  }

  @Test
  void getTasksByProjectIdAndStatus_ShouldReturnFilteredTasks() throws Exception {
    List<TaskResponseCardDto> taskList = Arrays.asList(taskResponseCardDto);
    when(taskCrudService.getTasksByProjectIdAndStatus("project123", "Pending"))
        .thenReturn(ResponseEntity.ok(taskList));

    mockMvc.perform(get("/project/project123/status/Pending"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("task123"));
  }

  @Test
  void updateTaskStatus_ShouldReturnUpdatedTask() throws Exception {
    when(taskCrudService.updateTaskStatus("task123", "In Progress"))
        .thenReturn(ResponseEntity.ok(taskResponseDto));

    mockMvc.perform(put("/task123/status/In Progress"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"));
  }

  @Test
  void updateTaskPriority_ShouldReturnUpdatedTask() throws Exception {
    when(taskCrudService.updateTaskPriority("task123", "Medium"))
        .thenReturn(ResponseEntity.ok(taskResponseDto));

    mockMvc.perform(put("/task123/priority/Medium"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"));
  }

  @Test
  void updateTaskEffortPoints_ShouldReturnUpdatedTask() throws Exception {
    when(taskCrudService.updateTaskEffortPoints("task123", 10))
        .thenReturn(ResponseEntity.ok(taskResponseDto));

    mockMvc.perform(put("/task123/effortPoints/10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"));
  }

  @Test
  void updateTaskBlocked_ShouldReturnUpdatedTask() throws Exception {
    when(taskCrudService.updateTaskBlocked("task123", true))
        .thenReturn(ResponseEntity.ok(taskResponseDto));

    mockMvc.perform(put("/task123/blocked/true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"));
  }

  @Test
  void getTaskSummary_ShouldReturnTaskSummary() throws Exception {
    when(taskCrudService.getTaskSummary("task123"))
        .thenReturn(ResponseEntity.ok(taskSummaryDto));

    mockMvc.perform(get("/task123/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("task123"))
        .andExpect(jsonPath("$.title").value("Test Task"));
  }

  @Test
  void createTask_WithInvalidData_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(post("/")
        .contentType("application/json")
        .content("""
                {
                    "title": "",
                    "description": "Test Description",
                    "type": "Feature",
                    "status": "Pending",
                    "projectId": "project123",
                    "dueDate": "2026-04-29T02:51:52.000Z"
                }
            """))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTaskById_WithBlankId_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/ "))
        .andExpect(status().isBadRequest());
  }

  @Test
  void getTasksByStatus_WithInvalidStatus_ShouldReturnBadRequest() throws Exception {
    mockMvc.perform(get("/status/InvalidStatus"))
        .andExpect(status().isBadRequest());
  }
}