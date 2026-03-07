package com.chimera.controller.dto;

import java.util.List;
import java.util.UUID;

/**
 * Transaction request projection.
 */
public record TransactionRequestView(
        UUID id,
        UUID walletId,
        String direction,
        String amount,
        String assetCode,
        String status,
        List<String> policyFlags
) {}
