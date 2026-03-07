package com.chimera.service.agents;

import com.chimera.domain.model.agents.ChimeraAgent;
import com.chimera.domain.repository.ChimeraAgentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Manages agent profile updates, enforcing immutability of persona fields.
 * Only mutable biography may be updated by automated workflows.
 */
@Service
public class AgentProfileService {

    private static final Logger log = LoggerFactory.getLogger(AgentProfileService.class);

    private final ChimeraAgentRepository agentRepository;

    public AgentProfileService(ChimeraAgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    /**
     * Update the mutable biography summary. This is the only persona-adjacent
     * field that automated workflows may change.
     */
    @Transactional
    public ChimeraAgent updateBiography(UUID agentId, String newBiography) {
        ChimeraAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));
        agent.setMutableBiographySummary(newBiography);
        agent = agentRepository.save(agent);
        log.info("Agent biography updated: id={}", agentId);
        return agent;
    }
}
