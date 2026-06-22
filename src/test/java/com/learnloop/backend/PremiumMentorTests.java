package com.learnloop.backend;

import com.learnloop.backend.model.Course;
import com.learnloop.backend.model.User;
import com.learnloop.backend.repository.CourseRepository;
import com.learnloop.backend.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class PremiumMentorTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @WithMockUser(username = "swetha.j@gmail.com") // Make sure this user exists via DB seeder
    public void ratingBelow4Point2CannotCreatePaidSessions() throws Exception {
        // Find user and artificially lower rating
        User user = userRepository.findByEmail("swetha.j@gmail.com").orElseThrow();
        user.setAverageRating(3.5);
        userRepository.save(user);

        String payload = "{\"title\":\"Test Course\", \"description\":\"Desc\", \"category\":\"Tech\", \"language\":\"English\", \"sessionType\":\"LIVE\", \"creditsRequired\":10, \"pricePerSession\": 50.0, \"skillName\": \"React.js\"}";
        
        // Ensure paid session creation fails for rating < 4.2
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isForbidden()); // Assuming we return 403 Forbidden
    }

    @Test
    @WithMockUser(username = "aravind.kumar@gmail.com")
    public void ratingAbove4Point2CanCreatePaidSessions() throws Exception {
        User user = userRepository.findByEmail("aravind.kumar@gmail.com").orElseThrow();
        user.setAverageRating(4.5);
        userRepository.save(user);

        String payload = "{\"title\":\"Paid Course\", \"description\":\"Desc\", \"category\":\"Tech\", \"language\":\"English\", \"sessionType\":\"LIVE\", \"creditsRequired\":10, \"pricePerSession\": 50.0, \"skillName\": \"Java\"}";
        
        mockMvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk());
    }
}
