package com.project.G1_T3.tournament.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.G1_T3.tournament.model.Tournament;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
public class TournamentIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private final String adminUsername = "tuturu";
    private final String adminPassword = "P@ssw0rd";

    @Test
    public void testCreateTournament() throws Exception {
        // JSON payload for tournament creation
        String tournamentJson = "{ " +
                "\"name\": \"Integration Test Tournament\", " +
                "\"location\": \"New York\", " +
                "\"startDate\": \"2024-01-01T10:00:00\", " +
                "\"endDate\": \"2024-01-10T18:00:00\", " +
                "\"deadline\": \"2023-12-20T23:59:59\", " +
                "\"maxParticipants\": 16, " +
                "\"description\": \"An integration test tournament\", " +
                "\"status\": \"SCHEDULED\", " +
                "\"numStages\": 1 " +
                "}";

        // Mock a file for the photo (if required by the endpoint, otherwise it can be
        // removed)
        MockMultipartFile mockFile = new MockMultipartFile("photo", "", "image/jpeg", new byte[0]);

        // Perform the request to create the tournament with authentication
        MvcResult result = mockMvc.perform(
                multipart("/tournament/create")
                        .file(mockFile)
                        .param("tournament", tournamentJson)
                        .with(httpBasic(adminUsername, adminPassword)) // Authentication
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk()) // Expect a 200 OK status if successful
                .andReturn();

        // Verify the response content
        String responseContent = result.getResponse().getContentAsString();
        assertNotNull(responseContent, "Response content should not be null");

        // Optionally, deserialize the response and validate the content
        Tournament createdTournament = new ObjectMapper().readValue(responseContent, Tournament.class);
        assertNotNull(createdTournament.getId(), "Tournament ID should not be null");
        assertEquals("Integration Test Tournament", createdTournament.getName());
        assertEquals("New York", createdTournament.getLocation());
        assertEquals(16, createdTournament.getMaxParticipants());
    }
}
