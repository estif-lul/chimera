package com.chimera.service.campaign;

import com.chimera.domain.model.agents.ChimeraAgent;
import com.chimera.domain.model.campaigns.*;
import com.chimera.domain.repository.*;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Orchestrates campaign creation, execution plan generation, approval, and activation.
 */
@Service
public class CampaignPlanningService {

    private static final Logger log = LoggerFactory.getLogger(CampaignPlanningService.class);

    private final CampaignRepository campaignRepository;
    private final CampaignAgentAssignmentRepository assignmentRepository;
    private final ExecutionPlanRepository executionPlanRepository;
    private final TaskRepository taskRepository;
    private final ChimeraAgentRepository agentRepository;
    private final AuditService auditService;

    public CampaignPlanningService(CampaignRepository campaignRepository,
                                   CampaignAgentAssignmentRepository assignmentRepository,
                                   ExecutionPlanRepository executionPlanRepository,
                                   TaskRepository taskRepository,
                                   ChimeraAgentRepository agentRepository,
                                   AuditService auditService) {
        this.campaignRepository = campaignRepository;
        this.assignmentRepository = assignmentRepository;
        this.executionPlanRepository = executionPlanRepository;
        this.taskRepository = taskRepository;
        this.agentRepository = agentRepository;
        this.auditService = auditService;
    }

    /**
     * Create a campaign with agent assignments and a draft execution plan.
     */
    @Transactional
    public Campaign createCampaign(UUID tenantWorkspaceId, String name, String goalDescription,
                                   String targetAudience, List<UUID> agentIds, UUID createdByUserId) {
        Campaign campaign = new Campaign(tenantWorkspaceId, name, goalDescription, targetAudience, createdByUserId);
        campaign = campaignRepository.save(campaign);

        for (UUID agentId : agentIds) {
            CampaignAgentAssignment assignment = new CampaignAgentAssignment(
                    campaign.getId(), agentId, "member");
            assignmentRepository.save(assignment);
        }

        // Generate a draft execution plan
        ExecutionPlan plan = new ExecutionPlan(campaign.getId(), 1,
                "Auto-generated plan for: " + goalDescription, "planner-v1");
        executionPlanRepository.save(plan);

        campaign.transitionTo("planned");
        campaignRepository.save(campaign);

        auditService.record(tenantWorkspaceId, "system", "campaign-planner",
                "campaign.created", "campaign", campaign.getId().toString(),
                Map.of("name", name, "agentCount", agentIds.size()), null);

        log.info("Campaign created: id={} name={}", campaign.getId(), name);
        return campaign;
    }

    /**
     * Approve the draft plan for a campaign and activate it.
     */
    @Transactional
    public Campaign approveCampaign(UUID campaignId) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new IllegalArgumentException("Campaign not found: " + campaignId));

        ExecutionPlan plan = executionPlanRepository.findByCampaignIdAndStatus(campaignId, "draft")
                .orElseThrow(() -> new IllegalStateException("No draft plan found for campaign: " + campaignId));

        plan.approve();
        executionPlanRepository.save(plan);

        // Generate initial tasks from the plan for each assigned agent
        List<CampaignAgentAssignment> assignments = assignmentRepository.findByCampaignId(campaignId);
        for (CampaignAgentAssignment assignment : assignments) {
            Task task = new Task(campaign.getTenantWorkspaceId(), campaignId,
                    assignment.getChimeraAgentId(), plan.getId(), "content_creation", "normal");
            taskRepository.save(task);
        }

        campaign.transitionTo("active");
        campaignRepository.save(campaign);

        auditService.record(campaign.getTenantWorkspaceId(), "system", "campaign-planner",
                "campaign.approved", "campaign", campaignId.toString(),
                Map.of("planId", plan.getId().toString()), null);

        log.info("Campaign approved and activated: id={}", campaignId);
        return campaign;
    }

    /**
     * Retrieve the current execution plan for a campaign.
     */
    @Transactional(readOnly = true)
    public ExecutionPlan getCurrentPlan(UUID campaignId) {
        List<ExecutionPlan> plans = executionPlanRepository.findByCampaignIdOrderByPlanVersionDesc(campaignId);
        if (plans.isEmpty()) {
            throw new IllegalArgumentException("No plan found for campaign: " + campaignId);
        }
        return plans.getFirst();
    }

    @Transactional(readOnly = true)
    public List<Campaign> listCampaigns(UUID tenantWorkspaceId) {
        return campaignRepository.findByTenantWorkspaceId(tenantWorkspaceId);
    }
}
