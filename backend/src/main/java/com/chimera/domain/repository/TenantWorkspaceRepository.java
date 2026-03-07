package com.chimera.domain.repository;

import com.chimera.domain.model.tenant.TenantWorkspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for tenant workspace lookups.
 */
public interface TenantWorkspaceRepository extends JpaRepository<TenantWorkspace, UUID> {

    Optional<TenantWorkspace> findBySlug(String slug);
}
