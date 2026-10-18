package com.serenitydojo.cashback_rewards.application.service;

import com.serenitydojo.cashback_rewards.application.port.in.ViewRewardsBalanceUseCase;
import com.serenitydojo.cashback_rewards.application.port.out.CashbackRecordRepository;
import com.serenitydojo.cashback_rewards.domain.model.RewardsBalance;
import org.springframework.stereotype.Service;

@Service
public class ViewRewardsBalanceService implements ViewRewardsBalanceUseCase {

    private final CashbackRecordRepository cashbackRecordRepository;

    public ViewRewardsBalanceService(CashbackRecordRepository cashbackRecordRepository) {
        this.cashbackRecordRepository = cashbackRecordRepository;
    }

    @Override
    public RewardsBalance rewardsBalance(String customerId) {
        return RewardsBalance.of(cashbackRecordRepository.findByCustomerId(customerId));
    }
}
