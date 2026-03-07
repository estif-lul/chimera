package com.chimera.domain.model.media;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Tracks video generation job metadata and ties cost, provenance, and status
 * to campaign governance.
 */
@Entity
@Table(name = "video_render_job")
public class VideoRenderJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "content_artifact_id")
    private UUID contentArtifactId;

    @Column(name = "chimera_agent_id", nullable = false)
    private UUID chimeraAgentId;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "render_tier", nullable = false, length = 32)
    private String renderTier;

    @Column(nullable = false, length = 64)
    private String provider;

    @Column(name = "source_prompt", columnDefinition = "TEXT")
    private String sourcePrompt;

    @Column(name = "source_image_asset_id", length = 512)
    private String sourceImageAssetId;

    @Column(nullable = false, length = 32)
    private String status = "queued";

    @Column(name = "cost_amount", precision = 20, scale = 8)
    private BigDecimal costAmount;

    @Column(name = "cost_currency", length = 8)
    private String costCurrency;

    @Column(name = "requested_at", nullable = false, updatable = false)
    private Instant requestedAt = Instant.now();

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "asset_uri", length = 1024)
    private String assetUri;

    @Column(name = "provider_job_id", length = 256)
    private String providerJobId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata_json", columnDefinition = "jsonb")
    private Map<String, Object> metadataJson;

    protected VideoRenderJob() {}

    public VideoRenderJob(UUID taskId, UUID chimeraAgentId, UUID campaignId,
                          String renderTier, String provider) {
        this.taskId = taskId;
        this.chimeraAgentId = chimeraAgentId;
        this.campaignId = campaignId;
        this.renderTier = renderTier;
        this.provider = provider;
    }

    public UUID getId() { return id; }
    public UUID getTaskId() { return taskId; }
    public UUID getContentArtifactId() { return contentArtifactId; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public UUID getCampaignId() { return campaignId; }
    public String getRenderTier() { return renderTier; }
    public String getProvider() { return provider; }
    public String getSourcePrompt() { return sourcePrompt; }
    public String getSourceImageAssetId() { return sourceImageAssetId; }
    public String getStatus() { return status; }
    public BigDecimal getCostAmount() { return costAmount; }
    public String getCostCurrency() { return costCurrency; }
    public Instant getRequestedAt() { return requestedAt; }
    public Instant getCompletedAt() { return completedAt; }
    public String getAssetUri() { return assetUri; }
    public String getProviderJobId() { return providerJobId; }
    public Map<String, Object> getMetadataJson() { return metadataJson; }

    public void setContentArtifactId(UUID id) { this.contentArtifactId = id; }
    public void setSourcePrompt(String prompt) { this.sourcePrompt = prompt; }
    public void setSourceImageAssetId(String assetId) { this.sourceImageAssetId = assetId; }

    public void transitionTo(String newStatus) {
        this.status = newStatus;
    }

    public void complete(String assetUri, BigDecimal cost, String currency) {
        this.status = "completed";
        this.assetUri = assetUri;
        this.costAmount = cost;
        this.costCurrency = currency;
        this.completedAt = Instant.now();
    }

    public void setProviderJobId(String jobId) { this.providerJobId = jobId; }
}
