package com.project_service.controllers;

import com.project_service.dtos.*;
import com.project_service.services.interfaces.ProjectCrudService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Validated
@RequestMapping("/project")
public class ProjectController {

  private final ProjectCrudService projectCrudService;

  @Autowired
  public ProjectController(ProjectCrudService projectCrudService) {
    this.projectCrudService = projectCrudService;
  }

  @PostMapping
  public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
    return projectCrudService.createProject(projectRequestDto);
  }

  @GetMapping
  public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
    return projectCrudService.getAllProjects();
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId) {
    return projectCrudService.getProjectById(projectId);
  }

  @GetMapping("/client/{clientName}")
  public ResponseEntity<List<ProjectResponseDto>> getProjectsByClient(@PathVariable @NotBlank(message = "El nombre del cliente no puede estar vacío") String clientName) {
    return projectCrudService.getProjectsByClient(clientName);
  }

  @GetMapping("/priority/{priority}")
  public ResponseEntity<List<ProjectResponseDto>> getProjectsByPriority(@PathVariable @NotBlank(message = "La prioridad no puede estar vacía") String priority) {
    return projectCrudService.getProjectsByPriority(priority);
  }

  @GetMapping("/search/tag/{tag}")
  public ResponseEntity<List<ProjectResponseDto>> searchProjectsByTag(@PathVariable @NotBlank(message = "La etiqueta no puede estar vacía") String tag) {
    return projectCrudService.searchProjectsByTag(tag);
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<ProjectResponseDto> updateProject(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId,
      @Valid @RequestBody ProjectRequestDto projectRequestDto) {
    return projectCrudService.updateProject(projectId, projectRequestDto);
  }

  @DeleteMapping("/{projectId}")
  public ResponseEntity<ApiResponseDto> deleteProject(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId) {
    return projectCrudService.deleteProject(projectId);
  }

  @GetMapping("/{projectId}/summary")
  public ResponseEntity<ProjectSummaryDto> getProjectSummary(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId) {
    return projectCrudService.getProjectSummary(projectId);
  }

  @GetMapping("/ofTheUser/{userId}")
  public ResponseEntity<List<ProjectResponseCardDto>> getProjectsByUser(@PathVariable @NotBlank(message = "El ID del usuario no puede estar vacío") String userId) {
    return projectCrudService.getProjectsByUser(userId);
  }

  @PutMapping("/{projectId}/progress/{progress}")
  public ResponseEntity<ProjectResponseDto> updateProgressProject(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId,
      @PathVariable @Min(0) @Max(100) Integer progress) {
    return projectCrudService.updateProgressProject(projectId, progress);
  }

  @PutMapping("/{projectId}/health/{health}")
  public ResponseEntity<ProjectResponseDto> updateHealthProject(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId,
      @PathVariable @Pattern(regexp = "En foco|En riesgo|Descubrimiento",message = "El estado de salud es inválido") String health) {
    return projectCrudService.updateHealthProject(projectId, health);
  }

  @PutMapping("/{projectId}/priority/{priority}")
  public ResponseEntity<ProjectResponseDto> updatePriorityProject(@PathVariable @NotBlank(message = "El ID del proyecto no puede estar vacío") String projectId,
      @PathVariable @Pattern(regexp = "Alta|Media|Baja", message = "La prioridad es inválida") String priority) {
    return projectCrudService.updatePriorityProject(projectId, priority); 
  }
}