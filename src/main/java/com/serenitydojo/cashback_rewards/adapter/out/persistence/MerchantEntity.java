package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "merchant")
class MerchantEntity {

    @Id
    private String name;

    @Column(name = "cashback_rate", nullable = false)
    private BigDecimal cashbackRate;

    protected MerchantEntity() {
    }

    MerchantEntity(String name, BigDecimal cashbackRate) {
        this.name = name;
        this.cashbackRate = cashbackRate;
    }

    String getName() {
        return name;
    }

    BigDecimal getCashbackRate() {
        return cashbackRate;
    }
}
