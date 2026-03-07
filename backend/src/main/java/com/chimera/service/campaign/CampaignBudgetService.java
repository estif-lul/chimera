package com.chimera.service.campaign;

import com.chimera.domain.model.wallet.Wallet;
import com.chimera.service.wallet.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Budget enforcement hooks invoked during campaign task execution.
 */
@Service
public class CampaignBudgetService {

    private static final Logger log = LoggerFactory.getLogger(CampaignBudgetService.class);

    private final WalletService walletService;

    public CampaignBudgetService(WalletService walletService) {
        this.walletService = walletService;
    }

    /**
     * Check whether the agent's wallet has sufficient balance for the estimated cost.
     */
    public boolean hasSufficientBudget(UUID agentId, BigDecimal estimatedCost) {
        Wallet wallet = walletService.getByAgent(agentId);
        boolean sufficient = wallet.getAvailableBalance().compareTo(estimatedCost) >= 0;
        if (!sufficient) {
            log.warn("Insufficient budget for agent {}: required={} available={}",
                    agentId, estimatedCost, wallet.getAvailableBalance());
        }
        return sufficient;
    }
}
