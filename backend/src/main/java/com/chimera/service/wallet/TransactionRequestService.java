package com.chimera.service.wallet;

import com.chimera.controller.dto.TransactionRequestView;
import com.chimera.domain.model.wallet.TransactionRequest;
import com.chimera.domain.repository.TransactionRequestRepository;
import com.chimera.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Creates, evaluates, and executes transaction requests against agent wallets.
 */
@Service
public class TransactionRequestService {

    private static final Logger log = LoggerFactory.getLogger(TransactionRequestService.class);

    private final TransactionRequestRepository txRepository;
    private final WalletService walletService;
    private final WalletPolicyService policyService;
    private final AuditService auditService;

    public TransactionRequestService(TransactionRequestRepository txRepository,
                                     WalletService walletService,
                                     WalletPolicyService policyService,
                                     AuditService auditService) {
        this.txRepository = txRepository;
        this.walletService = walletService;
        this.policyService = policyService;
        this.auditService = auditService;
    }

    /**
     * Submit a transaction request. Inbound requests execute immediately.
     * Outbound requests pass through policy evaluation first.
     */
    @Transactional
    public TransactionRequest submit(UUID walletId, String direction,
                                     BigDecimal amount, String assetCode, String counterparty) {
        TransactionRequest tx = new TransactionRequest(walletId, direction, amount, assetCode, counterparty);

        if ("inbound".equals(direction)) {
            tx.approve();
            tx.execute();
            walletService.credit(walletId, amount);
            tx = txRepository.save(tx);
            log.info("Inbound transaction executed: id={} amount={}", tx.getId(), amount);
        } else {
            List<String> flags = policyService.evaluate(walletId, amount);
            flags.forEach(tx::addPolicyFlag);

            if (flags.isEmpty()) {
                tx.approve();
                tx.execute();
                walletService.debit(walletId, amount);
                log.info("Outbound transaction auto-approved: id={}", tx.getId());
            } else {
                log.info("Outbound transaction flagged for review: id={} flags={}", tx.getId(), flags);
            }
            tx = txRepository.save(tx);
        }

        auditService.record(null, "system", "transaction-submit",
                "wallet.transaction_request", "transaction_request", tx.getId().toString(),
                Map.of("direction", direction, "amount", amount.toPlainString(), "flags", tx.getPolicyFlags().toString()),
                null);

        return tx;
    }

    @Transactional(readOnly = true)
    public List<TransactionRequest> listForWallet(UUID walletId) {
        return txRepository.findByWalletIdOrderByCreatedAtDesc(walletId);
    }

    public TransactionRequestView toView(TransactionRequest tx) {
        return new TransactionRequestView(tx.getId(), tx.getWalletId(), tx.getDirection(),
                tx.getAmount().toPlainString(), tx.getAssetCode(), tx.getStatus(), tx.getPolicyFlags());
    }
}
