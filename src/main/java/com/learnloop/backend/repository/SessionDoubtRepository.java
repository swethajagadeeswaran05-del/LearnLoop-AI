package com.learnloop.backend.repository;

import com.learnloop.backend.model.SessionDoubt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SessionDoubtRepository extends JpaRepository<SessionDoubt, Long> {
    List<SessionDoubt> findBySessionId(String sessionId);
    List<SessionDoubt> findByLearnerId(Long learnerId);
}
