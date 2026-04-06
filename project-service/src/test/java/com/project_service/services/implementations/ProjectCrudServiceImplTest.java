package com.project_service.services.implementations;

import com.project_service.dtos.ApiResponseDto;
import com.project_service.dtos.ProjectRequestDto;
import com.project_service.dtos.ProjectResponseDto;
import com.project_service.dtos.ProjectSummaryDto;
import com.project_service.dtos.RoleUserDto;
import com.project_service.exceptions.BadRequestException;
import com.project_service.exceptions.ResourceNotFoundException;
import com.project_service.models.ProjectEntity;
import com.project_service.models.RoleUserEntity;
import com.project_service.repositories.ProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectCrudServiceImplTest {

  @Mock
  private ProjectRepository projectRepository;

  @InjectMocks
  private ProjectCrudServiceImpl projectCrudService;

  @Test
  void createProject_success() {
    ProjectRequestDto request = buildProjectRequestDto();
    ProjectEntity savedEntity = buildProjectEntity();

    when(projectRepository.findByName(request.getName())).thenReturn(Optional.empty());
    when(projectRepository.save(any(ProjectEntity.class))).thenReturn(savedEntity);

    ResponseEntity<ProjectResponseDto> response = projectCrudService.createProject(request);

    assertThat(response).isNotNull();
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(savedEntity.getId());
    assertThat(response.getBody().getName()).isEqualTo(request.getName());
  }

  @Test
  void createProject_duplicateName_throwsBadRequestException() {
    ProjectRequestDto request = buildProjectRequestDto();
    ProjectEntity existingEntity = buildProjectEntity();

    when(projectRepository.findByName(request.getName())).thenReturn(Optional.of(existingEntity));

    assertThatThrownBy(() -> projectCrudService.createProject(request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Project with name");
  }

  @Test
  void getAllProjects_returnsList() {
    ProjectEntity entity = buildProjectEntity();
    when(projectRepository.findAll()).thenReturn(List.of(entity));

    ResponseEntity<List<ProjectResponseDto>> response = projectCrudService.getAllProjects();

    assertThat(response).isNotNull();
    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getName()).isEqualTo(entity.getName());
  }

  @Test
  void getProjectById_notFound_throwsBadRequestException() {
    when(projectRepository.findById("id-123")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> projectCrudService.getProjectById("id-123"))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Project not found with id");
  }

  @Test
  void getProjectsByClient_returnsMatching() {
    ProjectEntity entity = buildProjectEntity();
    when(projectRepository.findAll()).thenReturn(List.of(entity));

    ResponseEntity<List<ProjectResponseDto>> response = projectCrudService.getProjectsByClient(entity.getClient());

    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getClient()).isEqualTo(entity.getClient());
  }

  @Test
  void getProjectsByPriority_returnsMatching() {
    ProjectEntity entity = buildProjectEntity();
    when(projectRepository.findAll()).thenReturn(List.of(entity));

    ResponseEntity<List<ProjectResponseDto>> response = projectCrudService.getProjectsByPriority(entity.getPriority());

    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getPriority()).isEqualTo(entity.getPriority());
  }

  @Test
  void searchProjectsByTag_returnsMatching() {
    ProjectEntity entity = buildProjectEntity();
    when(projectRepository.findAll()).thenReturn(List.of(entity));

    ResponseEntity<List<ProjectResponseDto>> response = projectCrudService.searchProjectsByTag("tag1");

    assertThat(response.getBody()).hasSize(1);
    assertThat(response.getBody().get(0).getTags()).containsExactlyInAnyOrder("tag1", "tag2");
  }

  @Test
  void updateProject_success() {
    ProjectRequestDto request = buildProjectRequestDto();
    ProjectEntity existingEntity = buildProjectEntity();
    ProjectEntity updatedEntity = buildProjectEntity();
    updatedEntity.setName("Updated Name");

    when(projectRepository.findById(existingEntity.getId())).thenReturn(Optional.of(existingEntity));
    when(projectRepository.save(any(ProjectEntity.class))).thenReturn(updatedEntity);

    ResponseEntity<ProjectResponseDto> response = projectCrudService.updateProject(existingEntity.getId(), request);

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getName()).isEqualTo("Updated Name");
  }

  @Test
  void updateProject_notFound_throwsBadRequestException() {
    when(projectRepository.findById("missing-id")).thenReturn(Optional.empty());
    ProjectRequestDto request = buildProjectRequestDto();

    assertThatThrownBy(() -> projectCrudService.updateProject("missing-id", request))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Project not found with id");
  }

  @Test
  void deleteProject_success() {
    ProjectEntity existingEntity = buildProjectEntity();

    when(projectRepository.findById(existingEntity.getId())).thenReturn(Optional.of(existingEntity));
    doNothing().when(projectRepository).deleteById(existingEntity.getId());

    ResponseEntity<ApiResponseDto> response = projectCrudService.deleteProject(existingEntity.getId());

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(200);
    assertThat(response.getBody().getMessage()).contains("deleted successfully");
    verify(projectRepository).deleteById(existingEntity.getId());
  }

  @Test
  void deleteProject_notFound_throwsBadRequestException() {
    when(projectRepository.findById("missing-id")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> projectCrudService.deleteProject("missing-id"))
        .isInstanceOf(BadRequestException.class)
        .hasMessageContaining("Project not found with id");
  }

  @Test
  void getProjectSummary_success() {
    ProjectEntity existingEntity = buildProjectEntity();
    when(projectRepository.findById(existingEntity.getId())).thenReturn(Optional.of(existingEntity));

    ResponseEntity<ProjectSummaryDto> response = projectCrudService.getProjectSummary(existingEntity.getId());

    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getId()).isEqualTo(existingEntity.getId());
    assertThat(response.getBody().getTeamSize()).isEqualTo(2);
  }

  @Test
  void getProjectSummary_notFound_throwsBadRequestException() {
    when(projectRepository.findById("missing-id")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> projectCrudService.getProjectSummary("missing-id"))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Project not found with id");
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
        .userCreated(RoleUserDto.builder().userId("user-1").role("ADMIN").build())
        .teamMembers(List.of(
            RoleUserDto.builder().userId("user-2").role("DEV").build(),
            RoleUserDto.builder().userId("user-3").role("QA").build()
        ))
        .build();
  }

  private ProjectEntity buildProjectEntity() {
    return ProjectEntity.builder()
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
        .userCreated(RoleUserEntity.builder().userId("user-1").role("ADMIN").build())
        .teamMembers(List.of(
            RoleUserEntity.builder().userId("user-2").role("DEV").build(),
            RoleUserEntity.builder().userId("user-3").role("QA").build()
        ))
        .build();
  }
}
