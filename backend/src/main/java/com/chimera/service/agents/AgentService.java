package com.chimera.service.agents;

import com.chimera.controller.dto.CreateAgentRequest;
import com.chimera.controller.dto.AgentView;
import com.chimera.controller.dto.SoulDefinitionInput;
import com.chimera.domain.model.agents.ChimeraAgent;
import com.chimera.domain.model.agents.SoulDefinition;
import com.chimera.domain.repository.ChimeraAgentRepository;
import com.chimera.domain.repository.SoulDefinitionRepository;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Manages agent lifecycle: creation, activation, and profile queries.
 */
@Service
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final ChimeraAgentRepository agentRepository;
    private final SoulDefinitionRepository soulRepository;
    private final AuditService auditService;

    public AgentService(ChimeraAgentRepository agentRepository,
                        SoulDefinitionRepository soulRepository,
                        AuditService auditService) {
        this.agentRepository = agentRepository;
        this.soulRepository = soulRepository;
        this.auditService = auditService;
    }

    /**
     * Create a new agent with an inline soul definition.
     */
    @Transactional
    public AgentView createAgent(UUID tenantWorkspaceId, CreateAgentRequest request) {
        SoulDefinitionInput soulInput = request.soulDefinition();

        SoulDefinition soul = new SoulDefinition(
                tenantWorkspaceId,
                request.personaSlug(),
                1,
                soulInput.backstory(),
                String.join(",", soulInput.voiceTone()),
                String.join(",", soulInput.coreBeliefsAndValues()),
                String.join(",", soulInput.directives()),
                null
        );
        soul = soulRepository.save(soul);

        ChimeraAgent agent = new ChimeraAgent(
                tenantWorkspaceId,
                request.displayName(),
                request.personaSlug(),
                soul.getId(),
                soul.getVersion(),
                request.visualReferenceId()
        );
        agent.activate();
        agent = agentRepository.save(agent);

        auditService.record(tenantWorkspaceId, "system", "agent-service",
                "agent.created", "chimera_agent", agent.getId().toString(),
                Map.of("displayName", request.displayName()), null);

        log.info("Agent created: id={} persona={}", agent.getId(), request.personaSlug());
        return toView(agent);
    }

    @Transactional(readOnly = true)
    public List<AgentView> listAgents(UUID tenantWorkspaceId) {
        return agentRepository.findByTenantWorkspaceId(tenantWorkspaceId)
                .stream().map(this::toView).toList();
    }

    @Transactional(readOnly = true)
    public AgentView getAgent(UUID agentId) {
        ChimeraAgent agent = agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));
        return toView(agent);
    }

    private AgentView toView(ChimeraAgent agent) {
        return new AgentView(
                agent.getId(),
                agent.getTenantWorkspaceId(),
                agent.getDisplayName(),
                agent.getPersonaSlug(),
                agent.getStatus(),
                String.valueOf(agent.getSoulDocumentVersion()),
                agent.getMutableBiographySummary(),
                agent.getVisualReferenceId()
        );
    }
}
