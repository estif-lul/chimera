package com.chimera.domain.model.campaigns;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Atomic unit of orchestrated work handled by Planner, Worker, and Judge responsibilities.
 * Uses optimistic concurrency via {@code stateVersion}.
 */
@Entity
@Table(name = "task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Column(name = "chimera_agent_id", nullable = false)
    private UUID chimeraAgentId;

    @Column(name = "execution_plan_id", nullable = false)
    private UUID executionPlanId;

    @Column(name = "task_type", nullable = false, length = 64)
    private String taskType;

    @Column(nullable = false, length = 16)
    private String priority = "normal";

    @Column(name = "video_render_tier", length = 32)
    private String videoRenderTier;

    @Column(nullable = false, length = 32)
    private String status = "pending";

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "planner_context", columnDefinition = "jsonb")
    private Map<String, Object> plannerContext;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "worker_output", columnDefinition = "jsonb")
    private Map<String, Object> workerOutput;

    @Column(name = "judge_decision_summary", columnDefinition = "TEXT")
    private String judgeDecisionSummary;

    @Version
    @Column(name = "state_version", nullable = false)
    private int stateVersion = 1;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Task() {}

    public Task(UUID tenantWorkspaceId, UUID campaignId, UUID chimeraAgentId,
                UUID executionPlanId, String taskType, String priority) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.campaignId = campaignId;
        this.chimeraAgentId = chimeraAgentId;
        this.executionPlanId = executionPlanId;
        this.taskType = taskType;
        this.priority = priority;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public UUID getCampaignId() { return campaignId; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public UUID getExecutionPlanId() { return executionPlanId; }
    public String getTaskType() { return taskType; }
    public String getPriority() { return priority; }
    public String getVideoRenderTier() { return videoRenderTier; }
    public String getStatus() { return status; }
    public Map<String, Object> getPlannerContext() { return plannerContext; }
    public Map<String, Object> getWorkerOutput() { return workerOutput; }
    public String getJudgeDecisionSummary() { return judgeDecisionSummary; }
    public int getStateVersion() { return stateVersion; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    /**
     * Transition to a new status with stale-write protection.
     * JPA's @Version ensures optimistic concurrency.
     */
    public void transitionTo(String newStatus) {
        this.status = newStatus;
        this.updatedAt = Instant.now();
    }

    public void setVideoRenderTier(String tier) {
        this.videoRenderTier = tier;
    }

    public void setWorkerOutput(Map<String, Object> output) {
        this.workerOutput = output;
        this.updatedAt = Instant.now();
    }

    public void setJudgeDecisionSummary(String summary) {
        this.judgeDecisionSummary = summary;
        this.updatedAt = Instant.now();
    }

    public void setPlannerContext(Map<String, Object> context) {
        this.plannerContext = context;
    }
}
