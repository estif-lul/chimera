package com.chimera.controller.dto;

import java.util.UUID;

/**
 * Wallet details projection.
 */
public record WalletView(
        UUID id,
        UUID chimeraAgentId,
        String status,
        String availableBalance,
        String dailySpendLimit,
        String perTransactionLimit
) {}
