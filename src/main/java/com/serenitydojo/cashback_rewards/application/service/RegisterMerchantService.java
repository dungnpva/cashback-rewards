package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.RegisterMerchantUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.exception.MerchantAlreadyRegisteredException;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class RegisterMerchantService implements RegisterMerchantUseCase {

    private final MerchantRepository merchantRepository;

    public RegisterMerchantService(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public void registerMerchant(String name, BigDecimal cashbackRate) {
        if (merchantRepository.findByName(name).isPresent()) {
            throw new MerchantAlreadyRegisteredException(name);
        }
        merchantRepository.save(new Merchant(name, cashbackRate));
    }
}
