package com.chimera.skill;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Skill interface for normalizing connector-originated social signals into
 * MCP Resource-backed perception inputs for downstream relevance scoring.
 */
public interface IngestPlatformSignalsSkill {

    /** Execute signal ingestion for the given request envelope. */
    void execute(Request request);

    /** Request envelope for signal ingestion. */
    record Request(
            UUID tenantWorkspaceId,
            UUID chimeraAgentId,
            UUID correlationId,
            String platform,
            String resourceType,
            String resourceUri,
            Instant occurredAt,
            Map<String, Object> payload
    ) {}
}
