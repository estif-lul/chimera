package com.chimera.domain.repository;

import com.chimera.domain.model.wallet.TransactionRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repository for transaction requests against agent wallets.
 */
public interface TransactionRequestRepository extends JpaRepository<TransactionRequest, UUID> {

    List<TransactionRequest> findByWalletIdOrderByCreatedAtDesc(UUID walletId);

    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM TransactionRequest t " +
           "WHERE t.walletId = :walletId AND t.direction = 'outbound' " +
           "AND t.status IN ('approved', 'executed') " +
           "AND t.createdAt >= :since")
    BigDecimal sumOutboundSince(UUID walletId, Instant since);
}
