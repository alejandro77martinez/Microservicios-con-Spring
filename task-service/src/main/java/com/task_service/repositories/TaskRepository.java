package com.task_service.repositories;

import com.task_service.models.taskEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends MongoRepository<taskEntity, String> {
  Optional<taskEntity> findByTitle(String title);
  
  List<taskEntity> findByStatus(String status);
  
  List<taskEntity> findByPriority(String priority);
  
  List<taskEntity> findByProjectId(String projectId);
  
  List<taskEntity> findByAssigneeId(String assigneeId);
  
  List<taskEntity> findByType(String type);
  
  List<taskEntity> findByBlocked(Boolean blocked);
  
  List<taskEntity> findByProjectIdAndStatus(String projectId, String status);
  
  List<taskEntity> findByProjectIdAndPriority(String projectId, String priority);
}
