package com.chimera.controller.dto;

import java.util.List;
import java.util.UUID;

/**
 * Execution plan with task summaries.
 */
public record ExecutionPlanView(
        UUID id,
        UUID campaignId,
        int planVersion,
        String status,
        String summary,
        List<String> acceptanceCriteria,
        List<TaskSummary> tasks
) {}
