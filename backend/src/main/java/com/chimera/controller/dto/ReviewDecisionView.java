package com.chimera.controller.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Recorded review decision projection.
 */
public record ReviewDecisionView(
        UUID id,
        UUID reviewItemId,
        String decisionType,
        String rationale,
        Instant createdAt
) {}
