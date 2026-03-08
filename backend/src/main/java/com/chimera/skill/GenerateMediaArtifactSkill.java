package com.chimera.skill;

import java.util.Map;
import java.util.UUID;

/**
 * Skill interface for producing image or video artifacts through MCP Tool
 * orchestration while preserving render-tier, provenance, and cost metadata.
 */
public interface GenerateMediaArtifactSkill {

    /** Execute media artifact generation for the given request envelope. */
    void execute(Request request) throws BudgetExceededException;

    /** Request envelope for media artifact generation. */
    record Request(
            UUID tenantWorkspaceId,
            UUID chimeraAgentId,
            UUID campaignId,
            UUID taskId,
            UUID correlationId,
            String assetType,
            String providerTool,
            Map<String, Object> promptPackage
    ) {}
}
