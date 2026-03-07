package com.chimera.controller;

import com.chimera.controller.dto.AgentView;
import com.chimera.controller.dto.CreateAgentRequest;
import com.chimera.service.agents.AgentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for managing Chimera agents.
 */
@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping
    public ResponseEntity<AgentView> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        // TODO: resolve tenantWorkspaceId from authenticated principal
        UUID tenantWorkspaceId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        AgentView agent = agentService.createAgent(tenantWorkspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(agent);
    }

    @GetMapping
    public ResponseEntity<List<AgentView>> listAgents() {
        UUID tenantWorkspaceId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        return ResponseEntity.ok(agentService.listAgents(tenantWorkspaceId));
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<AgentView> getAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(agentService.getAgent(agentId));
    }
}
