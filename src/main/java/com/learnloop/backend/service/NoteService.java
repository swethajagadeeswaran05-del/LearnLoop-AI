package com.learnloop.backend.service;

import com.learnloop.backend.model.CollaborativeNote;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CollaborativeNoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class NoteService {

    @Autowired
    private CollaborativeNoteRepository noteRepository;

    @Autowired
    private UserService userService;

    public CollaborativeNote getNoteBetweenUsers(User currentUser, Long partnerId) {
        Optional<CollaborativeNote> noteOpt = noteRepository.findBetweenUsers(currentUser.getId(), partnerId);
        if (noteOpt.isPresent()) {
            return noteOpt.get();
        }

        // Initialize a new empty note if none exists
        User partner = userService.findById(partnerId)
                .orElseThrow(() -> new RuntimeException("Partner user not found"));

        CollaborativeNote note = CollaborativeNote.builder()
                .userA(currentUser)
                .userB(partner)
                .content("# Collaborative Space\n\nWrite shared session notes here...")
                .updatedAt(LocalDateTime.now())
                .build();

        return noteRepository.save(note);
    }

    @Transactional
    public CollaborativeNote updateNoteBetweenUsers(User currentUser, Long partnerId, String content) {
        CollaborativeNote note = getNoteBetweenUsers(currentUser, partnerId);
        note.setContent(content);
        note.setUpdatedAt(LocalDateTime.now());
        return noteRepository.save(note);
    }
}
