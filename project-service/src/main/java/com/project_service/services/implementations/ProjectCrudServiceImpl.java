package com.project_service.services.implementations;

import com.project_service.dtos.*;
import com.project_service.exceptions.BadRequestException;
import com.project_service.exceptions.ProjectServiceException;
import com.project_service.exceptions.ResourceNotFoundException;
import com.project_service.models.ProjectEntity;
import com.project_service.models.RoleUserEntity;
import com.project_service.repositories.ProjectRepository;
import com.project_service.services.interfaces.ProjectCrudService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProjectCrudServiceImpl implements ProjectCrudService {


  private final ProjectRepository projectRepository;

  @Autowired
  public ProjectCrudServiceImpl(ProjectRepository projectRepository) {
    this.projectRepository = projectRepository;
  }

  @Override
  public ResponseEntity<ProjectResponseDto> createProject(ProjectRequestDto projectRequestDto) {
    try {
      ProjectResponseDto response = createProjectLogic(projectRequestDto);
      return ResponseEntity.ok(response);
    } catch (ProjectServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
    try {
      List<ProjectResponseDto> response = getAllProjectsLogic();
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ProjectResponseDto> getProjectById(String projectId) {
    try {
      ProjectResponseDto response = getProjectByIdLogic(projectId);
      return ResponseEntity.ok(response);
    } catch (ProjectServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<ProjectResponseDto>> getProjectsByClient(String clientName) {
    try {
      List<ProjectResponseDto> response = getProjectsByClientLogic(clientName);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<ProjectResponseDto>> getProjectsByPriority(String priority) {
    try {
      List<ProjectResponseDto> response = getProjectsByPriorityLogic(priority);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<List<ProjectResponseDto>> searchProjectsByTag(String tag) {
    try {
      List<ProjectResponseDto> response = searchProjectsByTagLogic(tag);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ProjectResponseDto> updateProject(String projectId, ProjectRequestDto projectRequestDto) {
    try {
      ProjectResponseDto response = updateProjectLogic(projectId, projectRequestDto);
      return ResponseEntity.ok(response);
    } catch (ProjectServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ApiResponseDto> deleteProject(String projectId) {
    try {
      ApiResponseDto response = deleteProjectLogic(projectId);
      return ResponseEntity.ok(response);
    } catch (ProjectServiceException e) {
      throw new BadRequestException(e.getMessage());
    }
  }

  @Override
  public ResponseEntity<ProjectSummaryDto> getProjectSummary(String projectId) {
    try {
      ProjectSummaryDto response = getProjectSummaryLogic(projectId);
      return ResponseEntity.ok(response);
    } catch (ProjectServiceException e) {
      throw new ResourceNotFoundException(e.getMessage());
    }
  }

  // Métodos privados con lógica

  private ProjectResponseDto createProjectLogic(ProjectRequestDto dto) throws ProjectServiceException {
    Optional<ProjectEntity> existing = projectRepository.findByName(dto.getName());
    if (existing.isPresent()) {
      throw new ProjectServiceException("Project with name '" + dto.getName() + "' already exists");
    }
    ProjectEntity entity = mapToEntity(dto);
    ProjectEntity saved = projectRepository.save(entity);
    return mapToResponseDto(saved);
  }

  private List<ProjectResponseDto> getAllProjectsLogic() {
    List<ProjectEntity> entities = projectRepository.findAll();
    return entities.stream().map(this::mapToResponseDto).toList();
  }

  private ProjectResponseDto getProjectByIdLogic(String projectId) throws ProjectServiceException {
    Optional<ProjectEntity> entity = projectRepository.findById(projectId);
    if (!entity.isPresent()) {
      throw new ProjectServiceException("Project not found with id: " + projectId);
    }
    return mapToResponseDto(entity.get());
  }

  private List<ProjectResponseDto> getProjectsByClientLogic(String clientName) {
    List<ProjectEntity> entities = projectRepository.findAll().stream()
        .filter(p -> clientName.equals(p.getClient()))
        .toList();
    return entities.stream().map(this::mapToResponseDto).toList();
  }

  private List<ProjectResponseDto> getProjectsByPriorityLogic(String priority) {
    List<ProjectEntity> entities = projectRepository.findAll().stream()
        .filter(p -> priority.equals(p.getPriority()))
        .toList();
    return entities.stream().map(this::mapToResponseDto).toList();
  }

  private List<ProjectResponseDto> searchProjectsByTagLogic(String tag) {
    List<ProjectEntity> entities = projectRepository.findAll().stream()
        .filter(p -> p.getTags() != null && p.getTags().contains(tag))
        .toList();
    return entities.stream().map(this::mapToResponseDto).toList();
  }

  private ProjectResponseDto updateProjectLogic(String projectId, ProjectRequestDto dto)
      throws ProjectServiceException {
    Optional<ProjectEntity> existing = projectRepository.findById(projectId);
    if (!existing.isPresent()) {
      throw new ProjectServiceException("Project not found with id: " + projectId);
    }
    ProjectEntity entity = existing.get();
    // Update fields
    entity.setName(dto.getName());
    entity.setClient(dto.getClient());
    entity.setSummary(dto.getSummary());
    entity.setPriority(dto.getPriority());
    entity.setHealth(dto.getHealth());
    entity.setProgress(dto.getProgress());
    entity.setMethodology(dto.getMethodology());
    entity.setCreatedDate(dto.getCreatedDate());
    entity.setStartDate(dto.getStartDate());
    entity.setDueDate(dto.getDueDate());
    entity.setTags(dto.getTags());
    entity.setUserCreated(mapToRoleUserEntity(dto.getUserCreated()));
    entity.setTeamMembers(dto.getTeamMembers().stream().map(this::mapToRoleUserEntity).toList());

    ProjectEntity saved = projectRepository.save(entity);
    return mapToResponseDto(saved);
  }

  private ApiResponseDto deleteProjectLogic(String projectId) throws ProjectServiceException {
    Optional<ProjectEntity> entity = projectRepository.findById(projectId);
    if (!entity.isPresent()) {
      throw new ProjectServiceException("Project not found with id: " + projectId);
    }
    projectRepository.deleteById(projectId);
    return ApiResponseDto.builder().status(200).message("Project deleted successfully").build();
  }

  private ProjectSummaryDto getProjectSummaryLogic(String projectId) throws ProjectServiceException {
    Optional<ProjectEntity> entity = projectRepository.findById(projectId);
    if (!entity.isPresent()) {
      throw new ProjectServiceException("Project not found with id: " + projectId);
    }
    ProjectEntity p = entity.get();
    return ProjectSummaryDto.builder()
        .id(p.getId())
        .name(p.getName())
        .client(p.getClient())
        .priority(p.getPriority())
        .health(p.getHealth())
        .progress(p.getProgress())
        .teamSize(p.getTeamMembers() != null ? p.getTeamMembers().size() : 0)
        .build();
  }

  // Métodos de mapeo
  private ProjectEntity mapToEntity(ProjectRequestDto dto) {
    return ProjectEntity.builder()
        .name(dto.getName())
        .client(dto.getClient())
        .summary(dto.getSummary())
        .priority(dto.getPriority())
        .health(dto.getHealth())
        .progress(dto.getProgress())
        .methodology(dto.getMethodology())
        .createdDate(dto.getCreatedDate())
        .startDate(dto.getStartDate())
        .dueDate(dto.getDueDate())
        .tags(dto.getTags())
        .userCreated(mapToRoleUserEntity(dto.getUserCreated()))
        .teamMembers(dto.getTeamMembers() != null
            ? dto.getTeamMembers().stream().map(this::mapToRoleUserEntity).toList()
            : null)
        .build();
  }

  private ProjectResponseDto mapToResponseDto(ProjectEntity entity) {
    return ProjectResponseDto.builder()
        .id(entity.getId())
        .name(entity.getName())
        .client(entity.getClient())
        .summary(entity.getSummary())
        .priority(entity.getPriority())
        .health(entity.getHealth())
        .progress(entity.getProgress())
        .methodology(entity.getMethodology())
        .createdDate(entity.getCreatedDate())
        .startDate(entity.getStartDate())
        .dueDate(entity.getDueDate())
        .tags(entity.getTags())
        .userCreated(mapToRoleUserDto(entity.getUserCreated()))
        .teamMembers(entity.getTeamMembers() != null
            ? entity.getTeamMembers().stream().map(this::mapToRoleUserDto).toList()
            : null)
        .build();
  }

  private RoleUserEntity mapToRoleUserEntity(RoleUserDto dto) {
    if (dto == null)
      return null;
    return RoleUserEntity.builder()
        .userId(dto.getUserId())
        .role(dto.getRole())
        .build();
  }

  private RoleUserDto mapToRoleUserDto(RoleUserEntity entity) {
    if (entity == null)
      return null;
    return RoleUserDto.builder()
        .userId(entity.getUserId())
        .role(entity.getRole())
        .build();
  }
}