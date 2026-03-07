package com.chimera.controller.dto;

import jakarta.validation.constraints.NotNull;

/**
 * Request body for submitting a review decision.
 */
public record ReviewDecisionRequest(
        @NotNull String decisionType,
        String rationale,
        String editedContent
) {}
