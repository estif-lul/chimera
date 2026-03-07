package com.chimera.domain.repository;

import com.chimera.domain.model.campaigns.ExternalSignal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for external signal ingestion and retrieval.
 */
public interface ExternalSignalRepository extends JpaRepository<ExternalSignal, UUID> {

    List<ExternalSignal> findByTenantWorkspaceIdAndProcessedAtIsNull(UUID tenantWorkspaceId);

    List<ExternalSignal> findByCampaignId(UUID campaignId);
}
