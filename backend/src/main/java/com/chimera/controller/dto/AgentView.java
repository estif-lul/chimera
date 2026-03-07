package com.chimera.controller.dto;

import java.util.UUID;

/**
 * Agent details projection exposed through the API.
 */
public record AgentView(
        UUID id,
        UUID tenantWorkspaceId,
        String displayName,
        String personaSlug,
        String status,
        String soulDefinitionVersion,
        String mutableBiographySummary,
        String visualReferenceId
) {}
