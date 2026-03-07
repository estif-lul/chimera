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
 * Contract tests for review-queue and review-decision endpoints.
 */
@SpringBootTest
@AutoConfigureMockMvc
class ReviewContractTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @WithMockUser(roles = "reviewer")
    void listPendingReviews_returnsArray() throws Exception {
        mockMvc.perform(get("/api/v1/reviews").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "reviewer")
    void submitDecision_requiresMandatoryFields() throws Exception {
        mockMvc.perform(post("/api/v1/reviews/00000000-0000-0000-0000-000000000001/decisions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "reviewer")
    void getReview_returns404ForMissing() throws Exception {
        mockMvc.perform(get("/api/v1/reviews/00000000-0000-0000-0000-000000000099")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
