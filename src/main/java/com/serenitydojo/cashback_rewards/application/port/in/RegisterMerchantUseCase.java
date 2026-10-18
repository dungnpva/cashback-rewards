package com.serenitydojo.cashback_rewards.application.port.in;

import java.math.BigDecimal;

public interface RegisterMerchantUseCase {

    void registerMerchant(String name, BigDecimal cashbackRate);
}
