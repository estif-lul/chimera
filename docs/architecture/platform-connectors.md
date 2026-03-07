# Platform Connectors

## Overview

Platform connectors bridge Chimera agents with external social media platforms. Each connector implements the `PlatformConnector` interface defined in `com.chimera.persistence.external.connectors`.

## Connector Interface

```java
public interface PlatformConnector {
    PublishResult publishContent(UUID agentId, String contentBody, Map<String, String> metadata);
    ReplyResult replyToInteraction(UUID agentId, String interactionId, String replyBody);
    List<SignalPayload> fetchSignals(UUID agentId, String signalType, int limit);
    boolean validateRateLimit(UUID agentId);
}
```

## Supported Platforms

### Instagram
- **Class**: `InstagramConnector`
- **Capabilities**: Photo/carousel posts, story replies, comment monitoring
- **Rate limiting**: Instagram Graph API hourly limits

### TikTok
- **Class**: `TikTokConnector`
- **Capabilities**: Video upload, comment replies, trend signal ingestion
- **Rate limiting**: TikTok Content Posting API daily limits

### YouTube
- **Class**: `YouTubeConnector`
- **Capabilities**: Video/Shorts upload, comment replies, engagement signal fetch
- **Rate limiting**: YouTube Data API v3 quota-based

## Adding a New Connector

1. Create a class implementing `PlatformConnector` in `persistence/external/connectors/`
2. Register it as a Spring `@Component`
3. Update the `SignalScoringService` platform detection heuristics
4. Add integration test coverage
5. Document the platform-specific rate limits and authentication in this file

## MCP Integration

Platform connectors are invoked through MCP Tool calls for content publishing and MCP Resource subscriptions for signal ingestion. See the MCP interface contracts:
- `McpToolClient.java` — tool invocation for publishing and media generation
- `McpResourceClient.java` — resource fetch and subscription for signal streams
