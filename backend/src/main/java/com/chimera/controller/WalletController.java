package com.chimera.controller;

import com.chimera.controller.dto.TransactionRequestInput;
import com.chimera.controller.dto.TransactionRequestView;
import com.chimera.controller.dto.WalletView;
import com.chimera.domain.model.wallet.Wallet;
import com.chimera.service.wallet.TransactionRequestService;
import com.chimera.service.wallet.WalletService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for wallet lookup, balance queries, and transaction requests.
 */
@RestController
@RequestMapping("/api/v1/wallets")
public class WalletController {

    private final WalletService walletService;
    private final TransactionRequestService txService;

    public WalletController(WalletService walletService,
                            TransactionRequestService txService) {
        this.walletService = walletService;
        this.txService = txService;
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<WalletView> getByAgent(@PathVariable UUID agentId) {
        Wallet wallet = walletService.getByAgent(agentId);
        return ResponseEntity.ok(walletService.toView(wallet));
    }

    @GetMapping("/{walletId}")
    public ResponseEntity<WalletView> getById(@PathVariable UUID walletId) {
        Wallet wallet = walletService.getById(walletId);
        return ResponseEntity.ok(walletService.toView(wallet));
    }

    @GetMapping("/{walletId}/transactions")
    public ResponseEntity<List<TransactionRequestView>> listTransactions(@PathVariable UUID walletId) {
        List<TransactionRequestView> views = txService.listForWallet(walletId)
                .stream().map(txService::toView).toList();
        return ResponseEntity.ok(views);
    }

    @PostMapping("/{walletId}/transactions")
    public ResponseEntity<TransactionRequestView> submitTransaction(
            @PathVariable UUID walletId,
            @Valid @RequestBody TransactionRequestInput input) {
        var tx = txService.submit(walletId, input.direction(),
                new BigDecimal(input.amount()), input.assetCode(), input.counterparty());
        return ResponseEntity
                .created(URI.create("/api/v1/wallets/" + walletId + "/transactions/" + tx.getId()))
                .body(txService.toView(tx));
    }
}
