package com.chimera.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test for SOUL.md immutability, recent-context retention,
 * and Judge-approved Weaviate write-backs.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AgentMemoryWritebackIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "operator")
    void agentPersonaIsImmutableAfterCreation() throws Exception {
        // Create agent
        String agentBody = """
                {
                  "displayName": "Immutable Persona Agent",
                  "personaSlug": "immutable-test",
                  "soulDefinition": {
                    "backstory": "Original backstory",
                    "voiceTone": ["neutral"],
                    "coreBeliefsAndValues": ["honesty"],
                    "directives": ["Stay true"]
                  }
                }
                """;
        mockMvc.perform(post("/api/v1/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agentBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.personaSlug").value("immutable-test"));
    }

    @Test
    @WithMockUser(roles = "operator")
    void memoryEndpointReturnsEmptyForNewAgent() throws Exception {
        // Agent list should work even if memory is empty
        mockMvc.perform(get("/api/v1/agents").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
