package com.chimera.domain.model.agents;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Persistent digital influencer identity with governance, memory, and wallet references.
 */
@Entity
@Table(name = "chimera_agent")
public class ChimeraAgent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "display_name", nullable = false, length = 255)
    private String displayName;

    @Column(name = "persona_slug", nullable = false, length = 128)
    private String personaSlug;

    @Column(name = "soul_definition_id", nullable = false)
    private UUID soulDefinitionId;

    @Column(name = "soul_document_version", nullable = false)
    private int soulDocumentVersion = 1;

    @Column(nullable = false, length = 32)
    private String status = "draft";

    @Column(name = "mutable_biography_summary", columnDefinition = "TEXT")
    private String mutableBiographySummary;

    @Column(name = "visual_reference_id", length = 512)
    private String visualReferenceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "current_state", columnDefinition = "jsonb")
    private Map<String, Object> currentState;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected ChimeraAgent() {}

    public ChimeraAgent(UUID tenantWorkspaceId, String displayName, String personaSlug,
                        UUID soulDefinitionId, int soulDocumentVersion, String visualReferenceId) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.displayName = displayName;
        this.personaSlug = personaSlug;
        this.soulDefinitionId = soulDefinitionId;
        this.soulDocumentVersion = soulDocumentVersion;
        this.visualReferenceId = visualReferenceId;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public String getDisplayName() { return displayName; }
    public String getPersonaSlug() { return personaSlug; }
    public UUID getSoulDefinitionId() { return soulDefinitionId; }
    public int getSoulDocumentVersion() { return soulDocumentVersion; }
    public String getStatus() { return status; }
    public String getMutableBiographySummary() { return mutableBiographySummary; }
    public String getVisualReferenceId() { return visualReferenceId; }
    public Map<String, Object> getCurrentState() { return currentState; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void activate() {
        this.status = "active";
        this.updatedAt = Instant.now();
    }

    public void pause() {
        this.status = "paused";
        this.updatedAt = Instant.now();
    }

    public void setMutableBiographySummary(String summary) {
        this.mutableBiographySummary = summary;
        this.updatedAt = Instant.now();
    }
}
