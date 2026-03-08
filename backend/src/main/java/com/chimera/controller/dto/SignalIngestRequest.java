package com.chimera.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.UUID;

/**
 * Request body for ingesting an MCP Resource-backed external signal.
 */
public record SignalIngestRequest(
        @NotBlank String sourcePlatform,
        @NotBlank String mcpResourceType,
        @NotBlank String mcpResourceUri,
        @NotBlank String signalType,
        @NotNull Map<String, Object> payloadSummary,
        UUID campaignId
) {}
