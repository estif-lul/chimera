package com.chimera.service;

import com.chimera.domain.model.audit.AuditEvent;
import com.chimera.domain.repository.AuditEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

/**
 * Immutable audit logging for privileged, risky, and financial actions.
 */
@Service
public class AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditService.class);
    private final AuditEventRepository auditEventRepository;

    public AuditService(AuditEventRepository auditEventRepository) {
        this.auditEventRepository = auditEventRepository;
    }

    /**
     * Record an immutable audit event.
     */
    public AuditEvent record(UUID tenantWorkspaceId,
                             String actorType,
                             String actorId,
                             String eventType,
                             String resourceType,
                             String resourceId,
                             Map<String, Object> payload,
                             UUID correlationId) {
        AuditEvent event = new AuditEvent(
                tenantWorkspaceId, actorType, actorId,
                eventType, resourceType, resourceId,
                payload, correlationId
        );
        AuditEvent saved = auditEventRepository.save(event);
        log.info("Audit event recorded: type={} resource={}:{} correlation={}",
                eventType, resourceType, resourceId, correlationId);
        return saved;
    }
}
