package com.chimera.domain.repository;

import com.chimera.domain.model.wallet.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for agent-owned wallets.
 */
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findByChimeraAgentId(UUID chimeraAgentId);
}
