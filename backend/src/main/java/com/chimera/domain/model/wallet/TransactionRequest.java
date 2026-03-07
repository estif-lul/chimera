package com.chimera.domain.model.wallet;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * A pending or executed financial transaction against an agent wallet.
 */
@Entity
@Table(name = "transaction_request")
public class TransactionRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "wallet_id", nullable = false)
    private UUID walletId;

    @Column(name = "task_id")
    private UUID taskId;

    @Column(nullable = false, length = 16)
    private String direction;

    @Column(nullable = false, precision = 20, scale = 8)
    private BigDecimal amount;

    @Column(name = "asset_code", nullable = false, length = 16)
    private String assetCode;

    @Column(length = 256)
    private String counterparty;

    @Column(nullable = false, length = 16)
    private String status = "pending";

    @Column(name = "policy_flags", columnDefinition = "TEXT[]")
    private List<String> policyFlags = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "executed_at")
    private Instant executedAt;

    protected TransactionRequest() {}

    public TransactionRequest(UUID walletId, String direction,
                              BigDecimal amount, String assetCode, String counterparty) {
        this.walletId = walletId;
        this.direction = direction;
        this.amount = amount;
        this.assetCode = assetCode;
        this.counterparty = counterparty;
    }

    public UUID getId() { return id; }
    public UUID getWalletId() { return walletId; }
    public UUID getTaskId() { return taskId; }
    public String getDirection() { return direction; }
    public BigDecimal getAmount() { return amount; }
    public String getAssetCode() { return assetCode; }
    public String getCounterparty() { return counterparty; }
    public String getStatus() { return status; }
    public List<String> getPolicyFlags() { return policyFlags; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getExecutedAt() { return executedAt; }

    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public void addPolicyFlag(String flag) { this.policyFlags.add(flag); }

    public void approve() { this.status = "approved"; }

    public void reject() { this.status = "rejected"; }

    public void execute() {
        this.status = "executed";
        this.executedAt = Instant.now();
    }

    public void fail() { this.status = "failed"; }
}
