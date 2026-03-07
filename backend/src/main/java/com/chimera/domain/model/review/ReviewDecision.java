package com.chimera.domain.model.review;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable record of a human review action (approve, reject, or edit).
 */
@Entity
@Table(name = "review_decision")
public class ReviewDecision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "review_item_id", nullable = false)
    private UUID reviewItemId;

    @Column(name = "reviewed_by_user_id", nullable = false)
    private UUID reviewedByUserId;

    @Column(name = "decision_type", nullable = false, length = 16)
    private String decisionType;

    @Column(columnDefinition = "TEXT")
    private String rationale;

    @Column(name = "edit_summary", columnDefinition = "TEXT")
    private String editSummary;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ReviewDecision() {}

    public ReviewDecision(UUID reviewItemId, UUID reviewedByUserId,
                          String decisionType, String rationale, String editSummary) {
        this.reviewItemId = reviewItemId;
        this.reviewedByUserId = reviewedByUserId;
        this.decisionType = decisionType;
        this.rationale = rationale;
        this.editSummary = editSummary;
    }

    public UUID getId() { return id; }
    public UUID getReviewItemId() { return reviewItemId; }
    public UUID getReviewedByUserId() { return reviewedByUserId; }
    public String getDecisionType() { return decisionType; }
    public String getRationale() { return rationale; }
    public String getEditSummary() { return editSummary; }
    public Instant getCreatedAt() { return createdAt; }
}
