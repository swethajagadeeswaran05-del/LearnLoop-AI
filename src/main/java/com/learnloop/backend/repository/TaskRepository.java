package com.learnloop.backend.repository;

import com.learnloop.backend.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, String> {
    List<Task> findByMentorId(Long mentorId);
    List<Task> findBySessionId(String sessionId);
}
