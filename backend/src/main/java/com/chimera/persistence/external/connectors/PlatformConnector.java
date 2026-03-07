package com.chimera.persistence.external.connectors;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Standardized boundary between the Chimera control plane and external social platforms.
 * Each connector implements platform-specific API behavior while exposing a uniform contract.
 */
public interface PlatformConnector {

    /** The platform identifier (e.g. "instagram", "tiktok", "youtube"). */
    String platform();

    /** Publish content for a tenant-owned agent. */
    ConnectorResponse publishContent(PublishRequest request);

    /** Reply to a prior interaction on the external platform. */
    ConnectorResponse replyToInteraction(ReplyRequest request);

    /** Fetch engagement signals and normalize them into MCP Resource descriptors. */
    List<Map<String, Object>> fetchSignals(UUID tenantWorkspaceId, UUID agentId);

    /** Check current rate-limit allowance. */
    RateLimitStatus validateRateLimit(UUID tenantWorkspaceId, UUID agentId);

    // ── Request / Response types ───────────────────────────────

    record PublishRequest(
            UUID tenantWorkspaceId,
            UUID agentId,
            UUID campaignId,
            UUID taskId,
            UUID correlationId,
            String text,
            List<MediaAttachment> media,
            String disclosureMode
    ) {}

    record ReplyRequest(
            UUID tenantWorkspaceId,
            UUID agentId,
            UUID correlationId,
            String externalReplyToId,
            String text
    ) {}

    record MediaAttachment(String url, String mediaType) {}

    record ConnectorResponse(
            UUID correlationId,
            String platform,
            String status,
            String externalResourceId,
            RateLimitStatus rateLimit,
            List<String> errors
    ) {}

    record RateLimitStatus(int remaining, String resetAt) {}
}
