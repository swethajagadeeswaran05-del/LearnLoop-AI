package com.learnloop.backend.repository;

import com.learnloop.backend.model.SharedResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SharedResourceRepository extends JpaRepository<SharedResource, Long> {
    List<SharedResource> findBySessionId(String sessionId);
}
