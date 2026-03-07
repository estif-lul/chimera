package com.chimera.service.agents;

import com.chimera.controller.dto.SoulDefinitionInput;
import com.chimera.domain.model.agents.SoulDefinition;
import com.chimera.domain.repository.SoulDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Loads and versions SOUL.md persona blueprints. Immutable persona fields
 * cannot be mutated by automated workflows.
 */
@Service
public class SoulDefinitionService {

    private static final Logger log = LoggerFactory.getLogger(SoulDefinitionService.class);

    private final SoulDefinitionRepository soulRepository;

    public SoulDefinitionService(SoulDefinitionRepository soulRepository) {
        this.soulRepository = soulRepository;
    }

    /**
     * Create a new version of a soul definition from inline input.
     */
    @Transactional
    public SoulDefinition createFromInput(UUID tenantWorkspaceId, String personaSlug,
                                          SoulDefinitionInput input) {
        int nextVersion = soulRepository
                .findTopByTenantWorkspaceIdAndPersonaSlugOrderByVersionDesc(tenantWorkspaceId, personaSlug)
                .map(s -> s.getVersion() + 1)
                .orElse(1);

        SoulDefinition soul = new SoulDefinition(
                tenantWorkspaceId, personaSlug, nextVersion,
                input.backstory(),
                String.join(",", input.voiceTone()),
                String.join(",", input.coreBeliefsAndValues()),
                String.join(",", input.directives()),
                null
        );
        soul = soulRepository.save(soul);
        log.info("Soul definition created: persona={} version={}", personaSlug, nextVersion);
        return soul;
    }

    @Transactional(readOnly = true)
    public SoulDefinition getLatestVersion(UUID tenantWorkspaceId, String personaSlug) {
        return soulRepository
                .findTopByTenantWorkspaceIdAndPersonaSlugOrderByVersionDesc(tenantWorkspaceId, personaSlug)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Soul definition not found for persona: " + personaSlug));
    }

    @Transactional(readOnly = true)
    public SoulDefinition getById(UUID soulDefinitionId) {
        return soulRepository.findById(soulDefinitionId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Soul definition not found: " + soulDefinitionId));
    }
}
