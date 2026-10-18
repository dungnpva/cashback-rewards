package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface CashbackRecordJpaRepository extends JpaRepository<CashbackRecordEntity, UUID> {

    List<CashbackRecordEntity> findByCustomerId(String customerId);
}
