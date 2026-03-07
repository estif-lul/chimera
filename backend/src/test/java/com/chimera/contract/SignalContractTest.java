package com.chimera.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contract tests for the signal ingestion endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class SignalContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "operator")
    void ingestSignal_requiresMandatoryFields() throws Exception {
        mockMvc.perform(post("/api/v1/signals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "operator")
    void ingestSignal_acceptsValidPayload() throws Exception {
        String body = """
                {
                  "sourcePlatform": "twitter",
                  "mcpResourceType": "social/mention",
                  "mcpResourceUri": "mcp://twitter/mentions/12345",
                  "signalType": "mention",
                  "payloadSummary": {"text": "hello"}
                }
                """;
        mockMvc.perform(post("/api/v1/signals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }
}
