package com.task_service.models;

import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tasks")
public class TaskEntity {

  @Id
  private String id;
  private String title;
  private String description;
  private String type;
  private String status;
  private String projectId;
  private String assigneeId;
  private List<String> subTasks;
  private Date dueDate;
  private Date createdDate;
  private Date startDate;
  private String priority;
  private Integer effortPoints;
  private Boolean blocked;

}
