package com.chimera.domain.model.audit;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable audit log entry for privileged, risky, or financially significant actions.
 */
@Entity
@Table(name = "audit_event")
public class AuditEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "actor_type", nullable = false, length = 16)
    private String actorType;

    @Column(name = "actor_id", nullable = false, length = 256)
    private String actorId;

    @Column(name = "event_type", nullable = false, length = 128)
    private String eventType;

    @Column(name = "resource_type", nullable = false, length = 128)
    private String resourceType;

    @Column(name = "resource_id", nullable = false, length = 256)
    private String resourceId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "event_payload", columnDefinition = "jsonb")
    private Map<String, Object> eventPayload;

    @Column(name = "correlation_id")
    private UUID correlationId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected AuditEvent() {}

    public AuditEvent(UUID tenantWorkspaceId, String actorType, String actorId,
                      String eventType, String resourceType, String resourceId,
                      Map<String, Object> eventPayload, UUID correlationId) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.actorType = actorType;
        this.actorId = actorId;
        this.eventType = eventType;
        this.resourceType = resourceType;
        this.resourceId = resourceId;
        this.eventPayload = eventPayload;
        this.correlationId = correlationId;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public String getActorType() { return actorType; }
    public String getActorId() { return actorId; }
    public String getEventType() { return eventType; }
    public String getResourceType() { return resourceType; }
    public String getResourceId() { return resourceId; }
    public Map<String, Object> getEventPayload() { return eventPayload; }
    public UUID getCorrelationId() { return correlationId; }
    public Instant getCreatedAt() { return createdAt; }
}
