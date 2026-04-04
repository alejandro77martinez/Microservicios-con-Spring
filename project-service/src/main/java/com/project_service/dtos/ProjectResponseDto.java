package com.project_service.dtos;

import lombok.Data;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProjectResponseDto {

    private String id;
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
    private RoleUserDto userCreated;
    private List<RoleUserDto> teamMembers;
}
