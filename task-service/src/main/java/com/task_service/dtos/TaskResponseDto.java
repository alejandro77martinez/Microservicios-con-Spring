package com.task_service.dtos;

import java.util.Date;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class TaskResponseDto {
  private String id;
  private String title;
  private String description;
  private String type;
  private String status;
  private String projectId;
  private String assigneeId;
  private String parentTaskId;
  private Date dueDate;
  private Date createdDate;
  private Date startDate;
  private String priority;
  private Integer effortPoints;
  private Boolean blocked;
}
