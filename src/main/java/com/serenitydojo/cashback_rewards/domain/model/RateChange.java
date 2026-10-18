package com.serenitydojo.cashback_rewards.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

public record RateChange(BigDecimal cashbackRate, Instant effectiveFrom) {

    public RateChange {
        Merchant.requireAllowedRate(cashbackRate);
    }

    boolean isEffectiveAt(Instant moment) {
        return !effectiveFrom.isAfter(moment);
    }
}
