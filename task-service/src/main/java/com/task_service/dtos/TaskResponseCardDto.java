package com.task_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseCardDto {
  private String id;
  private String title;
  private String description;
  private String type;
  private String status;
  private String projectId;
  private String priority;
  private String assigneeId;
  private String dueDate;
  private Integer effortPoints;
  private Boolean blocked;
  private List<String> subTasks;
}
