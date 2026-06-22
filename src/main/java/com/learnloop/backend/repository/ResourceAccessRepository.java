package com.learnloop.backend.repository;

import com.learnloop.backend.model.ResourceAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceAccessRepository extends JpaRepository<ResourceAccess, Long> {
    List<ResourceAccess> findByLearnerId(Long learnerId);
    Optional<ResourceAccess> findByLearnerIdAndResourceId(Long learnerId, Long resourceId);
}
