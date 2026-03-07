package com.chimera.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Inline persona input when creating an agent.
 */
public record SoulDefinitionInput(
        @NotBlank String backstory,
        @NotEmpty List<String> voiceTone,
        @NotEmpty List<String> coreBeliefsAndValues,
        @NotEmpty List<String> directives
) {}
