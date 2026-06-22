package com.learnloop.backend.controller;

import com.learnloop.backend.model.Certificate;
import com.learnloop.backend.model.User;
import com.learnloop.backend.model.ResourceAccess;
import com.learnloop.backend.service.CertificateService;
import com.learnloop.backend.service.UserService;
import com.learnloop.backend.service.CourseCompletionService;
import java.util.Optional;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/certificates")
public class CertificateController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @Autowired
    private CourseCompletionService courseCompletionService;

    @GetMapping
    public ResponseEntity<?> getMyCertificates() {
        try {
            User currentUser = getCurrentUser();
            return ResponseEntity.ok(certificateService.getLearnerCertificates(currentUser.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCertificate(@PathVariable String id) {
        try {
            return ResponseEntity.ok(certificateService.getCertificateById(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/verify/{id}")
    public ResponseEntity<?> verifyCertificate(@PathVariable String id) {
        try {
            Optional<Certificate> verified = courseCompletionService.verifyCertificate(id);
            if (verified.isPresent()) {
                return ResponseEntity.ok(verified.get());
            } else {
                return ResponseEntity.status(404).body(Map.of(
                    "status", "FAILED",
                    "message", "Certificate is not valid or could not be found."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/resources/{resourceId}/access")
    public ResponseEntity<?> trackResourceAccess(@PathVariable Long resourceId) {
        try {
            User currentUser = getCurrentUser();
            ResourceAccess access = courseCompletionService.trackResourceAccess(currentUser.getId(), resourceId);
            
            // On accessing a resource, check if this triggers course completion.
            // Find the session for the resource
            return ResponseEntity.ok(access);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
