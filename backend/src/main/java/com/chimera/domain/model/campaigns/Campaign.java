package com.chimera.domain.model.campaigns;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Operator-defined initiative that drives planning, execution, review, and measurement.
 */
@Entity
@Table(name = "campaign")
public class Campaign {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "goal_description", nullable = false, columnDefinition = "TEXT")
    private String goalDescription;

    @Column(name = "target_audience", columnDefinition = "TEXT")
    private String targetAudience;

    @Column(name = "brand_constraints", columnDefinition = "TEXT[]")
    private String brandConstraints;

    @Column(name = "risk_profile", length = 64)
    private String riskProfile;

    @Column(name = "budget_policy_id")
    private UUID budgetPolicyId;

    @Column(name = "confidence_policy_id")
    private UUID confidencePolicyId;

    @Column(nullable = false, length = 32)
    private String status = "draft";

    @Column(name = "created_by_user_id")
    private UUID createdByUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Campaign() {}

    public Campaign(UUID tenantWorkspaceId, String name, String goalDescription,
                    String targetAudience, UUID createdByUserId) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.name = name;
        this.goalDescription = goalDescription;
        this.targetAudience = targetAudience;
        this.createdByUserId = createdByUserId;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public String getName() { return name; }
    public String getGoalDescription() { return goalDescription; }
    public String getTargetAudience() { return targetAudience; }
    public String getBrandConstraints() { return brandConstraints; }
    public String getRiskProfile() { return riskProfile; }
    public UUID getBudgetPolicyId() { return budgetPolicyId; }
    public UUID getConfidencePolicyId() { return confidencePolicyId; }
    public String getStatus() { return status; }
    public UUID getCreatedByUserId() { return createdByUserId; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setConfidencePolicyId(UUID confidencePolicyId) {
        this.confidencePolicyId = confidencePolicyId;
        this.updatedAt = Instant.now();
    }

    public void setBudgetPolicyId(UUID budgetPolicyId) {
        this.budgetPolicyId = budgetPolicyId;
        this.updatedAt = Instant.now();
    }

    public void transitionTo(String newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }
}
