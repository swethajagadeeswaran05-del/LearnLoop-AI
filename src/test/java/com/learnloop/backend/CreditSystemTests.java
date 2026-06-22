package com.learnloop.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnloop.backend.model.CreditTransaction;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CreditTransactionRepository;
import com.learnloop.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class CreditSystemTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CreditTransactionRepository creditRepository;

    @Test
    @WithMockUser(username = "aravind.kumar@gmail.com")
    public void transactionHistoryIsRecordedCorrectly() throws Exception {
        // Mock a transaction
        mockMvc.perform(post("/api/creditTransactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 10, \"type\": \"earned\", \"description\": \"Test session completed\"}"))
                .andExpect(status().isOk());

        User user = userRepository.findByEmail("aravind.kumar@gmail.com").orElseThrow();
        long transactionCount = creditRepository.findByUserIdOrderByTimestampDesc(user.getId()).size();
        assertEquals(1, transactionCount, "Transaction should be saved");
    }
}
