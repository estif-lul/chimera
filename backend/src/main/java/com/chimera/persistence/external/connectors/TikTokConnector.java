package com.chimera.persistence.external.connectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * TikTok publishing and engagement adapter behind the shared PlatformConnector contract.
 */
@Component
public class TikTokConnector implements PlatformConnector {

    private static final Logger log = LoggerFactory.getLogger(TikTokConnector.class);

    @Override
    public ConnectorResponse publishContent(PublishRequest request) {
        log.info("TikTok: publishing content type={}", request.contentType());
        return new ConnectorResponse(true, "tt-post-" + System.currentTimeMillis(), Map.of());
    }

    @Override
    public ConnectorResponse replyToInteraction(ReplyRequest request) {
        log.info("TikTok: replying to interaction={}", request.interactionId());
        return new ConnectorResponse(true, "tt-reply-" + System.currentTimeMillis(), Map.of());
    }

    @Override
    public List<Map<String, Object>> fetchSignals(String mcpResourceUri) {
        log.info("TikTok: fetching signals from {}", mcpResourceUri);
        return List.of();
    }

    @Override
    public RateLimitStatus validateRateLimit() {
        return new RateLimitStatus(true, 50, 0);
    }
}
