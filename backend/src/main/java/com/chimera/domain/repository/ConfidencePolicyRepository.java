package com.chimera.domain.repository;

import com.chimera.domain.model.policy.ConfidencePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for confidence policy lookups.
 */
public interface ConfidencePolicyRepository extends JpaRepository<ConfidencePolicy, UUID> {

    Optional<ConfidencePolicy> findByTenantWorkspaceIdAndCampaignIdIsNull(UUID tenantWorkspaceId);

    Optional<ConfidencePolicy> findByCampaignId(UUID campaignId);
}
