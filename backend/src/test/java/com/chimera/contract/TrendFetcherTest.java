package com.chimera.contract;

import com.chimera.persistence.external.connectors.InstagramConnector;
import com.chimera.persistence.external.connectors.PlatformConnector;
import com.chimera.persistence.external.connectors.TikTokConnector;
import com.chimera.persistence.external.connectors.YouTubeConnector;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opentest4j.AssertionFailedError;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Contract tests for normalized trend payloads emitted by platform connectors.
 */
class TrendFetcherTest {

    @Test
    @DisplayName("Connector trend payloads match the signal ingest API contract")
    void connectorTrendPayloads_matchSignalIngestContract() {
        UUID tenantWorkspaceId = UUID.randomUUID();
        UUID agentId = UUID.randomUUID();

        List<PlatformConnector> connectors = List.of(
                new InstagramConnector(),
                new TikTokConnector(),
                new YouTubeConnector()
        );

        for (PlatformConnector connector : connectors) {
            List<Map<String, Object>> signals = connector.fetchSignals(tenantWorkspaceId, agentId);
            assertFalse(signals.isEmpty(), () -> connector.platform()
                    + " should return at least one normalized signal payload");

            Map<String, Object> trendPayload = signals.stream()
                    .filter(signal -> "trend".equals(signal.get("signalType")))
                    .findFirst()
                    .orElseThrow(() -> new AssertionFailedError(
                            connector.platform() + " should expose a trend signal payload"));

            assertHasText(trendPayload, "sourcePlatform");
            assertHasText(trendPayload, "mcpResourceType");
            assertHasText(trendPayload, "mcpResourceUri");
            assertHasText(trendPayload, "signalType");
            assertTrue(trendPayload.get("payloadSummary") instanceof Map,
                    () -> connector.platform() + " should expose payloadSummary as an object");
            assertTrue(((String) trendPayload.get("mcpResourceUri")).startsWith("mcp://"),
                    () -> connector.platform() + " should emit canonical MCP resource URIs");
            assertTrue(((String) trendPayload.get("mcpResourceType")).toLowerCase().contains("trend"),
                    () -> connector.platform() + " should expose a trend-specific MCP resource type");
        }
    }

    private static void assertHasText(Map<String, Object> payload, String key) {
        Object value = payload.get(key);
        assertTrue(value instanceof String && !((String) value).isBlank(),
                () -> "Expected non-blank string field '" + key + "' in payload " + payload);
    }
}