package com.chimera.service.review;

import com.chimera.controller.dto.ReviewDecisionRequest;
import com.chimera.controller.dto.ReviewDecisionView;
import com.chimera.controller.dto.ReviewItemView;
import com.chimera.domain.model.review.ReviewDecision;
import com.chimera.domain.model.review.ReviewItem;
import com.chimera.domain.repository.ReviewDecisionRepository;
import com.chimera.domain.repository.ReviewItemRepository;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Handles reviewer queue listing, approve/reject/edit decisions, and immutable decision capture.
 */
@Service
public class ReviewDecisionService {

    private static final Logger log = LoggerFactory.getLogger(ReviewDecisionService.class);

    private final ReviewItemRepository reviewItemRepository;
    private final ReviewDecisionRepository reviewDecisionRepository;
    private final AuditService auditService;

    public ReviewDecisionService(ReviewItemRepository reviewItemRepository,
                                 ReviewDecisionRepository reviewDecisionRepository,
                                 AuditService auditService) {
        this.reviewItemRepository = reviewItemRepository;
        this.reviewDecisionRepository = reviewDecisionRepository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public List<ReviewItemView> listPending(UUID tenantWorkspaceId) {
        return reviewItemRepository.findByTenantWorkspaceIdAndQueueStatus(tenantWorkspaceId, "pending")
                .stream().map(this::toItemView).toList();
    }

    @Transactional(readOnly = true)
    public ReviewItemView getReviewItem(UUID reviewItemId) {
        ReviewItem item = reviewItemRepository.findById(reviewItemId)
                .orElseThrow(() -> new java.util.NoSuchElementException("Review item not found: " + reviewItemId));
        return toItemView(item);
    }

    @Transactional
    public ReviewDecisionView decide(UUID reviewItemId, UUID reviewerUserId,
                                     ReviewDecisionRequest request) {
        ReviewItem item = reviewItemRepository.findById(reviewItemId)
                .orElseThrow(() -> new IllegalArgumentException("Review item not found: " + reviewItemId));

        ReviewDecision decision = new ReviewDecision(
                reviewItemId, reviewerUserId,
                request.decisionType(), request.rationale(), request.editedContent());
        decision = reviewDecisionRepository.save(decision);

        item.resolve(request.decisionType().equals("approve") ? "approved"
                : request.decisionType().equals("reject") ? "rejected" : "edited");
        reviewItemRepository.save(item);

        auditService.record(item.getTenantWorkspaceId(), "user", reviewerUserId.toString(),
                "review.decided", "review_item", reviewItemId.toString(),
                Map.of("decision", request.decisionType()), null);

        log.info("Review decision recorded: item={} decision={}", reviewItemId, request.decisionType());
        return toDecisionView(decision);
    }

    private ReviewItemView toItemView(ReviewItem i) {
        double confidence = i.getConfidenceScore() != null ? i.getConfidenceScore().doubleValue() : 0.0;
        List<String> reasons = i.getReasonCodes() != null
                ? Arrays.asList(i.getReasonCodes())
                : List.of();
        return new ReviewItemView(
                i.getId(), i.getTaskId(), i.getQueueStatus(),
                confidence, i.getPolicyClassification(), reasons, null);
    }

    private ReviewDecisionView toDecisionView(ReviewDecision d) {
        return new ReviewDecisionView(
                d.getId(), d.getReviewItemId(),
                d.getDecisionType(), d.getRationale(), d.getCreatedAt());
    }
}
