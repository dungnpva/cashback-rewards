package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "merchant_rate_change")
class MerchantRateChangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "cashback_rate", nullable = false)
    private BigDecimal cashbackRate;

    @Column(name = "effective_from", nullable = false)
    private Instant effectiveFrom;

    protected MerchantRateChangeEntity() {
    }

    MerchantRateChangeEntity(String merchantName, BigDecimal cashbackRate, Instant effectiveFrom) {
        this.merchantName = merchantName;
        this.cashbackRate = cashbackRate;
        this.effectiveFrom = effectiveFrom;
    }

    BigDecimal getCashbackRate() {
        return cashbackRate;
    }

    Instant getEffectiveFrom() {
        return effectiveFrom;
    }
}
