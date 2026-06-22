package com.learnloop.backend.repository;

import com.learnloop.backend.model.Slot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlotRepository extends JpaRepository<Slot, String> {
    List<Slot> findByCourseIdAndIsBookedFalse(String courseId);
    List<Slot> findByTeacherId(Long teacherId);
}
