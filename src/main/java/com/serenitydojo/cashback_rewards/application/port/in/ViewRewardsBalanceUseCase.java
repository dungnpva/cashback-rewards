package com.serenitydojo.cashback_rewards.application.port.in;

import com.serenitydojo.cashback_rewards.domain.model.RewardsBalance;

public interface ViewRewardsBalanceUseCase {

    RewardsBalance rewardsBalance(String customerId);
}
