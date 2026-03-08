package com.chimera.domain.model.campaigns;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

/**
 * Reviewable decomposition of a campaign into taskable work.
 * Only one approved plan may be active per campaign.
 */
@Entity
@Table(name = "execution_plan")
public class ExecutionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "plan_version", nullable = false)
    private int planVersion = 1;

    @Column(columnDefinition = "TEXT")
    private String summary;

    @Column(name = "acceptance_criteria", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private String[] acceptanceCriteria;

    @Column(name = "generated_by", length = 128)
    private String generatedBy;

    @Column(nullable = false, length = 32)
    private String status = "draft";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ExecutionPlan() {}

    public ExecutionPlan(UUID campaignId, int planVersion, String summary,
                         String generatedBy) {
        this.campaignId = campaignId;
        this.planVersion = planVersion;
        this.summary = summary;
        this.generatedBy = generatedBy;
    }

    public UUID getId() { return id; }
    public UUID getCampaignId() { return campaignId; }
    public int getPlanVersion() { return planVersion; }
    public String getSummary() { return summary; }
    public String[] getAcceptanceCriteria() { return acceptanceCriteria; }
    public String getGeneratedBy() { return generatedBy; }
    public String getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    public void approve() {
        this.status = "approved";
    }

    public void supersede() {
        this.status = "superseded";
    }
}
