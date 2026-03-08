package com.chimera.domain.model.policy;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Tenant- or campaign-scoped decision thresholds for automated approval and human review.
 * Invariant: 0 ≤ reviewThreshold ≤ autoApproveThreshold ≤ 1.
 */
@Entity
@Table(name = "confidence_policy")
public class ConfidencePolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "campaign_id")
    private UUID campaignId;

    @Column(name = "auto_approve_threshold", nullable = false, precision = 4, scale = 3)
    private BigDecimal autoApproveThreshold = new BigDecimal("0.900");

    @Column(name = "review_threshold", nullable = false, precision = 4, scale = 3)
    private BigDecimal reviewThreshold = new BigDecimal("0.600");

    @Column(name = "sensitive_topics", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] sensitiveTopics;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected ConfidencePolicy() {}

    public ConfidencePolicy(UUID tenantWorkspaceId, BigDecimal autoApproveThreshold,
                            BigDecimal reviewThreshold, String[] sensitiveTopics) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.autoApproveThreshold = autoApproveThreshold;
        this.reviewThreshold = reviewThreshold;
        this.sensitiveTopics = sensitiveTopics;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public UUID getCampaignId() { return campaignId; }
    public BigDecimal getAutoApproveThreshold() { return autoApproveThreshold; }
    public BigDecimal getReviewThreshold() { return reviewThreshold; }
    public String[] getSensitiveTopics() { return sensitiveTopics; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setCampaignId(UUID campaignId) {
        this.campaignId = campaignId;
        this.updatedAt = Instant.now();
    }
}
