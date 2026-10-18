package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import com.serenitydojo.cashback_rewards.application.port.out.CashbackRecordRepository;
import com.serenitydojo.cashback_rewards.domain.model.CashbackRecord;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
class JpaCashbackRecordRepository implements CashbackRecordRepository {

    private final CashbackRecordJpaRepository cashbackRecordJpaRepository;

    JpaCashbackRecordRepository(CashbackRecordJpaRepository cashbackRecordJpaRepository) {
        this.cashbackRecordJpaRepository = cashbackRecordJpaRepository;
    }

    @Override
    public void save(CashbackRecord cashbackRecord) {
        cashbackRecordJpaRepository.save(new CashbackRecordEntity(
                cashbackRecord.purchaseId(), cashbackRecord.customerId(), cashbackRecord.merchantName(),
                cashbackRecord.cashback(), cashbackRecord.settled()));
    }

    @Override
    public Optional<CashbackRecord> findByPurchaseId(UUID purchaseId) {
        return cashbackRecordJpaRepository.findById(purchaseId).map(this::toDomain);
    }

    @Override
    public List<CashbackRecord> findByCustomerId(String customerId) {
        return cashbackRecordJpaRepository.findByCustomerId(customerId).stream()
                .map(this::toDomain)
                .toList();
    }

    private CashbackRecord toDomain(CashbackRecordEntity entity) {
        return new CashbackRecord(entity.getPurchaseId(), entity.getCustomerId(), entity.getMerchantName(),
                entity.getCashbackAmount(), entity.isSettled());
    }
}
