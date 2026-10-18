package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.RecordPurchaseUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRecordRepository;
import com.serenitydojo.cashback_rewards.application.port.out.MerchantRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import com.serenitydojo.cashback_rewards.domain.model.Merchant;
import com.serenitydojo.cashback_rewards.domain.service.CashbackCalculator;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RecordPurchaseService implements RecordPurchaseUseCase {

    private final MerchantRepository merchantRepository;
    private final CashbackRecordRepository cashbackRecordRepository;
    private final CashbackCalculator cashbackCalculator = new CashbackCalculator();

    public RecordPurchaseService(MerchantRepository merchantRepository,
                                 CashbackRecordRepository cashbackRecordRepository) {
        this.merchantRepository = merchantRepository;
        this.cashbackRecordRepository = cashbackRecordRepository;
    }

    @Override
    public Optional<UUID> recordPurchase(String customerId, String merchantName, BigDecimal amount,
                                         Instant purchasedAt) {
        Instant moment = purchasedAt != null ? purchasedAt : Instant.now();
        return merchantRepository.findByName(merchantName)
                .map(partner -> recordCashback(customerId, partner.asOf(moment), amount));
    }

    private UUID recordCashback(String customerId, Merchant partner, BigDecimal amount) {
        BigDecimal cashback = cashbackCalculator.calculate(partner, amount);
        CashbackRecord cashbackRecord = CashbackRecord.earned(customerId, partner.name(), cashback);
        cashbackRecordRepository.save(cashbackRecord);
        return cashbackRecord.purchaseId();
    }
}
