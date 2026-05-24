package com.task_service.repositories;

import com.task_service.models.TaskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<TaskEntity, String> {

  Optional<TaskEntity> findByTitle(String title); 
  List<TaskEntity> findByStatus(String status);
  List<TaskEntity> findByPriority(String priority);
  List<TaskEntity> findByProjectId(String projectId);
  List<TaskEntity> findByAssigneeId(String assigneeId);
  List<TaskEntity> findByType(String type);
  List<TaskEntity> findByBlocked(Boolean blocked);
  List<TaskEntity> findByProjectIdAndStatus(String projectId, String status);
  List<TaskEntity> findByProjectIdAndPriority(String projectId, String priority);
  List<TaskEntity> findByProjectIdIn(List<String> projectIds);
}
