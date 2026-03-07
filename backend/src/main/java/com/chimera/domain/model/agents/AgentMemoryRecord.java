package com.chimera.domain.model.agents;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Stores recent context and long-term summaries for agent continuity.
 * Metadata row in PostgreSQL; actual content may live in Redis or Weaviate.
 */
@Entity
@Table(name = "agent_memory_record")
public class AgentMemoryRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "chimera_agent_id", nullable = false)
    private UUID chimeraAgentId;

    @Column(name = "memory_type", nullable = false, length = 32)
    private String memoryType;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "embedding_reference", length = 512)
    private String embeddingReference;

    @Column(name = "storage_backend", nullable = false, length = 16)
    private String storageBackend;

    @Column(name = "source_task_id")
    private UUID sourceTaskId;

    @Column(name = "source_interaction_id")
    private UUID sourceInteractionId;

    @Column(name = "engagement_score", precision = 6, scale = 4)
    private BigDecimal engagementScore;

    @Column(name = "retention_until")
    private Instant retentionUntil;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AgentMemoryRecord() {}

    public AgentMemoryRecord(UUID chimeraAgentId, String memoryType, String content,
                             String storageBackend, UUID sourceTaskId) {
        this.chimeraAgentId = chimeraAgentId;
        this.memoryType = memoryType;
        this.content = content;
        this.storageBackend = storageBackend;
        this.sourceTaskId = sourceTaskId;
    }

    public UUID getId() { return id; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public String getMemoryType() { return memoryType; }
    public String getContent() { return content; }
    public String getEmbeddingReference() { return embeddingReference; }
    public String getStorageBackend() { return storageBackend; }
    public UUID getSourceTaskId() { return sourceTaskId; }
    public UUID getSourceInteractionId() { return sourceInteractionId; }
    public BigDecimal getEngagementScore() { return engagementScore; }
    public Instant getRetentionUntil() { return retentionUntil; }
    public Instant getCreatedAt() { return createdAt; }

    public void setEmbeddingReference(String ref) { this.embeddingReference = ref; }
    public void setEngagementScore(BigDecimal score) { this.engagementScore = score; }
    public void setRetentionUntil(Instant until) { this.retentionUntil = until; }
    public void setSourceInteractionId(UUID id) { this.sourceInteractionId = id; }
}
