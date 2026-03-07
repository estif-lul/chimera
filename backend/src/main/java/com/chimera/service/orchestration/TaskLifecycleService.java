package com.chimera.service.orchestration;

import com.chimera.domain.model.campaigns.Task;
import com.chimera.domain.repository.TaskRepository;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages task lifecycle transitions for the Planner/Worker/Judge orchestration pattern.
 * Uses JPA @Version for stale-write protection.
 */
@Service
public class TaskLifecycleService {

    private static final Logger log = LoggerFactory.getLogger(TaskLifecycleService.class);

    private final TaskRepository taskRepository;
    private final AuditService auditService;

    public TaskLifecycleService(TaskRepository taskRepository, AuditService auditService) {
        this.taskRepository = taskRepository;
        this.auditService = auditService;
    }

    /**
     * Transition a task to a new status with optimistic locking.
     *
     * @throws ObjectOptimisticLockingFailureException if the task was concurrently modified
     */
    @Transactional
    public Task transition(UUID taskId, String newStatus, int expectedVersion) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        if (task.getStateVersion() != expectedVersion) {
            throw new ObjectOptimisticLockingFailureException(
                    "Task state version mismatch: expected " + expectedVersion
                            + " but found " + task.getStateVersion(), null);
        }

        String oldStatus = task.getStatus();
        task.transitionTo(newStatus);
        task = taskRepository.save(task);

        auditService.record(task.getTenantWorkspaceId(), "system", "task-lifecycle",
                "task.status_changed", "task", taskId.toString(),
                Map.of("from", oldStatus, "to", newStatus, "version", task.getStateVersion()), null);

        log.info("Task {} transitioned: {} -> {} (v{})", taskId, oldStatus, newStatus, task.getStateVersion());
        return task;
    }

    /**
     * Record worker output on a task.
     */
    @Transactional
    public Task recordWorkerOutput(UUID taskId, Map<String, Object> output) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setWorkerOutput(output);
        return taskRepository.save(task);
    }

    /**
     * Record the judge decision summary.
     */
    @Transactional
    public Task recordJudgeDecision(UUID taskId, String summary) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
        task.setJudgeDecisionSummary(summary);
        return taskRepository.save(task);
    }

    @Transactional(readOnly = true)
    public List<Task> tasksByPlan(UUID executionPlanId) {
        return taskRepository.findByExecutionPlanId(executionPlanId);
    }

    @Transactional(readOnly = true)
    public List<Task> tasksByCampaign(UUID campaignId) {
        return taskRepository.findByCampaignId(campaignId);
    }
}
