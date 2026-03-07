package com.chimera.domain.model.auth;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

/**
 * Authenticated human principal for operators, reviewers, and technical administrators.
 */
@Entity
@Table(name = "user_account")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(nullable = false, length = 320)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "auth_provider_type", nullable = false, length = 16)
    private String authProviderType = "local";

    @Column(name = "provider_subject", length = 512)
    private String providerSubject;

    @Column(name = "role_set", nullable = false, length = 128)
    private String roleSet = "operator";

    @Column(nullable = false, length = 16)
    private String status = "invited";

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected UserAccount() {}

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getAuthProviderType() { return authProviderType; }
    public String getProviderSubject() { return providerSubject; }
    public String getRoleSet() { return roleSet; }
    public String getStatus() { return status; }
    public Instant getLastLoginAt() { return lastLoginAt; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
        this.updatedAt = Instant.now();
    }
}
