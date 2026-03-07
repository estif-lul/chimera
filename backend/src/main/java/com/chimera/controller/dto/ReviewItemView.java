package com.chimera.controller.dto;

import java.util.List;
import java.util.UUID;

/**
 * Review queue item projection.
 */
public record ReviewItemView(
        UUID id,
        UUID taskId,
        String queueStatus,
        double confidenceScore,
        String policyClassification,
        List<String> reasonCodes,
        String preview
) {}
