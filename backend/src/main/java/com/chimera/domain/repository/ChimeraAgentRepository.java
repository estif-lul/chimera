package com.chimera.domain.repository;

import com.chimera.domain.model.agents.ChimeraAgent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for tenant-scoped agent queries.
 */
public interface ChimeraAgentRepository extends JpaRepository<ChimeraAgent, UUID> {

    List<ChimeraAgent> findByTenantWorkspaceId(UUID tenantWorkspaceId);
}
