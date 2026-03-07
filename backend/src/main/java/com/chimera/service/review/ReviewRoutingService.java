package com.chimera.service.review;

import com.chimera.domain.model.review.ReviewItem;
import com.chimera.domain.repository.ReviewItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Routes tasks and artifacts into the human review queue based on confidence
 * scoring and policy evaluation.
 */
@Service
public class ReviewRoutingService {

    private static final Logger log = LoggerFactory.getLogger(ReviewRoutingService.class);

    private final ReviewItemRepository reviewItemRepository;

    public ReviewRoutingService(ReviewItemRepository reviewItemRepository) {
        this.reviewItemRepository = reviewItemRepository;
    }

    /**
     * Create a review item for a content artifact that requires human review.
     */
    @Transactional
    public ReviewItem routeForContentReview(UUID tenantWorkspaceId, UUID taskId,
                                            UUID contentArtifactId, BigDecimal confidenceScore,
                                            String policyClassification) {
        ReviewItem item = new ReviewItem(
                tenantWorkspaceId, taskId, contentArtifactId,
                null, confidenceScore, policyClassification);
        item = reviewItemRepository.save(item);
        log.info("Review item created for content: id={} task={}", item.getId(), taskId);
        return item;
    }

    /**
     * Create a review item for a transaction request that requires human review.
     */
    @Transactional
    public ReviewItem routeForTransactionReview(UUID tenantWorkspaceId, UUID taskId,
                                                UUID transactionRequestId, BigDecimal confidenceScore,
                                                String policyClassification) {
        ReviewItem item = new ReviewItem(
                tenantWorkspaceId, taskId, null,
                transactionRequestId, confidenceScore, policyClassification);
        item = reviewItemRepository.save(item);
        log.info("Review item created for transaction: id={} task={}", item.getId(), taskId);
        return item;
    }
}
