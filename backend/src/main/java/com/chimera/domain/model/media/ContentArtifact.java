package com.chimera.domain.model.media;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Generated text or media draft connected to review and execution.
 */
@Entity
@Table(name = "content_artifact")
public class ContentArtifact {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId;

    @Column(name = "chimera_agent_id", nullable = false)
    private UUID chimeraAgentId;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "artifact_type", nullable = false, length = 16)
    private String artifactType;

    @Column(name = "content_location", length = 1024)
    private String contentLocation;

    @Column(name = "preview_text", columnDefinition = "TEXT")
    private String previewText;

    @Column(name = "generation_provider", length = 128)
    private String generationProvider;

    @Column(name = "generation_mode", nullable = false, length = 32)
    private String generationMode;

    @Column(name = "confidence_score", precision = 4, scale = 3)
    private BigDecimal confidenceScore;

    @Column(name = "policy_classification", length = 64)
    private String policyClassification;

    @Column(name = "disclosure_mode", length = 32)
    private String disclosureMode;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ContentArtifact() {}

    public ContentArtifact(UUID taskId, UUID chimeraAgentId, UUID campaignId,
                           String artifactType, String generationMode) {
        this.taskId = taskId;
        this.chimeraAgentId = chimeraAgentId;
        this.campaignId = campaignId;
        this.artifactType = artifactType;
        this.generationMode = generationMode;
    }

    public UUID getId() { return id; }
    public UUID getTaskId() { return taskId; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public UUID getCampaignId() { return campaignId; }
    public String getArtifactType() { return artifactType; }
    public String getContentLocation() { return contentLocation; }
    public String getPreviewText() { return previewText; }
    public String getGenerationProvider() { return generationProvider; }
    public String getGenerationMode() { return generationMode; }
    public BigDecimal getConfidenceScore() { return confidenceScore; }
    public String getPolicyClassification() { return policyClassification; }
    public String getDisclosureMode() { return disclosureMode; }
    public Instant getCreatedAt() { return createdAt; }

    public void setContentLocation(String location) { this.contentLocation = location; }
    public void setPreviewText(String text) { this.previewText = text; }
    public void setGenerationProvider(String provider) { this.generationProvider = provider; }
    public void setConfidenceScore(BigDecimal score) { this.confidenceScore = score; }
    public void setPolicyClassification(String classification) { this.policyClassification = classification; }
    public void setDisclosureMode(String mode) { this.disclosureMode = mode; }
}
