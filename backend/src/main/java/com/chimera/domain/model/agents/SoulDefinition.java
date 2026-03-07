package com.chimera.domain.model.agents;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable persona blueprint loaded from SOUL.md during agent creation
 * and versioned for explicit operator changes.
 */
@Entity
@Table(name = "soul_definition",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tenant_workspace_id", "persona_slug", "version"}))
public class SoulDefinition {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "persona_slug", nullable = false, length = 128)
    private String personaSlug;

    @Column(nullable = false)
    private int version = 1;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String backstory;

    @Column(name = "voice_tone", nullable = false, columnDefinition = "TEXT")
    private String voiceTone;

    @Column(name = "core_beliefs_and_values", nullable = false, columnDefinition = "TEXT")
    private String coreBeliefsAndValues;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String directives;

    @Column(name = "source_path", length = 512)
    private String sourcePath;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected SoulDefinition() {}

    public SoulDefinition(UUID tenantWorkspaceId, String personaSlug, int version,
                          String backstory, String voiceTone,
                          String coreBeliefsAndValues, String directives,
                          String sourcePath) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.personaSlug = personaSlug;
        this.version = version;
        this.backstory = backstory;
        this.voiceTone = voiceTone;
        this.coreBeliefsAndValues = coreBeliefsAndValues;
        this.directives = directives;
        this.sourcePath = sourcePath;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public String getPersonaSlug() { return personaSlug; }
    public int getVersion() { return version; }
    public String getBackstory() { return backstory; }
    public String getVoiceTone() { return voiceTone; }
    public String getCoreBeliefsAndValues() { return coreBeliefsAndValues; }
    public String getDirectives() { return directives; }
    public String getSourcePath() { return sourcePath; }
    public Instant getCreatedAt() { return createdAt; }
}
