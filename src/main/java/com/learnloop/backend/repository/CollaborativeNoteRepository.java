package com.learnloop.backend.repository;

import com.learnloop.backend.model.CollaborativeNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CollaborativeNoteRepository extends JpaRepository<CollaborativeNote, Long> {
    @Query("SELECT n FROM CollaborativeNote n WHERE " +
           "(n.userA.id = :userAId AND n.userB.id = :userBId) OR " +
           "(n.userA.id = :userBId AND n.userB.id = :userAId)")
    Optional<CollaborativeNote> findBetweenUsers(Long userAId, Long userBId);
}
