package com.learnloop.backend;

import com.learnloop.backend.model.Certificate;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CertificateRepository;
import com.learnloop.backend.repository.UserRepository;
import com.learnloop.backend.service.CertificateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("h2")
public class CertificateTests {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private com.learnloop.backend.repository.CourseRepository courseRepository;

    @Test
    public void certificatesAreGeneratedCorrectlyAndNoDuplicates() {
        User learner = userRepository.findByEmail("swetha.j@gmail.com").orElseThrow();
        User mentor = userRepository.findByEmail("aravind.kumar@gmail.com").orElseThrow();
        
        com.learnloop.backend.model.Course course = new com.learnloop.backend.model.Course();
        course.setId(java.util.UUID.randomUUID().toString());
        course.setTitle("Test Course");
        course.setTeacher(mentor);
        course.setCreatedAt(java.time.LocalDateTime.now());
        course = courseRepository.save(course);
        
        String sessionId = course.getId();

        // Generate first time
        Certificate cert1 = certificateService.generateCertificate(learner, mentor, sessionId);
        assertNotNull(cert1);
        assertNotNull(cert1.getId());
        
        // Count before second attempt
        long countBefore = certificateRepository.count();

        // Generate second time should not create duplicate
        Certificate cert2 = certificateService.generateCertificate(learner, mentor, sessionId);
        
        long countAfter = certificateRepository.count();
        assertEquals(countBefore, countAfter, "Duplicate certificates should not be created");
        assertEquals(cert1.getId(), cert2.getId(), "Should return the existing certificate");
    }
}
