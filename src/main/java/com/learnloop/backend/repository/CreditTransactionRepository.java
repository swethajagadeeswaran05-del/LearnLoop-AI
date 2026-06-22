package com.learnloop.backend.repository;

import com.learnloop.backend.model.CreditTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, String> {
    List<CreditTransaction> findByUserIdOrderByTimestampDesc(Long userId);
}
