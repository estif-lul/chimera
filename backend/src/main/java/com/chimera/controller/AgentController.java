package com.chimera.controller;

import com.chimera.controller.dto.AgentView;
import com.chimera.controller.dto.CreateAgentRequest;
import com.chimera.service.DefaultTenantResolver;
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
    private final DefaultTenantResolver tenantResolver;

    public AgentController(AgentService agentService, DefaultTenantResolver tenantResolver) {
        this.agentService = agentService;
        this.tenantResolver = tenantResolver;
    }

    @PostMapping
    public ResponseEntity<AgentView> createAgent(@Valid @RequestBody CreateAgentRequest request) {
        UUID tenantWorkspaceId = tenantResolver.resolveDefaultTenantWorkspaceId();
        AgentView agent = agentService.createAgent(tenantWorkspaceId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(agent);
    }

    @GetMapping
    public ResponseEntity<List<AgentView>> listAgents() {
        UUID tenantWorkspaceId = tenantResolver.resolveDefaultTenantWorkspaceId();
        return ResponseEntity.ok(agentService.listAgents(tenantWorkspaceId));
    }

    @GetMapping("/{agentId}")
    public ResponseEntity<AgentView> getAgent(@PathVariable UUID agentId) {
        return ResponseEntity.ok(agentService.getAgent(agentId));
    }
}
