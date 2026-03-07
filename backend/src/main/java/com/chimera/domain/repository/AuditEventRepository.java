package com.chimera.domain.repository;

import com.chimera.domain.model.audit.AuditEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Append-only repository for immutable audit events.
 */
public interface AuditEventRepository extends JpaRepository<AuditEvent, UUID> {

    List<AuditEvent> findByTenantWorkspaceIdOrderByCreatedAtDesc(UUID tenantWorkspaceId);

    List<AuditEvent> findByCorrelationId(UUID correlationId);
}
