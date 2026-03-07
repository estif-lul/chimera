package com.chimera.domain.repository;

import com.chimera.domain.model.media.VideoRenderJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for video render job queries.
 */
public interface VideoRenderJobRepository extends JpaRepository<VideoRenderJob, UUID> {

    List<VideoRenderJob> findByTaskId(UUID taskId);

    List<VideoRenderJob> findByCampaignId(UUID campaignId);

    List<VideoRenderJob> findByStatus(String status);
}
