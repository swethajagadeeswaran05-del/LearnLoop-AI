package com.learnloop.backend.service;

import com.learnloop.backend.model.Certificate;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CertificateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {

    @Autowired
    private CertificateRepository certificateRepository;

    public Certificate generateCertificate(User learner, User mentor, String courseId) {
        java.util.Optional<Certificate> existing = certificateRepository.findByLearnerIdAndCourseId(learner.getId(), courseId);
        if (existing.isPresent()) {
            return existing.get();
        }

        Certificate certificate = new Certificate();
        certificate.setId(UUID.randomUUID().toString());
        certificate.setLearner(learner);
        certificate.setMentor(mentor);
        certificate.setCourseId(courseId);
        certificate.setIssueDate(LocalDateTime.now());
        
        // Frontend will generate the QR code using this URL pattern
        certificate.setCertificateUrl("/certificates/" + certificate.getId());

        return certificateRepository.save(certificate);
    }

    public List<Certificate> getLearnerCertificates(Long learnerId) {
        return certificateRepository.findByLearnerId(learnerId);
    }

    public Certificate getCertificateById(String id) {
        return certificateRepository.findById(id).orElseThrow(() -> new RuntimeException("Certificate not found"));
    }
}
