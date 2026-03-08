package com.chimera.persistence.external.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Instagram publishing and engagement adapter behind the shared PlatformConnector contract.
 */
@Component
public class InstagramConnector implements PlatformConnector {

    private static final Logger log = LoggerFactory.getLogger(InstagramConnector.class);

    @Override
    public String platform() {
        return "instagram";
    }

    @Override
    public ConnectorResponse publishContent(PublishRequest request) {
        log.info("Instagram: publishing content for agent={}", request.agentId());
        // Placeholder — real implementation calls Instagram Graph API
        String externalId = "ig-post-" + System.currentTimeMillis();
        return new ConnectorResponse(request.correlationId(), "instagram", "accepted", externalId,
                new RateLimitStatus(100, "PT15M"), List.of());
    }

    @Override
    public ConnectorResponse replyToInteraction(ReplyRequest request) {
        log.info("Instagram: replying to interaction={}", request.externalReplyToId());
        String externalId = "ig-reply-" + System.currentTimeMillis();
        return new ConnectorResponse(request.correlationId(), "instagram", "accepted", externalId,
                new RateLimitStatus(99, "PT15M"), List.of());
    }

    @Override
    public List<Map<String, Object>> fetchSignals(UUID tenantWorkspaceId, UUID agentId) {
        log.info("Instagram: fetching signals for tenant={} agent={}", tenantWorkspaceId, agentId);
        return List.of(Map.of(
                "sourcePlatform", "instagram",
                "mcpResourceType", "social/trend",
                "mcpResourceUri", "mcp://instagram/trends/stub-" + agentId,
                "signalType", "trend",
                "payloadSummary", Map.of("topic", "placeholder", "score", 0.0)
        ));
    }

    @Override
    public RateLimitStatus validateRateLimit(UUID tenantWorkspaceId, UUID agentId) {
        return new RateLimitStatus(100, "PT15M");
    }
}
