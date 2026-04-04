package com.project_service.dtos;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class ProjectSummaryDto {

    private String id;
    private String name;
    private String client;
    private String priority;
    private String health;
    private Number progress;
    private int teamSize;
}
