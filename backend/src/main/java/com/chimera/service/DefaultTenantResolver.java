package com.chimera.service;

import com.chimera.domain.model.auth.UserAccount;
import com.chimera.domain.model.tenant.TenantWorkspace;
import com.chimera.domain.repository.TenantWorkspaceRepository;
import com.chimera.domain.repository.UserAccountRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Resolves the default tenant workspace and admin user from seeded data.
 * Placeholder until proper principal-based resolution is implemented.
 */
@Component
public class DefaultTenantResolver {

    private final TenantWorkspaceRepository tenantWorkspaceRepository;
    private final UserAccountRepository userAccountRepository;

    public DefaultTenantResolver(TenantWorkspaceRepository tenantWorkspaceRepository,
                                 UserAccountRepository userAccountRepository) {
        this.tenantWorkspaceRepository = tenantWorkspaceRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /** Returns the ID of the default seeded tenant workspace. */
    public UUID resolveDefaultTenantWorkspaceId() {
        TenantWorkspace tenant = tenantWorkspaceRepository.findBySlug("default")
                .orElseThrow(() -> new IllegalStateException("Default tenant workspace not found"));
        return tenant.getId();
    }

    /** Returns the ID of the default admin user within the default tenant. */
    public UUID resolveDefaultUserId() {
        UUID tenantId = resolveDefaultTenantWorkspaceId();
        UserAccount user = userAccountRepository.findByTenantWorkspaceIdAndEmail(tenantId, "admin@chimera.local")
                .orElseThrow(() -> new IllegalStateException("Default admin user not found"));
        return user.getId();
    }
}
