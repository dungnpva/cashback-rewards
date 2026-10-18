package com.serenitydojo.cashback_rewards.application.port.out;

import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.RateChange;

import java.util.Optional;

public interface MerchantRepository {

    void save(Merchant merchant);

    void addRateChange(String merchantName, RateChange rateChange);

    Optional<Merchant> findByName(String name);
}
