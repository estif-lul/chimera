package com.chimera.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Request body for creating a transaction request.
 */
public record TransactionRequestInput(
        @NotNull String direction,
        @NotBlank String amount,
        @NotBlank String assetCode,
        String counterparty,
        String rationale
) {}
