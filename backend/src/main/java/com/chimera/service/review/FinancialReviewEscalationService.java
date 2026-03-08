package com.chimera.service.review;

import com.chimera.domain.model.review.ReviewItem;
import com.chimera.domain.repository.ReviewItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Escalates wallet transactions that violate spend policy into the review queue.
 */
@Service
public class FinancialReviewEscalationService {

    private static final Logger log = LoggerFactory.getLogger(FinancialReviewEscalationService.class);

    private final ReviewItemRepository reviewItemRepository;

    public FinancialReviewEscalationService(ReviewItemRepository reviewItemRepository) {
        this.reviewItemRepository = reviewItemRepository;
    }

    /**
     * Create a review item for a transaction that failed policy checks.
     */
    @Transactional
    public ReviewItem escalate(UUID tenantWorkspaceId, UUID transactionRequestId) {
        ReviewItem item = new ReviewItem(tenantWorkspaceId, null, null, transactionRequestId, null, "transaction_policy_violation");
        item = reviewItemRepository.save(item);
        log.info("Financial review escalation: reviewItem={} txRequest={}", item.getId(), transactionRequestId);
        return item;
    }
}
