package com.task_service.dtos;

import java.util.Date;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TaskResponseCardDto {
  private String id;
  private String title;
  private String description;
  private String type;
  private String status;
  private String projectId;
  private String priority;
  private String assigneeId;
  private Date dueDate;
  private Integer effortPoints;
  private Boolean blocked;
  private String parentTaskId;
}
