package com.project_service.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project_service.dtos.ApiResponseDto;
import com.project_service.dtos.ProjectRequestDto;
import com.project_service.dtos.ProjectResponseDto;
import com.project_service.dtos.ProjectSummaryDto;
import com.project_service.dtos.UserRoleDto;
import com.project_service.services.interfaces.ProjectCrudService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockitoBean
  private ProjectCrudService projectCrudService;

  @Test
  void createProject_invokesService() throws Exception {
    ProjectRequestDto request = buildProjectRequestDto();
    ProjectResponseDto responseDto = buildProjectResponseDto();

    when(projectCrudService.createProject(any(ProjectRequestDto.class)))
        .thenReturn(ResponseEntity.ok(responseDto));

    mockMvc.perform(post("/")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseDto.getId()));
  }

  @Test
  void getAllProjects_invokesService() throws Exception {
    ProjectResponseDto responseDto = buildProjectResponseDto();
    when(projectCrudService.getAllProjects())
        .thenReturn(ResponseEntity.ok(List.of(responseDto)));

    mockMvc.perform(get("/"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(responseDto.getId()));
  }

  @Test
  void getProjectById_invokesService() throws Exception {
    ProjectResponseDto responseDto = buildProjectResponseDto();
    when(projectCrudService.getProjectById("id-123"))
        .thenReturn(ResponseEntity.ok(responseDto));

    mockMvc.perform(get("/id-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value(responseDto.getName()));
  }

  @Test
  void getProjectsByClient_invokesService() throws Exception {
    ProjectResponseDto responseDto = buildProjectResponseDto();
    when(projectCrudService.getProjectsByClient("Cliente A"))
        .thenReturn(ResponseEntity.ok(List.of(responseDto)));

    mockMvc.perform(get("/client/{clientName}", "Cliente A"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].client").value(responseDto.getClient()));
  }

  @Test
  void getProjectsByPriority_invokesService() throws Exception {
    ProjectResponseDto responseDto = buildProjectResponseDto();
    when(projectCrudService.getProjectsByPriority("Alta"))
        .thenReturn(ResponseEntity.ok(List.of(responseDto)));

    mockMvc.perform(get("/priority/Alta"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].priority").value(responseDto.getPriority()));
  }

  @Test
  void searchProjectsByTag_invokesService() throws Exception {
    ProjectResponseDto responseDto = buildProjectResponseDto();
    when(projectCrudService.searchProjectsByTag("tag1"))
        .thenReturn(ResponseEntity.ok(List.of(responseDto)));

    mockMvc.perform(get("/search/tag/tag1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].tags[0]").value("tag1"));
  }

  @Test
  void updateProject_invokesService() throws Exception {
    ProjectRequestDto request = buildProjectRequestDto();
    ProjectResponseDto responseDto = buildProjectResponseDto();

    when(projectCrudService.updateProject(eq("id-123"), any(ProjectRequestDto.class)))
        .thenReturn(ResponseEntity.ok(responseDto));

    mockMvc.perform(put("/id-123")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(responseDto.getId()));
  }

  @Test
  void deleteProject_invokesService() throws Exception {
    ApiResponseDto apiResponse = ApiResponseDto.builder()
        .status(200)
        .message("Project deleted successfully")
        .build();

    when(projectCrudService.deleteProject("id-123"))
        .thenReturn(ResponseEntity.ok(apiResponse));

    mockMvc.perform(delete("/id-123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200));
  }

  @Test
  void getProjectSummary_invokesService() throws Exception {
    ProjectSummaryDto summaryDto = ProjectSummaryDto.builder()
        .id("id-123")
        .name("Project Test")
        .client("Cliente A")
        .priority("Alta")
        .health("En foco")
        .progress(75)
        .teamSize(2)
        .build();

    when(projectCrudService.getProjectSummary("id-123"))
        .thenReturn(ResponseEntity.ok(summaryDto));

    mockMvc.perform(get("/id-123/summary"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.teamSize").value(2));
  }

  private ProjectRequestDto buildProjectRequestDto() {
    return ProjectRequestDto.builder()
        .name("Project Test")
        .client("Cliente A")
        .summary("Resumen del proyecto")
        .priority("Alta")
        .health("En foco")
        .progress(75)
        .methodology("Scrum")
        .createdDate(new Date())
        .startDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
        .dueDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 10))
        .tags(List.of("tag1", "tag2"))
        .userCreated(UserRoleDto.builder().userId("user-1").role("ADMIN").build())
        .teamMembers(List.of(
            UserRoleDto.builder().userId("user-2").role("DEV").build(),
            UserRoleDto.builder().userId("user-3").role("QA").build()
        ))
        .build();
  }

  private ProjectResponseDto buildProjectResponseDto() {
    return ProjectResponseDto.builder()
        .id("id-123")
        .name("Project Test")
        .client("Cliente A")
        .summary("Resumen del proyecto")
        .priority("Alta")
        .health("En foco")
        .progress(75)
        .methodology("Scrum")
        .createdDate(new Date())
        .startDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24))
        .dueDate(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 10))
        .tags(List.of("tag1", "tag2"))
        .userCreated(UserRoleDto.builder().userId("user-1").role("ADMIN").build())
        .teamMembers(List.of(
            UserRoleDto.builder().userId("user-2").role("DEV").build(),
            UserRoleDto.builder().userId("user-3").role("QA").build()
        ))
        .build();
  }
}
