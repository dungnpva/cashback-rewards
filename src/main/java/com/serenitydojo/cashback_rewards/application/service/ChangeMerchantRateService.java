package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.ChangeMerchantRateUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.RateChange;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class ChangeMerchantRateService implements ChangeMerchantRateUseCase {

    private final MerchantRepository merchantRepository;

    public ChangeMerchantRateService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void changeRate(String merchantName, BigDecimal cashbackRate, Instant effectiveFrom) {
        Merchant merchant = merchantRepository.findByName(merchantName).orElseThrow();
        merchantRepository.addRateChange(merchant.name(), new RateChange(cashbackRate, effectiveFrom));
    }
}
