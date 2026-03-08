package com.chimera.controller;

import com.chimera.controller.dto.SignalIngestRequest;
import com.chimera.domain.model.campaigns.ExternalSignal;
import com.chimera.service.DefaultTenantResolver;
import com.chimera.service.signals.SignalScoringService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST endpoint for ingesting MCP Resource-backed external signals.
 */
@RestController
@RequestMapping("/api/v1/signals")
public class SignalController {

    private final SignalScoringService signalScoringService;
    private final DefaultTenantResolver tenantResolver;

    public SignalController(SignalScoringService signalScoringService,
                            DefaultTenantResolver tenantResolver) {
        this.signalScoringService = signalScoringService;
        this.tenantResolver = tenantResolver;
    }

    @PostMapping
    public ResponseEntity<Void> ingestSignal(@Valid @RequestBody SignalIngestRequest request) {
        UUID tenantWorkspaceId = tenantResolver.resolveDefaultTenantWorkspaceId();

        signalScoringService.ingest(
                tenantWorkspaceId,
                request.sourcePlatform(),
                request.mcpResourceType(),
                request.mcpResourceUri(),
                request.signalType(),
                request.payloadSummary(),
                request.campaignId()
        );
        return ResponseEntity.accepted().build();
    }
}
