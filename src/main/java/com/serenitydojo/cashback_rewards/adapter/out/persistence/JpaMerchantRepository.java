package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.model.RateChange;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
class JpaMerchantRepository implements MerchantRepository {

    private final MerchantJpaRepository merchantJpaRepository;
    private final MerchantRateChangeJpaRepository merchantRateChangeJpaRepository;

    JpaMerchantRepository(MerchantJpaRepository merchantJpaRepository,
                          MerchantRateChangeJpaRepository merchantRateChangeJpaRepository) {
        this.merchantJpaRepository = merchantJpaRepository;
        this.merchantRateChangeJpaRepository = merchantRateChangeJpaRepository;
    }

    @Override
    public void save(Merchant merchant) {
        merchantJpaRepository.save(new MerchantEntity(merchant.name(), merchant.cashbackRate()));
    }

    @Override
    public void addRateChange(String merchantName, RateChange rateChange) {
        merchantRateChangeJpaRepository.save(new MerchantRateChangeEntity(
                merchantName, rateChange.cashbackRate(), rateChange.effectiveFrom()));
    }

    @Override
    public Optional<Merchant> findByName(String name) {
        return merchantJpaRepository.findById(name)
                .map(entity -> new Merchant(entity.getName(), entity.getCashbackRate(), rateChangesOf(name)));
    }

    private List<RateChange> rateChangesOf(String merchantName) {
        return merchantRateChangeJpaRepository.findByMerchantName(merchantName).stream()
                .map(entity -> new RateChange(entity.getCashbackRate(), entity.getEffectiveFrom()))
                .toList();
    }
}
