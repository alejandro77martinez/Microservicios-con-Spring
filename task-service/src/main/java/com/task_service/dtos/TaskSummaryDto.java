package com.task_service.dtos;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
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
