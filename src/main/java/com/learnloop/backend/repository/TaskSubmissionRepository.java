package com.learnloop.backend.repository;

import com.learnloop.backend.model.TaskSubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, String> {
    List<TaskSubmission> findByTaskId(String taskId);
    List<TaskSubmission> findByLearnerId(Long learnerId);
    List<TaskSubmission> findByTaskIdAndLearnerId(String taskId, Long learnerId);
}
