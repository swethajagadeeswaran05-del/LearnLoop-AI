package com.learnloop.backend;

import com.learnloop.backend.model.LearningSession;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.LearningSessionRepository;
import com.learnloop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class LiveSessionTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LearningSessionRepository sessionRepository;

    @Test
    @WithMockUser(username = "aravind.kumar@gmail.com") // Mentor role
    public void statusTransitionsCorrectly() throws Exception {
        User learner = userRepository.findByEmail("swetha.j@gmail.com").orElseThrow();
        User mentor = userRepository.findByEmail("aravind.kumar@gmail.com").orElseThrow();

        LearningSession session = new LearningSession();
        session.setId(java.util.UUID.randomUUID().toString());
        session.setTitle("Test Meeting");
        session.setStatus("UPCOMING");
        session.setScheduler(learner);
        session.setPartner(mentor);
        session.setScheduledTime(LocalDateTime.now().plusDays(1));
        session = sessionRepository.save(session);

        // Transition UPCOMING -> LIVE
        mockMvc.perform(put("/api/sessions/" + session.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"LIVE\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("LIVE"));

        // Transition LIVE -> COMPLETED
        mockMvc.perform(put("/api/sessions/" + session.getId() + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }
}
