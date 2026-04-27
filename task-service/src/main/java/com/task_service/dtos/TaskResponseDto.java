package com.task_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
  private String id;
  private String title;
  private String description;
  private String type;
  private String status;
  private String projectId;
  private String assigneeId;
  private List<String> subTasks;
  private String dueDate;
  private String createdDate;
  private String startDate;
  private String priority;
  private Integer effortPoints;
  private Boolean blocked;
}
