package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.SettlePurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRecordRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SettlePurchaseService implements SettlePurchaseUseCase {

    private final CashbackRecordRepository cashbackRecordRepository;

    public SettlePurchaseService(CashbackRecordRepository cashbackRecordRepository) {
        this.cashbackRecordRepository = cashbackRecordRepository;
    }

    @Override
    public void settlePurchase(UUID purchaseId) {
        CashbackRecord cashbackRecord = cashbackRecordRepository.findByPurchaseId(purchaseId).orElseThrow();
        cashbackRecordRepository.save(cashbackRecord.settle());
    }
}
