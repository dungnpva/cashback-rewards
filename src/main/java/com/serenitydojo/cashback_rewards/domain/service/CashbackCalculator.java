package com.serenitydojo.cashback_rewards.domain.service;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CashbackCalculator {

    private static final int CASHBACK_SCALE = 2;
    private static final BigDecimal MINIMUM_PURCHASE_AMOUNT = new BigDecimal("5.00");
    private static final BigDecimal NO_CASHBACK = BigDecimal.ZERO.setScale(CASHBACK_SCALE);

    public BigDecimal calculate(Merchant merchant, BigDecimal purchaseAmount) {
        if (purchaseAmount.compareTo(MINIMUM_PURCHASE_AMOUNT) < 0) {
            return NO_CASHBACK;
        }
        return purchaseAmount.multiply(merchant.cashbackRate()).setScale(CASHBACK_SCALE, RoundingMode.DOWN);
    }
}
