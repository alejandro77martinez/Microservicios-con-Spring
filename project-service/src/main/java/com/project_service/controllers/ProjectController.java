package com.project_service.controllers;

import com.project_service.dtos.*;
import com.project_service.services.interfaces.ProjectCrudService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

  @Autowired
  private ProjectCrudService projectCrudService;

  @PostMapping
  public ResponseEntity<ProjectResponseDto> createProject(@Valid @RequestBody ProjectRequestDto projectRequestDto) {
    return projectCrudService.createProject(projectRequestDto);
  }

  @GetMapping
  public ResponseEntity<List<ProjectResponseDto>> getAllProjects() {
    return projectCrudService.getAllProjects();
  }

  @GetMapping("/{projectId}")
  public ResponseEntity<ProjectResponseDto> getProjectById(@PathVariable String projectId) {
    return projectCrudService.getProjectById(projectId);
  }

  @GetMapping("/client/{clientName}")
  public ResponseEntity<List<ProjectResponseDto>> getProjectsByClient(@PathVariable String clientName) {
    return projectCrudService.getProjectsByClient(clientName);
  }

  @GetMapping("/priority/{priority}")
  public ResponseEntity<List<ProjectResponseDto>> getProjectsByPriority(@PathVariable String priority) {
    return projectCrudService.getProjectsByPriority(priority);
  }

  @GetMapping("/search/tag/{tag}")
  public ResponseEntity<List<ProjectResponseDto>> searchProjectsByTag(@PathVariable String tag) {
    return projectCrudService.searchProjectsByTag(tag);
  }

  @PutMapping("/{projectId}")
  public ResponseEntity<ProjectResponseDto> updateProject(@PathVariable String projectId,
      @Valid @RequestBody ProjectRequestDto projectRequestDto) {
    return projectCrudService.updateProject(projectId, projectRequestDto);
  }

  @DeleteMapping("/{projectId}")
  public ResponseEntity<ApiResponseDto> deleteProject(@PathVariable String projectId) {
    return projectCrudService.deleteProject(projectId);
  }

  @GetMapping("/{projectId}/summary")
  public ResponseEntity<ProjectSummaryDto> getProjectSummary(@PathVariable String projectId) {
    return projectCrudService.getProjectSummary(projectId);
  }
}