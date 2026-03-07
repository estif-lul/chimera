package com.chimera.controller.dto;

import java.util.UUID;

/**
 * Campaign details projection exposed through the API.
 */
public record CampaignView(
        UUID id,
        UUID tenantWorkspaceId,
        String name,
        String status,
        String goalDescription,
        String targetAudience,
        UUID currentPlanId,
        String activeBudgetRemaining
) {}
