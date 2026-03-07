package com.chimera.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

/**
 * Request body for creating a campaign.
 */
public record CreateCampaignRequest(
        @NotBlank String name,
        @NotBlank String goalDescription,
        @NotBlank String targetAudience,
        List<String> brandConstraints,
        String riskProfile,
        @NotEmpty List<UUID> agentIds,
        UUID confidencePolicyId,
        UUID budgetPolicyId,
        String defaultVideoRenderTier
) {}
