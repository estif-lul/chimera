package com.chimera.domain.model.tenant;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Security and billing boundary for all customer-owned resources.
 */
@Entity
@Table(name = "tenant_workspace")
public class TenantWorkspace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 128)
    private String slug;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(nullable = false, length = 32)
    private String status = "active";

    @Column(name = "default_confidence_policy_id")
    private UUID defaultConfidencePolicyId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected TenantWorkspace() {}

    public TenantWorkspace(String slug, String displayName) {
        this.slug = slug;
        this.displayName = displayName;
    }

    public UUID getId() { return id; }
    public String getSlug() { return slug; }
    public String getDisplayName() { return displayName; }
    public String getStatus() { return status; }
    public UUID getDefaultConfidencePolicyId() { return defaultConfidencePolicyId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setDefaultConfidencePolicyId(UUID defaultConfidencePolicyId) {
        this.defaultConfidencePolicyId = defaultConfidencePolicyId;
        this.updatedAt = Instant.now();
    }
}
