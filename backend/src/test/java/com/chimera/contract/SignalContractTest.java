package com.chimera.contract;

import com.chimera.controller.SignalController;
import com.chimera.domain.model.campaigns.ExternalSignal;
import com.chimera.service.signals.SignalScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Contract tests for the signal ingestion endpoint.
 */
@WebMvcTest(SignalController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(SignalContractTest.TestConfig.class)
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
    void ingestSignal_rejectsPayloadMissingPayloadSummary() throws Exception {
        String body = """
                {
                  "sourcePlatform": "instagram",
                  "mcpResourceType": "social/trend",
                  "mcpResourceUri": "mcp://instagram/trends/12345",
                  "signalType": "trend"
                }
                """;

        mockMvc.perform(post("/api/v1/signals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "operator")
    void ingestSignal_acceptsValidPayload() throws Exception {
        String body = """
                {
          "sourcePlatform": "instagram",
                  "mcpResourceType": "social/mention",
          "mcpResourceUri": "mcp://instagram/mentions/12345",
                  "signalType": "mention",
                  "payloadSummary": {"text": "hello"}
                }
                """;
        mockMvc.perform(post("/api/v1/signals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());
    }

        @TestConfiguration
        static class TestConfig {

                @Bean
                SignalScoringService signalScoringService() {
                        return new SignalScoringService(null) {
                                @Override
                                public ExternalSignal ingest(UUID tenantWorkspaceId,
                                                                                         String sourcePlatform,
                                                                                         String mcpResourceType,
                                                                                         String mcpResourceUri,
                                                                                         String signalType,
                                                                                         Map<String, Object> payloadSummary,
                                                                                         UUID campaignId) {
                                        return null;
                                }
                        };
                }
        }
}
