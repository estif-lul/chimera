package com.chimera.contract;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Contract tests verifying agent, campaign, execution-plan, and approval endpoints
 * conform to the OpenAPI contract shapes.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CampaignContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "operator")
    void listAgents_returnsArray() throws Exception {
        mockMvc.perform(get("/api/v1/agents").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "operator")
    void createCampaign_requiresMandatoryFields() throws Exception {
        mockMvc.perform(post("/api/v1/campaigns")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "operator")
    void listCampaigns_returnsArray() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "operator")
    void getCampaignPlan_returns404ForMissing() throws Exception {
        mockMvc.perform(get("/api/v1/campaigns/00000000-0000-0000-0000-000000000001/plan")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "operator")
    void approveCampaign_returns404ForMissing() throws Exception {
        mockMvc.perform(post("/api/v1/campaigns/00000000-0000-0000-0000-000000000001/approve"))
                .andExpect(status().isNotFound());
    }
}
