package com.learnloop.backend.repository;

import com.learnloop.backend.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
    List<Course> findByTeacherId(Long teacherId);
    List<Course> findByCategoryAndIsApprovedTrue(String category);
    List<Course> findByIsApprovedTrue();
}
