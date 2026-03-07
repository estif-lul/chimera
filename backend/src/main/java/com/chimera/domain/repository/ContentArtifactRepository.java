package com.chimera.domain.repository;

import com.chimera.domain.model.media.ContentArtifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for content artifact lookups by task and campaign.
 */
public interface ContentArtifactRepository extends JpaRepository<ContentArtifact, UUID> {

    List<ContentArtifact> findByTaskId(UUID taskId);

    List<ContentArtifact> findByCampaignId(UUID campaignId);
}
