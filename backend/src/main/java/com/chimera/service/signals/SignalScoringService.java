package com.chimera.service.signals;

import com.chimera.domain.model.campaigns.ExternalSignal;
import com.chimera.domain.repository.ExternalSignalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Ingests MCP Resource-backed signals and scores their relevance to active campaigns.
 */
@Service
public class SignalScoringService {

    private static final Logger log = LoggerFactory.getLogger(SignalScoringService.class);

    private final ExternalSignalRepository signalRepository;

    public SignalScoringService(ExternalSignalRepository signalRepository) {
        this.signalRepository = signalRepository;
    }

    /**
     * Ingest a new external signal and compute a preliminary relevance score.
     */
    @Transactional
    public ExternalSignal ingest(UUID tenantWorkspaceId, String sourcePlatform,
                                 String mcpResourceType, String mcpResourceUri,
                                 String signalType, Map<String, Object> payloadSummary,
                                 UUID campaignId) {
        ExternalSignal signal = new ExternalSignal(
                tenantWorkspaceId, sourcePlatform, mcpResourceType,
                mcpResourceUri, signalType, payloadSummary);

        if (campaignId != null) {
            signal.setCampaignId(campaignId);
        }

        BigDecimal score = computeRelevanceScore(signalType, payloadSummary);
        signal.setRelevanceScore(score);

        signal = signalRepository.save(signal);
        log.info("Signal ingested: id={} type={} score={}", signal.getId(), signalType, score);
        return signal;
    }

    /**
     * Retrieve unprocessed signals for a tenant.
     */
    @Transactional(readOnly = true)
    public List<ExternalSignal> pendingSignals(UUID tenantWorkspaceId) {
        return signalRepository.findByTenantWorkspaceIdAndProcessedAtIsNull(tenantWorkspaceId);
    }

    /**
     * Compute a relevance score for the signal based on type and payload heuristics.
     * In production this would integrate with the Planner's reasoning or an ML model.
     */
    private BigDecimal computeRelevanceScore(String signalType, Map<String, Object> payload) {
        return switch (signalType) {
            case "mention" -> new BigDecimal("0.700");
            case "trend" -> new BigDecimal("0.500");
            case "engagement" -> new BigDecimal("0.800");
            default -> new BigDecimal("0.300");
        };
    }
}
