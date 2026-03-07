package com.chimera.domain.repository;

import com.chimera.domain.model.campaigns.ExecutionPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for execution plan queries.
 */
public interface ExecutionPlanRepository extends JpaRepository<ExecutionPlan, UUID> {

    List<ExecutionPlan> findByCampaignIdOrderByPlanVersionDesc(UUID campaignId);

    Optional<ExecutionPlan> findByCampaignIdAndStatus(UUID campaignId, String status);
}
