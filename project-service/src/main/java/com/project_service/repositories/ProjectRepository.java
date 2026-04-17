package com.project_service.repositories;

import com.project_service.models.ProjectEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends MongoRepository<ProjectEntity, String> {
  
  Optional<ProjectEntity> findByName(String name);
  List<ProjectEntity> findByUserCreatedUserIdOrTeamMembersUserId(String userId, String teamMemberUserId);
}
