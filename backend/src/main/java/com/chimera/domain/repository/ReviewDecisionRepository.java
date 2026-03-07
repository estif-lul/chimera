package com.chimera.domain.repository;

import com.chimera.domain.model.review.ReviewDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Append-only repository for immutable review decisions.
 */
public interface ReviewDecisionRepository extends JpaRepository<ReviewDecision, UUID> {

    List<ReviewDecision> findByReviewItemId(UUID reviewItemId);
}
