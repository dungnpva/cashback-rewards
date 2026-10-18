package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;

public record RewardsBalance(BigDecimal available, BigDecimal pending) {

    public static RewardsBalance of(List<CashbackRecord> cashbackRecords) {
        return new RewardsBalance(
                totalCashback(cashbackRecords, CashbackRecord::settled),
                totalCashback(cashbackRecords, cashbackRecord -> !cashbackRecord.settled()));
    }

    private static BigDecimal totalCashback(List<CashbackRecord> cashbackRecords, Predicate<CashbackRecord> filter) {
        return cashbackRecords.stream()
                .filter(filter)
                .map(CashbackRecord::cashback)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
