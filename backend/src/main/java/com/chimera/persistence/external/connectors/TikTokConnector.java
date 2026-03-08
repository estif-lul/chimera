package com.chimera.persistence.external.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * TikTok publishing and engagement adapter behind the shared PlatformConnector contract.
 */
@Component
public class TikTokConnector implements PlatformConnector {

    private static final Logger log = LoggerFactory.getLogger(TikTokConnector.class);

    @Override
    public String platform() {
        return "tiktok";
    }

    @Override
    public ConnectorResponse publishContent(PublishRequest request) {
        log.info("TikTok: publishing content for agent={}", request.agentId());
        String externalId = "tt-post-" + System.currentTimeMillis();
        return new ConnectorResponse(request.correlationId(), "tiktok", "accepted", externalId,
                new RateLimitStatus(50, "PT15M"), List.of());
    }

    @Override
    public ConnectorResponse replyToInteraction(ReplyRequest request) {
        log.info("TikTok: replying to interaction={}", request.externalReplyToId());
        String externalId = "tt-reply-" + System.currentTimeMillis();
        return new ConnectorResponse(request.correlationId(), "tiktok", "accepted", externalId,
                new RateLimitStatus(49, "PT15M"), List.of());
    }

    @Override
    public List<Map<String, Object>> fetchSignals(UUID tenantWorkspaceId, UUID agentId) {
        log.info("TikTok: fetching signals for tenant={} agent={}", tenantWorkspaceId, agentId);
        return List.of(Map.of(
                "sourcePlatform", "tiktok",
                "mcpResourceType", "social/trend",
                "mcpResourceUri", "mcp://tiktok/trends/stub-" + agentId,
                "signalType", "trend",
                "payloadSummary", Map.of("topic", "placeholder", "score", 0.0)
        ));
    }

    @Override
    public RateLimitStatus validateRateLimit(UUID tenantWorkspaceId, UUID agentId) {
        return new RateLimitStatus(50, "PT15M");
    }
}
