package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record CashbackRecord(UUID purchaseId, String customerId, String merchantName, BigDecimal cashback,
                             boolean settled) {

    public static CashbackRecord earned(String customerId, String merchantName, BigDecimal cashback) {
        return new CashbackRecord(UUID.randomUUID(), customerId, merchantName, cashback, false);
    }

    public CashbackRecord settle() {
        return new CashbackRecord(purchaseId, customerId, merchantName, cashback, true);
    }
}
