package com.chimera.service.orchestration;

import com.chimera.domain.model.campaigns.Task;
import com.chimera.domain.model.media.ContentArtifact;
import com.chimera.domain.repository.ContentArtifactRepository;
import com.chimera.domain.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Generates content artifacts for tasks, applying disclosure labeling,
 * and selecting the appropriate generation mode (native text, MCP image tool, MCP video tool).
 */
@Service
public class ContentGenerationService {

    private static final Logger log = LoggerFactory.getLogger(ContentGenerationService.class);

    private final TaskRepository taskRepository;
    private final ContentArtifactRepository contentArtifactRepository;

    public ContentGenerationService(TaskRepository taskRepository,
                                    ContentArtifactRepository contentArtifactRepository) {
        this.taskRepository = taskRepository;
        this.contentArtifactRepository = contentArtifactRepository;
    }

    /**
     * Generate a content artifact for a task. The generation mode is chosen
     * based on the task type and video render tier.
     */
    @Transactional
    public ContentArtifact generate(UUID taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));

        String generationMode = resolveGenerationMode(task);
        String artifactType = resolveArtifactType(generationMode);

        ContentArtifact artifact = new ContentArtifact(
                taskId, task.getChimeraAgentId(), task.getCampaignId(),
                artifactType, generationMode);

        artifact.setPreviewText("Generated content for task: " + taskId);
        artifact.setConfidenceScore(new BigDecimal("0.850"));
        artifact.setDisclosureMode("ai_generated");
        artifact.setPolicyClassification("standard");

        artifact = contentArtifactRepository.save(artifact);
        log.info("Content artifact created: id={} mode={} task={}", artifact.getId(), generationMode, taskId);
        return artifact;
    }

    private String resolveGenerationMode(Task task) {
        if (task.getVideoRenderTier() != null) {
            return "mcp_video_tool";
        }
        if ("image_creation".equals(task.getTaskType())) {
            return "mcp_image_tool";
        }
        return "native_text";
    }

    private String resolveArtifactType(String generationMode) {
        return switch (generationMode) {
            case "mcp_video_tool" -> "video";
            case "mcp_image_tool" -> "image";
            default -> "text";
        };
    }
}
