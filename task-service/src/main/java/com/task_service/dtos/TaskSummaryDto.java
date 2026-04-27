package com.task_service.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskSummaryDto {
  private String id;
  private String title;
  private String projectId;
  private String status;
  private String priority;
  private String type;
  private Integer effortPoints;
  private Boolean blocked;
}
