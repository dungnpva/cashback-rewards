package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CashbackRecordRepository {

    void save(CashbackRecord cashbackRecord);

    Optional<CashbackRecord> findByPurchaseId(UUID purchaseId);

    List<CashbackRecord> findByCustomerId(String customerId);
}
