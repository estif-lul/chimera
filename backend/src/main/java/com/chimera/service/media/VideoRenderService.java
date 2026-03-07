package com.chimera.service.media;

import com.chimera.domain.model.media.VideoRenderJob;
import com.chimera.domain.repository.VideoRenderJobRepository;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages two-tier video rendering: Tier 1 living portraits and Tier 2 hero videos.
 * Delegates to MCP video tool providers (Runway, Luma).
 */
@Service
public class VideoRenderService {

    private static final Logger log = LoggerFactory.getLogger(VideoRenderService.class);

    private final VideoRenderJobRepository renderJobRepository;
    private final AuditService auditService;

    public VideoRenderService(VideoRenderJobRepository renderJobRepository,
                              AuditService auditService) {
        this.renderJobRepository = renderJobRepository;
        this.auditService = auditService;
    }

    /**
     * Submit a new video render job for a task.
     */
    @Transactional
    public VideoRenderJob submit(UUID taskId, UUID agentId, UUID campaignId,
                                 String renderTier, String provider,
                                 String sourcePrompt, String sourceImageAssetId) {
        if ("tier_1_living_portrait".equals(renderTier) && (sourceImageAssetId == null || sourceImageAssetId.isBlank())) {
            throw new IllegalArgumentException("Tier 1 jobs require a sourceImageAssetId");
        }
        if ("tier_2_hero_video".equals(renderTier) && (sourcePrompt == null || sourcePrompt.isBlank())) {
            throw new IllegalArgumentException("Tier 2 jobs require a non-empty sourcePrompt");
        }

        VideoRenderJob job = new VideoRenderJob(taskId, agentId, campaignId, renderTier, provider);
        job.setSourcePrompt(sourcePrompt);
        job.setSourceImageAssetId(sourceImageAssetId);
        job.transitionTo("submitted");

        job = renderJobRepository.save(job);

        auditService.record(null, "system", "video-render",
                "video_render.submitted", "video_render_job", job.getId().toString(),
                Map.of("tier", renderTier, "provider", provider), null);

        log.info("Video render job submitted: id={} tier={} provider={}", job.getId(), renderTier, provider);
        return job;
    }

    /**
     * Mark a render job as completed with its output URI and cost.
     */
    @Transactional
    public VideoRenderJob complete(UUID jobId, String assetUri, BigDecimal cost, String currency) {
        VideoRenderJob job = renderJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Render job not found: " + jobId));
        job.complete(assetUri, cost, currency);
        return renderJobRepository.save(job);
    }

    @Transactional(readOnly = true)
    public List<VideoRenderJob> activeJobs() {
        return renderJobRepository.findByStatus("submitted");
    }
}
