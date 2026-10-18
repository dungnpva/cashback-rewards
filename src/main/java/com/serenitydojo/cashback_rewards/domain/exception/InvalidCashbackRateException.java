package com.serenitydojo.cashback_rewards.domain.exception;

import java.math.BigDecimal;

public class InvalidCashbackRateException extends RuntimeException {

    public InvalidCashbackRateException(BigDecimal cashbackRate) {
        super("Cashback rate must be between 0% and 25% but was " + cashbackRate.movePointRight(2).toPlainString() + "%");
    }
}
