package com.chimera.domain.model.campaigns;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Join entity linking agents to campaigns with an assignment role.
 */
@Entity
@Table(name = "campaign_agent_assignment")
@IdClass(CampaignAgentAssignment.CampaignAgentId.class)
public class CampaignAgentAssignment {

    @Id
    @Column(name = "campaign_id", nullable = false)
    private UUID campaignId;

    @Id
    @Column(name = "chimera_agent_id", nullable = false)
    private UUID chimeraAgentId;

    @Column(name = "assignment_role", nullable = false, length = 64)
    private String assignmentRole = "member";

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected CampaignAgentAssignment() {}

    public CampaignAgentAssignment(UUID campaignId, UUID chimeraAgentId, String assignmentRole) {
        this.campaignId = campaignId;
        this.chimeraAgentId = chimeraAgentId;
        this.assignmentRole = assignmentRole;
    }

    public UUID getCampaignId() { return campaignId; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public String getAssignmentRole() { return assignmentRole; }
    public Instant getCreatedAt() { return createdAt; }

    /**
     * Composite primary key for CampaignAgentAssignment.
     */
    public static class CampaignAgentId implements Serializable {
        private UUID campaignId;
        private UUID chimeraAgentId;

        public CampaignAgentId() {}

        public CampaignAgentId(UUID campaignId, UUID chimeraAgentId) {
            this.campaignId = campaignId;
            this.chimeraAgentId = chimeraAgentId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CampaignAgentId that)) return false;
            return Objects.equals(campaignId, that.campaignId)
                    && Objects.equals(chimeraAgentId, that.chimeraAgentId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(campaignId, chimeraAgentId);
        }
    }
}
