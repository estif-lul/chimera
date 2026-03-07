package com.chimera.service.wallet;

import com.chimera.domain.model.wallet.Wallet;
import com.chimera.domain.repository.TransactionRequestRepository;
import com.chimera.domain.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Evaluates spend policy: daily-limit, per-transaction, and anomaly checks
 * for outbound wallet transactions.
 */
@Service
public class WalletPolicyService {

    private static final Logger log = LoggerFactory.getLogger(WalletPolicyService.class);

    private final WalletRepository walletRepository;
    private final TransactionRequestRepository txRepository;

    public WalletPolicyService(WalletRepository walletRepository,
                               TransactionRequestRepository txRepository) {
        this.walletRepository = walletRepository;
        this.txRepository = txRepository;
    }

    /**
     * Evaluate policy for an outbound transaction.
     *
     * @return list of policy flags (empty = auto-approve)
     */
    public List<String> evaluate(UUID walletId, BigDecimal amount) {
        Wallet wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new IllegalArgumentException("Wallet not found: " + walletId));

        List<String> flags = new ArrayList<>();

        // Per-transaction limit check
        if (wallet.getPerTransactionLimit() != null
                && amount.compareTo(wallet.getPerTransactionLimit()) > 0) {
            flags.add("per_transaction_limit_exceeded");
        }

        // Daily spend limit check
        if (wallet.getDailySpendLimit() != null) {
            Instant startOfDay = LocalDate.now(ZoneOffset.UTC)
                    .atStartOfDay(ZoneOffset.UTC).toInstant();
            BigDecimal spentToday = txRepository.sumOutboundSince(walletId, startOfDay);
            if (spentToday.add(amount).compareTo(wallet.getDailySpendLimit()) > 0) {
                flags.add("daily_limit_exceeded");
            }
        }

        // Balance sufficiency
        if (amount.compareTo(wallet.getAvailableBalance()) > 0) {
            flags.add("insufficient_balance");
        }

        if (!flags.isEmpty()) {
            log.warn("Policy flags raised for wallet {} amount {}: {}", walletId, amount, flags);
        }
        return flags;
    }
}
