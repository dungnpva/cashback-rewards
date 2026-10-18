package com.serenitydojo.cashback_rewards.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface MerchantRateChangeJpaRepository extends JpaRepository<MerchantRateChangeEntity, Long> {

    List<MerchantRateChangeEntity> findByMerchantName(String merchantName);
}
