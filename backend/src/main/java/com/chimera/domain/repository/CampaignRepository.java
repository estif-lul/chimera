package com.chimera.domain.repository;

import com.chimera.domain.model.campaigns.Campaign;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for tenant-scoped campaign queries.
 */
public interface CampaignRepository extends JpaRepository<Campaign, UUID> {

    List<Campaign> findByTenantWorkspaceId(UUID tenantWorkspaceId);
}
