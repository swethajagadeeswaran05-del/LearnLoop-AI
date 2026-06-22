package com.learnloop.backend.repository;

import com.learnloop.backend.model.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends JpaRepository<Certificate, String> {
    List<Certificate> findByLearnerId(Long learnerId);
    List<Certificate> findByMentorId(Long mentorId);
    java.util.Optional<Certificate> findByLearnerIdAndCourseId(Long learnerId, String courseId);
    java.util.Optional<Certificate> findByLearnerIdAndCourseIdAndCertificateType(Long learnerId, String courseId, String certificateType);
}
