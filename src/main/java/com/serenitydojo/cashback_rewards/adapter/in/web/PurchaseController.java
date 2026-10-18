package com.serenitydojo.cashback_rewards.adapter.in.web;

import com.serenitydojo.cashback_rewards.application.port.in.CancelPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.in.SettlePurchaseUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/purchases")
class PurchaseController {

    private final RecordPurchaseUseCase recordPurchaseUseCase;
    private final SettlePurchaseUseCase settlePurchaseUseCase;
    private final CancelPurchaseUseCase cancelPurchaseUseCase;

    PurchaseController(RecordPurchaseUseCase recordPurchaseUseCase, SettlePurchaseUseCase settlePurchaseUseCase,
                       CancelPurchaseUseCase cancelPurchaseUseCase) {
        this.recordPurchaseUseCase = recordPurchaseUseCase;
        this.settlePurchaseUseCase = settlePurchaseUseCase;
        this.cancelPurchaseUseCase = cancelPurchaseUseCase;
    }

    @PostMapping
    ResponseEntity<RecordPurchaseResponse> record(@Valid @RequestBody RecordPurchaseRequest request) {
        return recordPurchaseUseCase.recordPurchase(
                        request.customerId(), request.merchantName(), request.amount(), request.purchasedAt())
                .map(purchaseId -> ResponseEntity.status(HttpStatus.CREATED).body(new RecordPurchaseResponse(purchaseId)))
                .orElseGet(() -> ResponseEntity.accepted().build());
    }

    @PostMapping("/{purchaseId}/settlement")
    void settle(@PathVariable UUID purchaseId) {
        settlePurchaseUseCase.settlePurchase(purchaseId);
    }

    @PostMapping("/{purchaseId}/cancellation")
    void cancel(@PathVariable UUID purchaseId) {
        cancelPurchaseUseCase.cancelPurchase(purchaseId);
    }

    record RecordPurchaseRequest(String customerId, String merchantName, BigDecimal amount, Instant purchasedAt) {
    }

    record RecordPurchaseResponse(UUID purchaseId) {
    }
}
