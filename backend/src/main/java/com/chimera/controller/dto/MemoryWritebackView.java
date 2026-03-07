package com.chimera.controller.dto;

import java.time.Instant;
import java.util.UUID;

/**
 * Memory write-back record visible through the API.
 */
public record MemoryWritebackView(
        UUID id,
        UUID chimeraAgentId,
        String memoryType,
        String storageBackend,
        String content,
        UUID sourceTaskId,
        Double engagementScore,
        Instant createdAt
) {}
