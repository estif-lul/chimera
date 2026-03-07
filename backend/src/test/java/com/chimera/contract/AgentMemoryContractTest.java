package com.chimera.contract;

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
 * Contract tests for agent creation and memory-writeback history endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class AgentMemoryContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "operator")
    void createAgent_returnsCreated() throws Exception {
        String body = """
                {
                  "displayName": "Memory Agent",
                  "personaSlug": "mem-agent",
                  "soulDefinition": {
                    "backstory": "A test agent for memory.",
                    "voiceTone": ["calm"],
                    "coreBeliefsAndValues": ["consistency"],
                    "directives": ["Remember everything"]
                  }
                }
                """;
        mockMvc.perform(post("/api/v1/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty());
    }

    @Test
    @WithMockUser(roles = "operator")
    void getMemoryWritebacks_returns404ForMissingAgent() throws Exception {
        mockMvc.perform(get("/api/v1/agents/00000000-0000-0000-0000-000000000099/memory")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
