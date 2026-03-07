package com.chimera.controller;

import com.chimera.controller.dto.MemoryWritebackView;
import com.chimera.domain.model.agents.AgentMemoryRecord;
import com.chimera.domain.repository.ChimeraAgentRepository;
import com.chimera.service.orchestration.JudgeMemoryWritebackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST endpoint for agent memory write-back history.
 */
@RestController
@RequestMapping("/api/v1/agents/{agentId}/memory")
public class MemoryController {

    private final JudgeMemoryWritebackService writebackService;
    private final ChimeraAgentRepository agentRepository;

    public MemoryController(JudgeMemoryWritebackService writebackService,
                            ChimeraAgentRepository agentRepository) {
        this.writebackService = writebackService;
        this.agentRepository = agentRepository;
    }

    @GetMapping
    public ResponseEntity<List<MemoryWritebackView>> getWritebackHistory(@PathVariable UUID agentId) {
        agentRepository.findById(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentId));

        List<MemoryWritebackView> history = writebackService.getWritebackHistory(agentId)
                .stream().map(this::toView).toList();
        return ResponseEntity.ok(history);
    }

    private MemoryWritebackView toView(AgentMemoryRecord r) {
        return new MemoryWritebackView(
                r.getId(), r.getChimeraAgentId(), r.getMemoryType(),
                r.getStorageBackend(), r.getContent(), r.getSourceTaskId(),
                r.getEngagementScore() != null ? r.getEngagementScore().doubleValue() : null,
                r.getCreatedAt());
    }
}
