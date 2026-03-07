package com.chimera.controller.dto;

import java.util.UUID;

/**
 * Compact task projection used inside execution plan views.
 */
public record TaskSummary(
        UUID id,
        String taskType,
        String priority,
        String videoRenderTier,
        String status,
        int stateVersion
) {}
