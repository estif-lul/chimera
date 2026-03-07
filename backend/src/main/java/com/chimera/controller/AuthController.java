package com.chimera.controller;

import com.chimera.controller.dto.LocalLoginRequest;
import com.chimera.controller.dto.SessionView;
import com.chimera.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Handles local login and session retrieval.
 * OIDC flows are managed by Spring Security OAuth2 login configuration.
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/local/login")
    public ResponseEntity<SessionView> localLogin(@Valid @RequestBody LocalLoginRequest request) {
        SessionView session = authService.authenticateLocal(request);
        return ResponseEntity.ok(session);
    }

    @GetMapping("/session")
    public ResponseEntity<SessionView> currentSession() {
        SessionView session = authService.currentSession();
        return ResponseEntity.ok(session);
    }
}
