package com.chimera.controller;

import com.chimera.controller.dto.*;
import com.chimera.domain.model.campaigns.Campaign;
import com.chimera.domain.model.campaigns.ExecutionPlan;
import com.chimera.domain.model.campaigns.Task;
import com.chimera.service.DefaultTenantResolver;
import com.chimera.service.campaign.CampaignPlanningService;
import com.chimera.service.orchestration.TaskLifecycleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for campaigns, execution plans, and plan approval.
 */
@RestController
@RequestMapping("/api/v1/campaigns")
public class CampaignController {

    private final CampaignPlanningService campaignPlanningService;
    private final TaskLifecycleService taskLifecycleService;
    private final DefaultTenantResolver tenantResolver;

    public CampaignController(CampaignPlanningService campaignPlanningService,
                               TaskLifecycleService taskLifecycleService,
                               DefaultTenantResolver tenantResolver) {
        this.campaignPlanningService = campaignPlanningService;
        this.taskLifecycleService = taskLifecycleService;
        this.tenantResolver = tenantResolver;
    }

    @PostMapping
    public ResponseEntity<CampaignView> createCampaign(@Valid @RequestBody CreateCampaignRequest request) {
        UUID tenantWorkspaceId = tenantResolver.resolveDefaultTenantWorkspaceId();
        UUID userId = tenantResolver.resolveDefaultUserId();

        Campaign campaign = campaignPlanningService.createCampaign(
                tenantWorkspaceId, request.name(), request.goalDescription(),
                request.targetAudience(), request.agentIds(), userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(toView(campaign));
    }

    @GetMapping
    public ResponseEntity<List<CampaignView>> listCampaigns() {
        UUID tenantWorkspaceId = tenantResolver.resolveDefaultTenantWorkspaceId();
        List<CampaignView> campaigns = campaignPlanningService.listCampaigns(tenantWorkspaceId)
                .stream().map(this::toView).toList();
        return ResponseEntity.ok(campaigns);
    }

    @GetMapping("/{campaignId}/plan")
    public ResponseEntity<ExecutionPlanView> getCampaignPlan(@PathVariable UUID campaignId) {
        ExecutionPlan plan = campaignPlanningService.getCurrentPlan(campaignId);
        List<Task> tasks = taskLifecycleService.tasksByPlan(plan.getId());

        List<TaskSummary> taskSummaries = tasks.stream().map(t ->
                new TaskSummary(t.getId(), t.getTaskType(), t.getPriority(),
                        t.getVideoRenderTier(), t.getStatus(), t.getStateVersion())
        ).toList();

        ExecutionPlanView view = new ExecutionPlanView(
                plan.getId(), plan.getCampaignId(), plan.getPlanVersion(),
                plan.getStatus(), plan.getSummary(), List.of(), taskSummaries);

        return ResponseEntity.ok(view);
    }

    @PostMapping("/{campaignId}/approve")
    public ResponseEntity<Void> approveCampaign(@PathVariable UUID campaignId) {
        campaignPlanningService.approveCampaign(campaignId);
        return ResponseEntity.accepted().build();
    }

    private CampaignView toView(Campaign c) {
        return new CampaignView(
                c.getId(), c.getTenantWorkspaceId(), c.getName(),
                c.getStatus(), c.getGoalDescription(), c.getTargetAudience(),
                null, null);
    }
}
