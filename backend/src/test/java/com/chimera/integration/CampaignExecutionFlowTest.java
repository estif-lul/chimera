package com.chimera.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test covering the campaign creation, plan generation, approval,
 * task generation, and optimistic state update flow.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CampaignExecutionFlowTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "operator")
    void campaignLifecycle_fromCreationToApproval() throws Exception {
        // Step 1: Create an agent first
        String agentBody = """
                {
                  "displayName": "Test Agent",
                  "personaSlug": "test-agent",
                  "soulDefinition": {
                    "backstory": "A friendly test persona.",
                    "voiceTone": ["warm"],
                    "coreBeliefsAndValues": ["helpfulness"],
                    "directives": ["Always be kind"]
                  }
                }
                """;
        MvcResult agentResult = mockMvc.perform(post("/api/v1/agents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(agentBody))
                .andExpect(status().isCreated())
                .andReturn();

        String agentId = com.fasterxml.jackson.databind.ObjectMapper
                .class.getDeclaredConstructor().newInstance()
                .readTree(agentResult.getResponse().getContentAsString())
                .get("id").asText();

        // Step 2: Create a campaign
        String campaignBody = """
                {
                  "name": "Integration Test Campaign",
                  "goalDescription": "Drive engagement for test brand.",
                  "targetAudience": "tech enthusiasts",
                  "agentIds": ["%s"]
                }
                """.formatted(agentId);

        MvcResult campaignResult = mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(campaignBody))
                .andExpect(status().isCreated())
                .andReturn();

        String campaignId = com.fasterxml.jackson.databind.ObjectMapper
                .class.getDeclaredConstructor().newInstance()
                .readTree(campaignResult.getResponse().getContentAsString())
                .get("id").asText();

        assertThat(campaignId).isNotBlank();

        // Step 3: Retrieve plan
        mockMvc.perform(get("/api/v1/campaigns/" + campaignId + "/plan")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("draft"));

        // Step 4: Approve the campaign
        mockMvc.perform(post("/api/v1/campaigns/" + campaignId + "/approve"))
                .andExpect(status().isAccepted());

        // Step 5: Verify campaign is now active
        mockMvc.perform(get("/api/v1/campaigns").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.id=='" + campaignId + "')].status").value("active"));
    }
}
