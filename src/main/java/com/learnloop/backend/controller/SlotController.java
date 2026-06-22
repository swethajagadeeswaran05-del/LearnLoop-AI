package com.learnloop.backend.controller;

import com.learnloop.backend.model.Slot;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.SlotRepository;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/slots")
public class SlotController {

    @Autowired
    private SlotRepository slotRepository;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @GetMapping
    public List<Slot> getSlots(@RequestParam(required = false) String courseId) {
        if (courseId != null) {
            return slotRepository.findByCourseIdAndIsBookedFalse(courseId);
        }
        return slotRepository.findAll();
    }

    @PostMapping
    public Slot createSlot(@RequestBody Slot slot) {
        slot.setId(UUID.randomUUID().toString());
        slot.setTeacher(getCurrentUser());
        if (slot.getIsBooked() == null) {
            slot.setIsBooked(false);
        }
        return slotRepository.save(slot);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Slot> updateSlot(@PathVariable String id, @RequestBody Slot slotDetails) {
        return slotRepository.findById(id).map(slot -> {
            slot.setIsBooked(slotDetails.getIsBooked());
            return ResponseEntity.ok(slotRepository.save(slot));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSlot(@PathVariable String id) {
        slotRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
