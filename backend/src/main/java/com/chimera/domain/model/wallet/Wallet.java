package com.chimera.domain.model.wallet;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Agent-owned custodial wallet with spend-limit governance.
 */
@Entity
@Table(name = "wallet")
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "tenant_workspace_id", nullable = false)
    private UUID tenantWorkspaceId;

    @Column(name = "chimera_agent_id", nullable = false)
    private UUID chimeraAgentId;

    @Column(name = "provider_type", nullable = false, length = 64)
    private String providerType;

    @Column(name = "wallet_address", nullable = false, length = 256)
    private String walletAddress;

    @Column(nullable = false, length = 32)
    private String status = "active";

    @Column(name = "available_balance", nullable = false, precision = 20, scale = 8)
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "daily_spend_limit", precision = 20, scale = 8)
    private BigDecimal dailySpendLimit;

    @Column(name = "per_transaction_limit", precision = 20, scale = 8)
    private BigDecimal perTransactionLimit;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt = Instant.now();

    protected Wallet() {}

    public Wallet(UUID tenantWorkspaceId, UUID chimeraAgentId,
                  String providerType, String walletAddress) {
        this.tenantWorkspaceId = tenantWorkspaceId;
        this.chimeraAgentId = chimeraAgentId;
        this.providerType = providerType;
        this.walletAddress = walletAddress;
    }

    public UUID getId() { return id; }
    public UUID getTenantWorkspaceId() { return tenantWorkspaceId; }
    public UUID getChimeraAgentId() { return chimeraAgentId; }
    public String getProviderType() { return providerType; }
    public String getWalletAddress() { return walletAddress; }
    public String getStatus() { return status; }
    public BigDecimal getAvailableBalance() { return availableBalance; }
    public BigDecimal getDailySpendLimit() { return dailySpendLimit; }
    public BigDecimal getPerTransactionLimit() { return perTransactionLimit; }
    public Instant getUpdatedAt() { return updatedAt; }

    public void setDailySpendLimit(BigDecimal limit) { this.dailySpendLimit = limit; }
    public void setPerTransactionLimit(BigDecimal limit) { this.perTransactionLimit = limit; }

    /**
     * Credit the wallet (inbound payment).
     */
    public void credit(BigDecimal amount) {
        this.availableBalance = this.availableBalance.add(amount);
        this.updatedAt = Instant.now();
    }

    /**
     * Debit the wallet (outbound transfer). Caller must verify policy first.
     */
    public void debit(BigDecimal amount) {
        if (amount.compareTo(this.availableBalance) > 0) {
            throw new IllegalStateException("Insufficient balance");
        }
        this.availableBalance = this.availableBalance.subtract(amount);
        this.updatedAt = Instant.now();
    }

    public void restrict() { this.status = "restricted"; this.updatedAt = Instant.now(); }
    public void suspend() { this.status = "suspended"; this.updatedAt = Instant.now(); }
}
