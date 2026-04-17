package com.project_service.services.interfaces;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.project_service.dtos.ApiResponseDto;
import com.project_service.dtos.ProjectRequestDto;
import com.project_service.dtos.ProjectResponseCardDto;
import com.project_service.dtos.ProjectResponseDto;
import com.project_service.dtos.ProjectSummaryDto;

public interface ProjectCrudService {

  ResponseEntity<ProjectResponseDto> createProject(ProjectRequestDto projectRequestDto);
  ResponseEntity<List<ProjectResponseDto>> getAllProjects();
  ResponseEntity<ProjectResponseDto> getProjectById(String projectId);
  ResponseEntity<List<ProjectResponseDto>> getProjectsByClient(String clientName);
  ResponseEntity<List<ProjectResponseDto>> getProjectsByPriority(String priority);
  ResponseEntity<List<ProjectResponseDto>> searchProjectsByTag(String tag);
  ResponseEntity<List<ProjectResponseCardDto>> getProjectsByUser(String userId);
  ResponseEntity<ProjectResponseDto> updateProject(String projectId, ProjectRequestDto projectRequestDto);
  ResponseEntity<ApiResponseDto> deleteProject(String projectId);
  ResponseEntity<ProjectSummaryDto> getProjectSummary(String projectId);
  ResponseEntity<ProjectResponseDto> updateProgressProject(String projectId, Integer progress);
  ResponseEntity<ProjectResponseDto> updateHealthProject(String projectId, String health);
  ResponseEntity<ProjectResponseDto> updatePriorityProject(String projectId, String priority);
}
