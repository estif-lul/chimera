package com.chimera.domain.repository;

import com.chimera.domain.model.campaigns.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for task queries scoped by campaign and execution plan.
 */
public interface TaskRepository extends JpaRepository<Task, UUID> {

    List<Task> findByExecutionPlanId(UUID executionPlanId);

    List<Task> findByCampaignId(UUID campaignId);

    List<Task> findByChimeraAgentIdAndStatus(UUID agentId, String status);
}
