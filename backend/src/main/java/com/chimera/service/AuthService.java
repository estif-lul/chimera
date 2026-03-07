package com.chimera.service;

import com.chimera.controller.dto.LocalLoginRequest;
import com.chimera.controller.dto.SessionView;
import com.chimera.domain.model.auth.UserAccount;
import com.chimera.domain.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Authentication service supporting local and OIDC session creation.
 */
@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserAccountRepository userAccountRepository, PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticate using local email/password credentials.
     *
     * @param request login credentials
     * @return session view for the authenticated user
     * @throws IllegalArgumentException when credentials are invalid
     */
    public SessionView authenticateLocal(LocalLoginRequest request) {
        UserAccount user = userAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }
        return new SessionView(
                user.getId(),
                user.getTenantWorkspaceId(),
                List.of(user.getRoleSet().split(",")),
                user.getAuthProviderType()
        );
    }

    /**
     * Returns the current authenticated session.
     * In a full implementation this reads from the security context.
     */
    public SessionView currentSession() {
        // Stub — will be replaced with SecurityContextHolder-based resolution
        throw new UnsupportedOperationException("Not implemented: resolve from security context");
    }
}
