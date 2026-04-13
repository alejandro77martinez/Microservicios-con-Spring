package com.project_service.dtos;

import lombok.Data;
import lombok.Builder;

import java.util.List;

@Data
@Builder
public class ProjectResponseCardDto {

  private String id;
  private String name;
  private String client;
  private String role;
  private String summary;
  private String priority;
  private String health;
  private Integer progress;
  private String dueDate;
  private String methodology;
  private List<String> teamMembers;
  private List<String> tags;
}
