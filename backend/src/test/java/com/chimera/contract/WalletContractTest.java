package com.chimera.contract;

import com.chimera.controller.dto.TransactionRequestInput;
import com.chimera.controller.dto.TransactionRequestView;
import com.chimera.controller.dto.WalletView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Contract tests verifying Wallet API DTO shapes match the OpenAPI specification.
 */
class WalletContractTest {

    @Test
    @DisplayName("WalletView carries expected fields")
    void walletView_hasExpectedShape() {
        var view = new WalletView(
                UUID.randomUUID(), UUID.randomUUID(), "active",
                "1000.00000000", "500.00000000", "100.00000000");
        assertEquals("active", view.status());
        assertNotNull(view.availableBalance());
    }

    @Test
    @DisplayName("TransactionRequestInput validates direction")
    void transactionRequestInput_hasDirectionAndAmount() {
        var input = new TransactionRequestInput(
                "outbound", "50.00", "USDC", "merchant-addr", "ad spend");
        assertEquals("outbound", input.direction());
        assertEquals("50.00", input.amount());
    }

    @Test
    @DisplayName("TransactionRequestView carries policy flags list")
    void transactionRequestView_hasPolicyFlags() {
        var view = new TransactionRequestView(
                UUID.randomUUID(), UUID.randomUUID(), "outbound",
                "50.00", "USDC", "pending", List.of("daily_limit_warning"));
        assertFalse(view.policyFlags().isEmpty());
    }
}
