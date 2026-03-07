package com.chimera.domain.repository;

import com.chimera.domain.model.agents.SoulDefinition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for immutable soul definitions.
 */
public interface SoulDefinitionRepository extends JpaRepository<SoulDefinition, UUID> {

    Optional<SoulDefinition> findByTenantWorkspaceIdAndPersonaSlugAndVersion(
            UUID tenantWorkspaceId, String personaSlug, int version);

    Optional<SoulDefinition> findTopByTenantWorkspaceIdAndPersonaSlugOrderByVersionDesc(
            UUID tenantWorkspaceId, String personaSlug);
}
