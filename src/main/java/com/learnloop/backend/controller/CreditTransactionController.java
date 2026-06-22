package com.learnloop.backend.controller;

import com.learnloop.backend.model.CreditTransaction;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CreditTransactionRepository;
import com.learnloop.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/creditTransactions")
public class CreditTransactionController {

    @Autowired
    private CreditTransactionRepository transactionRepository;

    @Autowired
    private UserService userService;

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Logged in user not found"));
    }

    @GetMapping
    public List<CreditTransaction> getUserTransactions() {
        User currentUser = getCurrentUser();
        return transactionRepository.findByUserIdOrderByTimestampDesc(currentUser.getId());
    }

    @PostMapping
    public CreditTransaction createTransaction(@RequestBody CreditTransaction transaction) {
        transaction.setId(UUID.randomUUID().toString());
        // For security, you might want to validate this, but allowing direct insert for MVP
        if (transaction.getUser() == null) {
            transaction.setUser(getCurrentUser());
        }
        transaction.setTimestamp(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }
}
