package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cashback_record")
class CashbackRecordEntity {

    @Id
    @Column(name = "purchase_id")
    private UUID purchaseId;

    @Column(name = "customer_id", nullable = false)
    private String customerId;

    @Column(name = "merchant_name", nullable = false)
    private String merchantName;

    @Column(name = "cashback_amount", nullable = false)
    private BigDecimal cashbackAmount;

    @Column(name = "settled", nullable = false)
    private boolean settled;

    protected CashbackRecordEntity() {
    }

    CashbackRecordEntity(UUID purchaseId, String customerId, String merchantName, BigDecimal cashbackAmount,
                         boolean settled) {
        this.purchaseId = purchaseId;
        this.customerId = customerId;
        this.merchantName = merchantName;
        this.cashbackAmount = cashbackAmount;
        this.settled = settled;
    }

    UUID getPurchaseId() {
        return purchaseId;
    }

    String getCustomerId() {
        return customerId;
    }

    String getMerchantName() {
        return merchantName;
    }

    BigDecimal getCashbackAmount() {
        return cashbackAmount;
    }

    boolean isSettled() {
        return settled;
    }
}
