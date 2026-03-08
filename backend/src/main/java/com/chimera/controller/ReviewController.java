package com.chimera.controller;

import com.chimera.controller.dto.ReviewDecisionRequest;
import com.chimera.controller.dto.ReviewDecisionView;
import com.chimera.controller.dto.ReviewItemView;
import com.chimera.service.review.ReviewDecisionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for the reviewer queue and decision capture.
 */
@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewDecisionService reviewDecisionService;
    private final com.chimera.service.DefaultTenantResolver tenantResolver;

    public ReviewController(ReviewDecisionService reviewDecisionService,
                            com.chimera.service.DefaultTenantResolver tenantResolver) {
        this.reviewDecisionService = reviewDecisionService;
        this.tenantResolver = tenantResolver;
    }

    @GetMapping
    public ResponseEntity<List<ReviewItemView>> listPending() {
        UUID tenantWorkspaceId = tenantResolver.resolveDefaultTenantWorkspaceId();
        return ResponseEntity.ok(reviewDecisionService.listPending(tenantWorkspaceId));
    }

    @GetMapping("/{reviewItemId}")
    public ResponseEntity<ReviewItemView> getReviewItem(@PathVariable UUID reviewItemId) {
        return ResponseEntity.ok(reviewDecisionService.getReviewItem(reviewItemId));
    }

    @PostMapping("/{reviewItemId}/decisions")
    public ResponseEntity<ReviewDecisionView> submitDecision(
            @PathVariable UUID reviewItemId,
            @Valid @RequestBody ReviewDecisionRequest request) {
        UUID reviewerUserId = tenantResolver.resolveDefaultUserId();
        ReviewDecisionView decision = reviewDecisionService.decide(reviewItemId, reviewerUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(decision);
    }
}
