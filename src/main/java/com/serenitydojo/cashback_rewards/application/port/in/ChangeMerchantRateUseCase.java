package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;
import java.time.Instant;

public interface ChangeMerchantRateUseCase {

    void changeRate(String merchantName, BigDecimal cashbackRate, Instant effectiveFrom);
}
