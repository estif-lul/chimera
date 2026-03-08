package com.chimera.config.seed;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Externalized configuration for the default local account seed data.
 * Controlled via {@code chimera.seed.*} properties.
 *
 * <p>Setting {@code chimera.seed.enabled=false} disables all seed behavior.
 */
@ConfigurationProperties(prefix = "chimera.seed")
public record SeedProperties(
        boolean enabled,
        TenantSeed defaultTenant,
        AdminSeed defaultAdmin,
        PolicySeed defaultPolicy
) {

    /**
     * Default tenant workspace created on first startup.
     */
    public record TenantSeed(
            String slug,
            String displayName
    ) {}

    /**
     * Default administrator account created within the default tenant.
     */
    public record AdminSeed(
            String email,
            String password,
            String roles
    ) {}

    /**
     * Default confidence policy attached to the default tenant workspace.
     */
    public record PolicySeed(
            BigDecimal autoApproveThreshold,
            BigDecimal reviewThreshold,
            String[] sensitiveTopics
    ) {}
}
