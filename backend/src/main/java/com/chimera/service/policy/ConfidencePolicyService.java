package com.chimera.service.policy;

import com.chimera.domain.model.policy.ConfidencePolicy;
import com.chimera.domain.repository.ConfidencePolicyRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Evaluates confidence scores against tenant or campaign policies
 * to determine auto-approve or mandatory human review routing.
 */
@Service
public class ConfidencePolicyService {

    private final ConfidencePolicyRepository policyRepository;

    public ConfidencePolicyService(ConfidencePolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }

    /**
     * Determine the routing decision for a given confidence score.
     *
     * @return "auto_approve", "review", or "reject"
     */
    public String evaluate(UUID tenantWorkspaceId, UUID campaignId, BigDecimal confidenceScore) {
        ConfidencePolicy policy = resolvePolicy(tenantWorkspaceId, campaignId);

        if (confidenceScore.compareTo(policy.getAutoApproveThreshold()) >= 0) {
            return "auto_approve";
        }
        if (confidenceScore.compareTo(policy.getReviewThreshold()) >= 0) {
            return "review";
        }
        return "reject";
    }

    private ConfidencePolicy resolvePolicy(UUID tenantWorkspaceId, UUID campaignId) {
        if (campaignId != null) {
            return policyRepository.findByCampaignId(campaignId)
                    .orElseGet(() -> tenantDefault(tenantWorkspaceId));
        }
        return tenantDefault(tenantWorkspaceId);
    }

    private ConfidencePolicy tenantDefault(UUID tenantWorkspaceId) {
        return policyRepository.findByTenantWorkspaceIdAndCampaignIdIsNull(tenantWorkspaceId)
                .orElseGet(() -> new ConfidencePolicy(
                        tenantWorkspaceId,
                        new BigDecimal("0.900"),
                        new BigDecimal("0.600"),
                        new String[0]));
    }
}
