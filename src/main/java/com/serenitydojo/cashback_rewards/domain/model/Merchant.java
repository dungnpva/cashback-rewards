package com.serenitydojo.cashback_rewards.domain.model;

import com.serenitydojo.cashback_rewards.domain.exception.InvalidCashbackRateException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;

/**
 * @param cashbackRate the rate given at registration; it applies until the first rate change takes effect
 */
public record Merchant(String name, BigDecimal cashbackRate, List<RateChange> rateChanges) {

    private static final BigDecimal MINIMUM_CASHBACK_RATE = BigDecimal.ZERO;
    private static final BigDecimal MAXIMUM_CASHBACK_RATE = new BigDecimal("0.25");

    public Merchant {
        requireAllowedRate(cashbackRate);
        rateChanges = List.copyOf(rateChanges);
    }

    public Merchant(String name, BigDecimal cashbackRate) {
        this(name, cashbackRate, List.of());
    }

    /**
     * The merchant as it was at the given moment: carrying the rate effective then.
     */
    public Merchant asOf(Instant moment) {
        BigDecimal effectiveRate = rateChanges.stream()
                .filter(rateChange -> rateChange.isEffectiveAt(moment))
                .max(Comparator.comparing(RateChange::effectiveFrom))
                .map(RateChange::cashbackRate)
                .orElse(cashbackRate);
        return new Merchant(name, effectiveRate, rateChanges);
    }

    static void requireAllowedRate(BigDecimal cashbackRate) {
        if (cashbackRate.compareTo(MINIMUM_CASHBACK_RATE) < 0 || cashbackRate.compareTo(MAXIMUM_CASHBACK_RATE) > 0) {
            throw new InvalidCashbackRateException(cashbackRate);
        }
    }
}
