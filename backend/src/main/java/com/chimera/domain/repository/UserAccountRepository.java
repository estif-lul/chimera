package com.chimera.domain.repository;

import com.chimera.domain.model.auth.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for tenant-scoped user account lookups.
 */
public interface UserAccountRepository extends JpaRepository<UserAccount, UUID> {

    Optional<UserAccount> findByEmail(String email);

    Optional<UserAccount> findByTenantWorkspaceIdAndEmail(UUID tenantWorkspaceId, String email);
}
