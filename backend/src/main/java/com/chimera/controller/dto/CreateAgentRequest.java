package com.chimera.controller.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for creating a Chimera agent.
 */
public record CreateAgentRequest(
        @NotBlank String displayName,
        @NotBlank String personaSlug,
        @NotNull @Valid SoulDefinitionInput soulDefinition,
        String visualReferenceId
) {}
