package com.chimera.domain.model.campaigns;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Incoming trend, mention, or market input that may generate new work.
 * Must originate from an MCP Resource rather than direct provider-specific ingestion.
 */
@Entity
@Table(name = "external_signal")
public class ExternalSignal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "campaign_id")
    private UUID campaignId;

    @Column(name = "chimera_agent_id")
    private UUID chimeraAgentId;

    @Column(name = "mcp_resource_type", nullable = false, length = 128)
    private String mcpResourceType;

    @Column(name = "mcp_resource_uri", nullable = false, length = 1024)
    private String mcpResourceUri;

    @Column(name = "source_platform", nullable = false, length = 64)
    private String sourcePlatform;

    @Column(name = "signal_type", nullable = false, length = 64)
    private String signalType;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_summary", columnDefinition = "jsonb")
    private Map<String, Object> payloadSummary;

    @Column(name = "relevance_score", precision = 4, scale = 3)
    private BigDecimal relevanceScore;

    @Column(name = "received_at", nullable = false, updatable = false)
    private Instant receivedAt = Instant.now();

    @Column(name = "processed_at")
    private Instant processedAt;

    protected ExternalSignal() {}

    public ExternalSignal(UUID tenantWorkspaceId, String sourcePlatform,
                          String mcpResourceType, String mcpResourceUri,
                          String signalType, Map<String, Object> payloadSummary) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.sourcePlatform = sourcePlatform;
        this.mcpResourceType = mcpResourceType;
        this.mcpResourceUri = mcpResourceUri;
        this.signalType = signalType;
        this.payloadSummary = payloadSummary;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public UUID getCampaignId() { return campaignId; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public String getMcpResourceType() { return mcpResourceType; }
    public String getMcpResourceUri() { return mcpResourceUri; }
    public String getSourcePlatform() { return sourcePlatform; }
    public String getSignalType() { return signalType; }
    public Map<String, Object> getPayloadSummary() { return payloadSummary; }
    public BigDecimal getRelevanceScore() { return relevanceScore; }
    public Instant getReceivedAt() { return receivedAt; }
    public Instant getProcessedAt() { return processedAt; }

    public void setCampaignId(UUID campaignId) { this.campaignId = campaignId; }
    public void setChimeraAgentId(UUID agentId) { this.chimeraAgentId = agentId; }
    public void setRelevanceScore(BigDecimal score) { this.relevanceScore = score; }
    public void markProcessed() { this.processedAt = Instant.now(); }
}
