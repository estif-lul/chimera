package com.chimera.config.seed;

import com.chimera.domain.model.auth.UserAccount;
import com.chimera.domain.model.policy.ConfidencePolicy;
import com.chimera.domain.model.tenant.TenantWorkspace;
import com.chimera.domain.repository.ConfidencePolicyRepository;
import com.chimera.domain.repository.TenantWorkspaceRepository;
import com.chimera.domain.repository.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Seeds the default tenant, admin user, and confidence policy on first startup
 * when {@code chimera.seed.enabled=true}. Each entity is created only if it does
 * not already exist, making this runner idempotent and safe for repeated restarts.
 */
@Component
public class ApplicationDataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(ApplicationDataSeeder.class);

    private final SeedProperties seedProperties;
    private final TenantWorkspaceRepository tenantWorkspaceRepository;
    private final UserAccountRepository userAccountRepository;
    private final ConfidencePolicyRepository confidencePolicyRepository;
    private final PasswordEncoder passwordEncoder;

    public ApplicationDataSeeder(SeedProperties seedProperties,
                                 TenantWorkspaceRepository tenantWorkspaceRepository,
                                 UserAccountRepository userAccountRepository,
                                 ConfidencePolicyRepository confidencePolicyRepository,
                                 PasswordEncoder passwordEncoder) {
        this.seedProperties = seedProperties;
        this.tenantWorkspaceRepository = tenantWorkspaceRepository;
        this.userAccountRepository = userAccountRepository;
        this.confidencePolicyRepository = confidencePolicyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!seedProperties.enabled()) {
            log.info("Seed data provisioning is disabled (chimera.seed.enabled=false)");
            return;
        }

        log.info("Checking default seed data...");

        TenantWorkspace tenant = ensureDefaultTenant();
        ensureDefaultConfidencePolicy(tenant);
        ensureDefaultAdmin(tenant);

        log.info("Seed data check complete");
    }

    private TenantWorkspace ensureDefaultTenant() {
        SeedProperties.TenantSeed cfg = seedProperties.defaultTenant();
        return tenantWorkspaceRepository.findBySlug(cfg.slug())
                .map(existing -> {
                    log.info("Default tenant '{}' already exists (id={})", cfg.slug(), existing.getId());
                    return existing;
                })
                .orElseGet(() -> {
                    TenantWorkspace created = tenantWorkspaceRepository.save(
                            new TenantWorkspace(cfg.slug(), cfg.displayName()));
                    log.info("Created default tenant '{}' (id={})", cfg.slug(), created.getId());
                    return created;
                });
    }

    private void ensureDefaultConfidencePolicy(TenantWorkspace tenant) {
        SeedProperties.PolicySeed cfg = seedProperties.defaultPolicy();
        boolean exists = confidencePolicyRepository
                .findByTenantWorkspaceIdAndCampaignIdIsNull(tenant.getId())
                .isPresent();
        if (exists) {
            log.info("Default confidence policy for tenant '{}' already exists", tenant.getSlug());
            return;
        }

        ConfidencePolicy policy = new ConfidencePolicy(
                tenant.getId(),
                cfg.autoApproveThreshold(),
                cfg.reviewThreshold(),
                cfg.sensitiveTopics());
        ConfidencePolicy saved = confidencePolicyRepository.save(policy);

        tenant.setDefaultConfidencePolicyId(saved.getId());
        tenantWorkspaceRepository.save(tenant);

        log.info("Created default confidence policy (id={}) for tenant '{}'",
                saved.getId(), tenant.getSlug());
    }

    private void ensureDefaultAdmin(TenantWorkspace tenant) {
        SeedProperties.AdminSeed cfg = seedProperties.defaultAdmin();
        boolean exists = userAccountRepository
                .findByTenantWorkspaceIdAndEmail(tenant.getId(), cfg.email())
                .isPresent();
        if (exists) {
            log.info("Default admin '{}' already exists in tenant '{}'",
                    cfg.email(), tenant.getSlug());
            return;
        }

        String encodedPassword = passwordEncoder.encode(cfg.password());
        UserAccount admin = UserAccount.createLocal(
                tenant.getId(), cfg.email(), encodedPassword, cfg.roles());
        userAccountRepository.save(admin);

        log.info("Created default admin '{}' with roles [{}] in tenant '{}'",
                cfg.email(), cfg.roles(), tenant.getSlug());
    }
}
