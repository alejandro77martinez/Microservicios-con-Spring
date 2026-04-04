package com.project_service.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class ProjectRequestDto {

  @NotBlank(message = "Project name is required")
  @Size(min = 2, max = 100, message = "Project name must be between 2 and 100 characters")
  private String name;

  @NotBlank(message = "Client is required")
  @Size(min = 2, max = 50, message = "Client name must be between 2 and 50 characters")
  private String client;

  @NotBlank(message = "Summary is required")
  @Size(max = 500, message = "Summary must not exceed 500 characters")
  private String summary;

  @NotBlank(message = "Priority is required")
  @Pattern(regexp = "Alta|Media|Baja", message = "Priority must be Alta, Media, or Baja")
  private String priority;

  @NotBlank(message = "Health is required")
  @Pattern(regexp = "En foco|En riesgo|descubrimiento", message = "Health must be En foco, En riesgo, or descubrimiento")
  private String health;

  @NotNull(message = "Progress is required")
  @Min(value = 0, message = "Progress must be at least 0")
  @Max(value = 100, message = "Progress must not exceed 100")
  private Number progress;

  @NotBlank(message = "Methodology is required")
  private String methodology;

  @NotNull(message = "Created date is required")
  @PastOrPresent(message = "Created date must be in the past or present")
  private Date createdDate;

  @FutureOrPresent(message = "Start date must be in the future or present")
  private Date startDate;

  @Future(message = "Due date must be in the future")
  private Date dueDate;

  @NotNull(message = "Created tags is required")
  private List<String> tags;

  @NotNull(message = "Created tasks is required")
  private List<String> tasks;

  @NotNull(message = "User created is required")
  private RoleUserDto userCreated;

  @NotNull(message = "Created team is required")
  private List<RoleUserDto> teamMembers;
}
