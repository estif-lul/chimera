package com.chimera.domain.repository;

import com.chimera.domain.model.review.ReviewItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for review item queue queries.
 */
public interface ReviewItemRepository extends JpaRepository<ReviewItem, UUID> {

    List<ReviewItem> findByTenantWorkspaceIdAndQueueStatus(UUID tenantWorkspaceId, String queueStatus);

    List<ReviewItem> findByTaskId(UUID taskId);
}
