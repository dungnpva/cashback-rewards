package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.CancelPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRecordRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Accepts cancellation notifications. Cashback is deliberately left pending:
 * adjusting it is a manual admin task for now.
 */
@Service
public class CancelPurchaseService implements CancelPurchaseUseCase {

    private final CashbackRecordRepository cashbackRecordRepository;

    public CancelPurchaseService(CashbackRecordRepository cashbackRecordRepository) {
        this.cashbackRecordRepository = cashbackRecordRepository;
    }

    @Override
    public void cancelPurchase(UUID purchaseId) {
        cashbackRecordRepository.findByPurchaseId(purchaseId).orElseThrow();
    }
}
