package com.learnloop.backend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class ChatTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Test
    @WithMockUser(username = "swetha.j@gmail.com")
    public void chatMessageCanBeSentAndRetrieved() throws Exception {
        User partner = userRepository.findByEmail("aravind.kumar@gmail.com").orElseThrow();

        // Send a message
        String payload = "{\"receiverId\":" + partner.getId() + ",\"content\":\"Hello from test\"}";
        mockMvc.perform(post("/api/chat/message")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());

        // Retrieve messages
        mockMvc.perform(get("/api/chat/history/" + partner.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value("Hello from test"));
    }
}
