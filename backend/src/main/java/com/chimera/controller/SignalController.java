package com.chimera.controller;

import com.chimera.controller.dto.SignalIngestRequest;
import com.chimera.domain.model.campaigns.ExternalSignal;
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

    public SignalController(SignalScoringService signalScoringService) {
        this.signalScoringService = signalScoringService;
    }

    @PostMapping
    public ResponseEntity<Void> ingestSignal(@Valid @RequestBody SignalIngestRequest request) {
        // TODO: resolve tenantWorkspaceId from authenticated principal
        UUID tenantWorkspaceId = UUID.fromString("00000000-0000-0000-0000-000000000001");

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
