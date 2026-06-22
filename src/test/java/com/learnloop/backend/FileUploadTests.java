package com.learnloop.backend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("h2")
public class FileUploadTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(username = "swetha.j@gmail.com")
    public void supportedFileTypesUploadSuccessfully() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test.pdf",
                "application/pdf",
                "Hello, World!".getBytes()
        );

        mockMvc.perform(multipart("/api/uploads").file(file))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "swetha.j@gmail.com")
    public void invalidFileTypesAreRejected() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "script.js",
                "application/javascript",
                "console.log('hack');".getBytes()
        );

        mockMvc.perform(multipart("/api/uploads").file(file))
                .andExpect(status().isUnsupportedMediaType());
    }
}
