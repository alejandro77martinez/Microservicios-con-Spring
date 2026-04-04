package com.project_service.models;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "projects")
public class ProjectEntity {
  
    @Id
    private String id;

    @Indexed(unique = true)
    private String name;

    private String client;

    private String summary;

    private String priority;

    private String health;

    private Number progress;

    private String methodology;

    private Date createdDate;

    private Date startDate;

    private Date dueDate;

    private List<String> tags;

    private List<String> tasks;

    private RoleUserEntity userCreated;

    private List<RoleUserEntity> teamMembers;
}
