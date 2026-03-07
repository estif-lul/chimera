package com.chimera.domain.repository;

import com.chimera.domain.model.campaigns.CampaignAgentAssignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for campaign-to-agent assignment lookups.
 */
public interface CampaignAgentAssignmentRepository
        extends JpaRepository<CampaignAgentAssignment, CampaignAgentAssignment.CampaignAgentId> {

    List<CampaignAgentAssignment> findByCampaignId(UUID campaignId);

    List<CampaignAgentAssignment> findByChimeraAgentId(UUID agentId);
}
