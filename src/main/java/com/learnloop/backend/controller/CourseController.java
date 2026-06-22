package com.learnloop.backend.controller;

import com.learnloop.backend.model.Course;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CourseRepository;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @GetMapping
    public List<Course> getAllCourses(@RequestParam(required = false) String category) {
        if (category != null) {
            return courseRepository.findByCategoryAndIsApprovedTrue(category);
        }
        return courseRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourse(@PathVariable String id) {
        return courseRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createCourse(@RequestBody Course course) {
        User currentUser = getCurrentUser();
        
        if (course.getPricePerSession() != null && course.getPricePerSession() > 0) {
            if (currentUser.getAverageRating() == null || currentUser.getAverageRating() < 4.2) {
                return ResponseEntity.status(403).body("Only Premium Mentors (rating >= 4.2) can create paid sessions.");
            }
        }

        course.setId(UUID.randomUUID().toString());
        course.setTeacher(currentUser);
        course.setCreatedAt(LocalDateTime.now());
        if (course.getIsApproved() == null) {
            course.setIsApproved(true); // Auto-approve for MVP
        }
        if (course.getRating() == null) {
            course.setRating(0.0);
        }
        if (course.getTotalRatings() == null) {
            course.setTotalRatings(0);
        }
        return ResponseEntity.ok(courseRepository.save(course));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourse(@PathVariable String id, @RequestBody Course courseDetails) {
        return courseRepository.findById(id).map(course -> {
            // Only update fields that are provided (not null)
            if (courseDetails.getTitle() != null) {
                course.setTitle(courseDetails.getTitle());
            }
            if (courseDetails.getDescription() != null) {
                course.setDescription(courseDetails.getDescription());
            }
            if (courseDetails.getLanguage() != null) {
                course.setLanguage(courseDetails.getLanguage());
            }
            if (courseDetails.getCategory() != null) {
                course.setCategory(courseDetails.getCategory());
            }
            if (courseDetails.getSessionType() != null) {
                if ("paid".equalsIgnoreCase(courseDetails.getSessionType())) {
                    User currentUser = getCurrentUser();
                    if (currentUser.getAverageRating() == null || currentUser.getAverageRating() < 4.2) {
                        throw new RuntimeException("Only Premium Mentors (rating >= 4.2) can create paid sessions.");
                    }
                }
                course.setSessionType(courseDetails.getSessionType());
            }
            if (courseDetails.getCreditsRequired() != null) {
                course.setCreditsRequired(courseDetails.getCreditsRequired());
            }
            if (courseDetails.getPricePerSession() != null) {
                course.setPricePerSession(courseDetails.getPricePerSession());
            }
            if (courseDetails.getIsApproved() != null) {
                course.setIsApproved(courseDetails.getIsApproved());
            }
            return ResponseEntity.ok(courseRepository.save(course));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable String id) {
        courseRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
