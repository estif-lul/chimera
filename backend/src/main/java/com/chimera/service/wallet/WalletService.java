package com.chimera.service.wallet;

import com.chimera.controller.dto.WalletView;
import com.chimera.domain.model.wallet.Wallet;
import com.chimera.domain.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Manages wallet lifecycle, balance queries, and credit/debit operations.
 */
@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional
    public Wallet createWallet(UUID tenantWorkspaceId, UUID agentId,
                               String providerType, String walletAddress) {
        Wallet wallet = new Wallet(tenantWorkspaceId, agentId, providerType, walletAddress);
        wallet = walletRepository.save(wallet);
        log.info("Wallet created: id={} agent={}", wallet.getId(), agentId);
        return wallet;
    }

    @Transactional(readOnly = true)
    public Wallet getByAgent(UUID agentId) {
        return walletRepository.findByChimeraAgentId(agentId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found for agent: " + agentId));
    }

    @Transactional(readOnly = true)
    public Wallet getById(UUID walletId) {
        return walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));
    }

    @Transactional
    public Wallet credit(UUID walletId, BigDecimal amount) {
        Wallet wallet = getById(walletId);
        wallet.credit(amount);
        wallet = walletRepository.save(wallet);
        log.info("Wallet credited: id={} amount={}", walletId, amount);
        return wallet;
    }

    @Transactional
    public Wallet debit(UUID walletId, BigDecimal amount) {
        Wallet wallet = getById(walletId);
        wallet.debit(amount);
        wallet = walletRepository.save(wallet);
        log.info("Wallet debited: id={} amount={}", walletId, amount);
        return wallet;
    }

    public WalletView toView(Wallet w) {
        return new WalletView(w.getId(), w.getChimeraAgentId(), w.getStatus(),
                w.getAvailableBalance().toPlainString(),
                w.getDailySpendLimit() != null ? w.getDailySpendLimit().toPlainString() : null,
                w.getPerTransactionLimit() != null ? w.getPerTransactionLimit().toPlainString() : null);
    }
}
