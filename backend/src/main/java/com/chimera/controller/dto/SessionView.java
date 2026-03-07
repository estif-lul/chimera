package com.chimera.controller.dto;

import java.util.List;
import java.util.UUID;

/**
 * Authenticated session details returned after login or session lookup.
 */
public record SessionView(
        UUID userId,
        UUID tenantWorkspaceId,
        List<String> roles,
        String authProviderType
) {}
