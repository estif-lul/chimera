package com.chimera.domain.model.review;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Queueable review work unit for human moderation.
 * At least one of contentArtifactId or transactionRequestId must be set.
 */
@Entity
@Table(name = "review_item")
public class ReviewItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "content_artifact_id")
    private UUID contentArtifactId;

    @Column(name = "transaction_request_id")
    private UUID transactionRequestId;

    @Column(name = "queue_status", nullable = false, length = 16)
    private String queueStatus = "pending";

    @Column(name = "reason_codes", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] reasonCodes;

    @Column(name = "confidence_score", precision = 4, scale = 3)
    private BigDecimal confidenceScore;

    @Column(name = "policy_classification", length = 64)
    private String policyClassification;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    protected ReviewItem() {}

    public ReviewItem(UUID tenantWorkspaceId, UUID taskId, UUID contentArtifactId,
                      UUID transactionRequestId, BigDecimal confidenceScore,
                      String policyClassification) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.taskId = taskId;
        this.contentArtifactId = contentArtifactId;
        this.transactionRequestId = transactionRequestId;
        this.confidenceScore = confidenceScore;
        this.policyClassification = policyClassification;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public UUID getTaskId() { return taskId; }
    public UUID getContentArtifactId() { return contentArtifactId; }
    public UUID getTransactionRequestId() { return transactionRequestId; }
    public String getQueueStatus() { return queueStatus; }
    public String[] getReasonCodes() { return reasonCodes; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public String getPolicyClassification() { return policyClassification; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }

    public void claim() {
        this.queueStatus = "claimed";
    }

    public void resolve(String outcome) {
        this.queueStatus = outcome;
        this.resolvedAt = Instant.now();
    }
}
